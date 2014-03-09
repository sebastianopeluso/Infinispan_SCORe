package org.infinispan.interceptors;

import org.infinispan.CacheException;
import org.infinispan.commands.CommandsFactory;
import org.infinispan.commands.read.GetKeyValueCommand;
import org.infinispan.commands.tx.*;
import org.infinispan.container.key.ContextAwareKey;
import org.infinispan.context.InvocationContext;
import org.infinispan.context.InvocationContextContainer;
import org.infinispan.context.impl.LocalTxInvocationContext;
import org.infinispan.context.impl.TxInvocationContext;
import org.infinispan.distribution.DistributionManager;
import org.infinispan.factories.annotations.Inject;
import org.infinispan.factories.annotations.Start;
import org.infinispan.jmx.annotations.MBean;
import org.infinispan.jmx.annotations.ManagedAttribute;
import org.infinispan.jmx.annotations.ManagedOperation;
import org.infinispan.mvcc.*;
import org.infinispan.mvcc.exception.ValidationException;
import org.infinispan.remoting.transport.Address;
import org.infinispan.util.Util;
import org.infinispan.util.concurrent.TimeoutException;
import org.infinispan.util.concurrent.locks.DeadlockDetectedException;
import org.rhq.helpers.pluginAnnotations.agent.DisplayType;
import org.rhq.helpers.pluginAnnotations.agent.MeasurementType;
import org.rhq.helpers.pluginAnnotations.agent.Metric;
import org.rhq.helpers.pluginAnnotations.agent.Operation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author pedro
 * @author <a href="mailto:peluso@gsd.inesc-id.pt">Sebastiano Peluso</a>
 * @since 5.0
 *
 * WARNING: this only works for put() and get(). others methods like putAll(), entrySet(), keySet()
 * and so on, it is not implemented
 */
@MBean(objectName = "Transactions", description = "Component that manages the cache's participation in JTA transactions.")
public class SerialDistTxInterceptor extends DistTxInterceptor {
    private CommandsFactory commandsFactory;
    private CommitQueue commitQueue;
    private InvocationContextContainer icc;
    private DistributionManager distributionManager;


    private int nodeId;

    private boolean info, debug;

    //this is only for remote commands only!!
    private final AtomicLong successPrepareTime = new AtomicLong(0);
    private final AtomicLong failedPrepareTime = new AtomicLong(0);
    private final AtomicLong nrSuccessPrepare = new AtomicLong(0);
    private final AtomicLong nrFailedPrepare = new AtomicLong(0);
    //this is only for local commands
    private final AtomicLong rollbacksDueToUnableAcquireLock = new AtomicLong(0);
    private final AtomicLong rollbacksDueToDeadLock = new AtomicLong(0);
    private final AtomicLong rollbacksDueToValidation = new AtomicLong(0);
    //read operation
    private final AtomicLong readTime = new AtomicLong(0);
    private final AtomicLong remoteReadTime = new AtomicLong(0);
    private final AtomicLong localReadTime = new AtomicLong(0);
    private final AtomicLong nrReadOp = new AtomicLong(0);
    private final AtomicLong nrLocalReadOp = new AtomicLong(0);
    private final AtomicLong nrRemoteReadOp = new AtomicLong(0);
    
    //Lock acquisition
    private final AtomicLong locksAcquisitionTime = new AtomicLong(0L);
    private final AtomicLong nrLocksAcquisitions = new AtomicLong(0L);
    
    
    

    @Inject
    public void inject(CommandsFactory commandsFactory, CommitQueue commitQueue, InvocationContextContainer icc,
                       DistributionManager distributionManager) {
        this.commandsFactory = commandsFactory;
        this.commitQueue = commitQueue;
        this.icc = icc;
        this.distributionManager = distributionManager;
    }

    @Start(priority = 30) //After DistributionManagerImpl
    public void start() {
        info = log.isInfoEnabled();
        debug = log.isDebugEnabled();

        nodeId = distributionManager.getSelfID();
    }

    @Override
    public Object visitPrepareCommand(TxInvocationContext ctx, PrepareCommand command) throws Throwable {
        long start = System.nanoTime();
        //boolean isReadOnly = false;
        long startAcquire = 0;
        
        boolean successful = true;
        try {
            if(trace) {
                log.tracef("Prepare Command received for transaction %s",
                        Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()));
            }
            
            
            Set<Address> writeSetAddresses = null;
            if(ctx.isOriginLocal()){
            	writeSetAddresses = new HashSet<Address>();
            	for(List<Address> laddr : distributionManager.locateAll(command.getAffectedKeys()).values()) {
            		writeSetAddresses.addAll(laddr);
            	}
            }
            
            
            
            Set<Object> writeSet = getOnlyLocalKeys(command.getAffectedKeys());
            
            Set<Object> readSet;
            Set<Object> tempReadSet = new HashSet<Object>();
            Object[] arrayReadSet;
            if(!ctx.isOriginLocal()){
            	arrayReadSet = command.getReadSet();
            	if(arrayReadSet != null){
            		for(Object o: arrayReadSet){
            			tempReadSet.add(o);
            		}
            	}
            	readSet = getOnlyLocalKeys(tempReadSet);
            }	
            else{
            	
            	arrayReadSet = ((LocalTxInvocationContext) ctx).getLocalReadSet();
            	
            	if(arrayReadSet != null){
            		for(Object o: arrayReadSet){
            			tempReadSet.add(o);
            		}
            	}
            	readSet = tempReadSet;
            }


            if(ctx.isInTxScope() && ctx.isOriginLocal()){
                SnapshotId mostRecentSeenSnapshotId = ((LocalTxInvocationContext)ctx).getSeenSnapshotId();

                //I want to update before the max seen remote snapshot id
                commitQueue.addSeenSnapshotId(mostRecentSeenSnapshotId);
            }


            if(ctx.isOriginLocal() && command.getAffectedKeys().isEmpty()) {
                if(debug) {
                    log.debugf("Transaction [%s] is read-only and it wants to prepare. ignore it and return.",
                            Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()));
                }
                //isReadOnly = true;
                //read-only transaction has a valid snapshot
                return null;
            }

            if(debug) {
                log.debugf("Transaction [%s] wants to prepare. read set: %s, write set: %s, version :%s",
                        Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()),
                        readSet, writeSet, command.getVersion());
            }
            
            if(statisticsEnabled){
            	startAcquire = System.nanoTime();
            }

            //it acquires the read-write locks and it validates the read set
            AcquireValidationLocksCommand locksCommand = commandsFactory.buildAcquireValidationLocksCommand(
                    command.getGlobalTransaction(), readSet, writeSet, command.getVersion());
            
            invokeNextInterceptor(ctx, locksCommand);
            
            if(statisticsEnabled){
            	long endAcquire = System.nanoTime();
            	locksAcquisitionTime.addAndGet( endAcquire - startAcquire);
            	nrLocksAcquisitions.incrementAndGet();
            	//log.error("Acquire time: "+ (endAcquire - startAcquire));
            	
            }

            if(debug) {
                log.debugf("Transaction [%s] passes validation",
                        Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()));
            }


            //Sebastiano
            if(ctx.isOriginLocal()){ //Transaction has validated its execution locally (e.g. all local locks are acquired, the read-set is valid).
                //This means that afterwards this transaction tries to validate the execution on some remote node (if any).
                //We should remember this next remote prepares.
                //I flag the local transaction as "entered in remote prepare". This is very important because of the garbage collection of the out of order rollbacks (see RollbackCommand).
                //When a rollback arrives in remote and I have not received yet the corresponding prepare I should distinguish between the following scenarios.

                //1) This transaction does't pass the local validation so this transaction doesn't arrive at this point. In this case
                //when transaction rollback arrives on a remote node, this node should know that a prepare message will not follow the current rollback message.

                //2) This transaction passes the local validation. During the remote prepare phase we can generate the out of order problem for rollback messages (see RollbackCommand).

                ((LocalTxInvocationContext) ctx).markLocallyValidated();

            }










            /*
            long startSuper=0;
            long endSuper = 0;
            if(statisticsEnabled){
            	startSuper = System.nanoTime();
            }
            */
            //(this is equals to old schemes) wrap the wrote entries
            //finally, process the rest of the command
            Object retVal = super.visitPrepareCommand(ctx, command);
            /*
            if(statisticsEnabled){
            	endSuper = System.nanoTime();
            	if(ctx.isOriginLocal()){
            		log.error("Local Super Prepare Time: "+ (endSuper - startSuper));
            	}
            	else{
            		log.error("Remote Super Prepare Time: "+ (endSuper - startSuper));
            	}
            }
            */
            
            
            SnapshotId proposedSnapshot = null;

            if(!writeSet.isEmpty()) {
            	/*
            	 if(statisticsEnabled){
                 	startSuper = System.nanoTime();
                 }
                 */
                proposedSnapshot = commitQueue.addTransaction(command.getGlobalTransaction(), nodeId,
                        icc.getInvocationContext().clone());
                /*
                if(statisticsEnabled){
                	endSuper = System.nanoTime();
                	if(ctx.isOriginLocal()){
                		log.error("Local Commit Queue Insertion: "+ (endSuper - startSuper));
                	}
                	else{
                		log.error("Remote Commit Queue Insertion: "+ (endSuper - startSuper));
                	}
                }
                */

                if(info) {
                    log.infof("Transaction %s can commit. It was added to commit queue. " +
                            "The 'temporary' commit version is %s",
                            Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()),
                            proposedSnapshot);
                }
            } else {
                //if it is readonly, return the most recent version
                //proposedSnapshot = commitQueue.currentPropose(nodeId);

                //if it is readonly, return the most recent version+1
                proposedSnapshot = commitQueue.newPropose(nodeId);

                if(info) {
                    log.infof("Transaction %s can commit. It is read-only on this node. " +
                            "The 'temporary' commit version is %s",
                            Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()),
                            proposedSnapshot);
                }
            }


            if(ctx.isOriginLocal() && retVal != null && retVal instanceof SnapshotId) {
                //the retVal has the maximum snapshotId of all involved nodes
                //this in only performed in the node that executed the transaction
                SnapshotId remoteMaxSnapshotId = (SnapshotId) retVal;
                proposedSnapshot.setMaximum(remoteMaxSnapshotId);

            }

            ctx.setCommitSnapshotId(proposedSnapshot);

            if(debug) {
                log.debugf("Transaction [%s] %s commit version is %s",
                        Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()),
                        (ctx.isOriginLocal() ? "final" : "temporary"),
                        proposedSnapshot);
            }

            return proposedSnapshot;
        } catch(TimeoutException e) {
            successful = false;
            if(statisticsEnabled && ctx.isOriginLocal()) {
                rollbacksDueToUnableAcquireLock.incrementAndGet();
            }
            throw e;
        } catch(DeadlockDetectedException e) {
            successful = false;
            if(statisticsEnabled && ctx.isOriginLocal()) {
                rollbacksDueToDeadLock.incrementAndGet();
            }
            throw e;
        } catch(ValidationException e) {
            successful = false;
            if(statisticsEnabled && ctx.isOriginLocal()) {
                rollbacksDueToValidation.incrementAndGet();
            }
            throw e;
        } finally {
        	if(statisticsEnabled){
        		
        		long end = System.nanoTime();
        		
        		if(!ctx.isOriginLocal()) {
        			
        			if(successful) {
        				successPrepareTime.addAndGet(end - start);
        				nrSuccessPrepare.incrementAndGet();
        			} else {
        				failedPrepareTime.addAndGet(end - start);
        				nrFailedPrepare.incrementAndGet();
        			}
        		}
        		/*
        		else if(!isReadOnly){
        			if(successful){
        				log.error("Prepare Time (nanosec): "+ (end - start));
        			}
        		}
        		*/
        	}
        }
    }

    @Override
    public Object visitCommitCommand(TxInvocationContext ctx, CommitCommand command) throws Throwable {
        if(info) {
            log.infof("Commit Command received for transaction %s",
                    Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()));
        }

        return super.visitCommitCommand(ctx, command);
    }

    @Override
    public Object visitRollbackCommand(TxInvocationContext ctx, RollbackCommand command) throws Throwable {
        if(info) {
            log.infof("Rollback Command received for transaction %s", Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()));
        }
        return super.visitRollbackCommand(ctx, command);
    }

    @Override
    public Object visitGetKeyValueCommand(InvocationContext ctx, GetKeyValueCommand command) throws Throwable {
        long start = System.nanoTime();
        
        boolean isKeyLocal = isKeyLocal(command.getKey());
        
        try {
        	
            //Object retVal = super.visitGetKeyValueCommand(ctx, command);
        	Object retVal = enlistReadAndInvokeNext(ctx, command);
        	
            if(ctx.isInTxScope() && ctx.isOriginLocal()) {
                TxInvocationContext txctx = (TxInvocationContext) ctx;
 
                //update vc
                InternalMVCCEntry ime;
                Object key = command.getKey();
                if(isKeyLocal){ //We have read on this node, The key of the read value is found in the local read set
                	ime = ctx.getLocalReadKey(key);

                    if(debug){
                        log.debugf("Local Get for transaction %s and key %s. Return value is %s",
                                        Util.prettyPrintGlobalTransaction(((LocalTxInvocationContext) ctx).
                                                getGlobalTransaction()), key, ime);
                    }
                	
                	if((key instanceof ContextAwareKey) && ((ContextAwareKey)key).identifyImmutableValue()){
                		//The key identifies an immutable object. We don't need validation for this key.
                		ctx.removeLocalReadKey(key); 
                	}


                }
                else{//We have read on a remote node. The key of the read value is found in the remote read set
                	ime = ctx.getRemoteReadKey(key);

                    if(debug){
                        log.debugf("Remote Get for transaction %s and key %s. Return value is %s",
                                        Util.prettyPrintGlobalTransaction(((LocalTxInvocationContext) ctx).
                                                getGlobalTransaction()), key, ime);
                    }
                	
                	if((key instanceof ContextAwareKey) && ((ContextAwareKey)key).identifyImmutableValue()){
                		//The key identifies an immutable object. We don't need validation for this key.
                		ctx.removeRemoteReadKey(key);
                	}
                }

                if(ime == null) {
                	//String gtxID = Util.prettyPrintGlobalTransaction(txctx.getGlobalTransaction());
                    //log.warnf("InternalMVCCEntry is null for key %s in transaction %s", command.getKey(), gtxID);



                }
                else{

                    SnapshotId seenSnapshotId = ime.getMaxSeenSnapshotId();

                    ((LocalTxInvocationContext)ctx).addSeenSnapshotId(seenSnapshotId);

                    SnapshotId mostRecentSeenSnapshotId = ((LocalTxInvocationContext)ctx).getSeenSnapshotId();



                    if(txctx.hasModifications() && !ime.isMostRecent()) {


                        //I'm aborting this update transaction
                        //I want to update before the max seen remote snapshot id
                        commitQueue.addSeenSnapshotId(mostRecentSeenSnapshotId);

                        //read an old value... the tx will abort in commit,
                        //so, do not waste time and abort it now
                        /*
                        if(info) {
                            String gtxID = Util.prettyPrintGlobalTransaction(txctx.getGlobalTransaction());
                            log.infof("Read-Write transaction [%s] read an old value. It will be aborted",
                                    gtxID);
                        }
                        */
                        throw new CacheException("Read-Write Transaction read an old value");
                    }



                }

            }

            //remote get
            if(!ctx.isOriginLocal()) {
            	/*
                if(debug) {
                String gtxID = Util.prettyPrintGlobalTransaction(txctx.getGlobalTransaction());
                    log.debugf("Remote Get received for key %s. return value is %s. Multi Version return is %s",
                            command.getKey(), retVal, ctx.getLocalReadKey(command.getKey()));
                }
                
                */

                if(ctx.readBasedOnVersion()) {
                    retVal = ctx.getLocalReadKey(command.getKey());
                    ctx.removeLocalReadKey(command.getKey()); //We don't need a readSet on a remote node!
                }
            }

            return retVal;
        } finally {
            if(statisticsEnabled) {
                long end = System.nanoTime();
                if(ctx.isOriginLocal()) {
                    readTime.addAndGet(end - start);
                    nrReadOp.incrementAndGet();
                } else {
                    remoteReadTime.addAndGet(end - start);
                    nrRemoteReadOp.incrementAndGet();
                }
                
                if(ctx.isOriginLocal() && isKeyLocal){
                	localReadTime.addAndGet(end - start);
                	nrLocalReadOp.incrementAndGet();
                }
            }
        }
    }



    private boolean isKeyLocal(Object key) {
        return distributionManager.getLocality(key).isLocal();
    }

    /**
     *
     * @param keys keys to check
     * @return return only the local key or empty if it has no one
     */
    private Set<Object> getOnlyLocalKeys(Set<Object> keys) {
        Set<Object> localKeys = new HashSet<Object>();
        for(Object key : keys) {
            if(distributionManager.getLocality(key).isLocal()) {
                localKeys.add(key);
            }
        }
        return localKeys;
    }

    private Integer[] getVCPositions(Set<Address> writeSetMembers) {
        Set<Integer> positions = new HashSet<Integer>();
        for(Address addr : writeSetMembers) {
            positions.add(distributionManager.getAddressID(addr));
        }
        Integer[] retVal = new Integer[positions.size()];
        positions.toArray(retVal);

        if(debug) {
            log.debugf("Address IDs for %s are %s", writeSetMembers, positions);
        }

        return retVal;
    }



    @ManagedOperation(description = "Resets statistics gathered by this component")
    @Operation(displayName = "Reset Statistics")
    public void resetStatistics() {
        super.resetStatistics();
        successPrepareTime.set(0);
        failedPrepareTime.set(0);
        nrSuccessPrepare.set(0);
        nrFailedPrepare.set(0);
        rollbacksDueToUnableAcquireLock.set(0);
        rollbacksDueToDeadLock.set(0);
        rollbacksDueToValidation.set(0);
        readTime.set(0);
        remoteReadTime.set(0);
        nrReadOp.set(0);
        nrLocalReadOp.set(0);
        nrRemoteReadOp.set(0);
        localReadTime.set(0);
        locksAcquisitionTime.set(0L);
        nrLocksAcquisitions.set(0L);
    }

    @ManagedAttribute(description = "Duration of all successful remote prepare command since last reset (nano-seconds)")
    @Metric(displayName = "SuccessPrepareTime", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getSuccessPrepareTime() {
        return successPrepareTime.get();
    }

    @ManagedAttribute(description = "Duration of all failed remote prepare command since last reset (nano-seconds)")
    @Metric(displayName = "FailedPrepareTime", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getFailedPrepareTime() {
        return failedPrepareTime.get();
    }

    @ManagedAttribute(description = "Number of successful remote prepare command performed since last reset")
    @Metric(displayName = "NrSuccessPrepare", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getNrSuccessPrepare() {
        return nrSuccessPrepare.get();
    }

    @ManagedAttribute(description = "Number of failed remote prepare command performed since last reset")
    @Metric(displayName = "NrFailedPrepare", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getNrFailedPrepare() {
        return nrFailedPrepare.get();
    }

    @ManagedAttribute(description = "Number of rollbacks due to unable of acquire the locks since last reset")
    @Metric(displayName = "RollbacksDueToUnableAcquireLock", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getRollbacksDueToUnableAcquireLock() {
        return rollbacksDueToUnableAcquireLock.get();
    }

    @ManagedAttribute(description = "Number of rollbacks due to dead locks since last reset")
    @Metric(displayName = "RollbacksDueToDeadLock", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getRollbacksDueToDeadLock() {
        return rollbacksDueToDeadLock.get();
    }

    @ManagedAttribute(description = "Number of rollbacks due to validation failed since last reset")
    @Metric(displayName = "RollbacksDueToValidation", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getRollbacksDueToValidation() {
        return rollbacksDueToValidation.get();
    }

    @ManagedAttribute(description = "Duration of all read command since last reset (nano-seconds)")
    @Metric(displayName = "ReadTime", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getReadTime() {
        return readTime.get();
    }

    @ManagedAttribute(description = "Duration of all remote read command since last reset (nano-seconds)")
    @Metric(displayName = "RemoteReadTime", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getRemoteReadTime() {
        return remoteReadTime.get();
    }
    
    @ManagedAttribute(description = "Duration of all local read command since last reset (nano-seconds)")
    @Metric(displayName = "LocalReadTime", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getLocalReadTime() {
        return localReadTime.get();
    }

    @ManagedAttribute(description = "Number of read commands since last reset")
    @Metric(displayName = "NrReadOp", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getNrReadOp() {
        return nrReadOp.get();
    }
    
    @ManagedAttribute(description = "Number of local read commands since last reset")
    @Metric(displayName = "NrLocalReadOp", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getNrLocalReadOp() {
        return this.nrLocalReadOp.get();
    }

    @ManagedAttribute(description = "Number of remote read commands since last reset")
    @Metric(displayName = "NrRemotelReadOp", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getNrRemoteReadOp() {
        return nrRemoteReadOp.get();
    }
    
    @ManagedAttribute(description = "Number of successful read-write locks acquisitions")
    @Metric(displayName = "NrRWLocksAcquisitions", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getNrRWLocksAcquisitions() {
        return nrLocksAcquisitions.get();
    }
    
    @ManagedAttribute(description = "Total duration of successful read-write locks acquisitions since last reset (nanoseconds)")
    @Metric(displayName = "RWLocksAcquisitionTime", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getRWLocksAcquisitionTime() {
        return locksAcquisitionTime.get();
    }
}
