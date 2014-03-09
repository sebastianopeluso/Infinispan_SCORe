package org.infinispan.interceptors;

import org.infinispan.CacheException;
import org.infinispan.commands.read.GetKeyValueCommand;
import org.infinispan.commands.tx.CommitCommand;
import org.infinispan.commands.tx.PrepareCommand;
import org.infinispan.commands.tx.RollbackCommand;
import org.infinispan.commands.write.PutKeyValueCommand;
import org.infinispan.container.entries.CacheEntry;
import org.infinispan.container.entries.InternalCacheEntry;
import org.infinispan.context.InvocationContext;
import org.infinispan.context.impl.LocalTxInvocationContext;
import org.infinispan.context.impl.TxInvocationContext;
import org.infinispan.factories.annotations.Inject;
import org.infinispan.factories.annotations.Start;
import org.infinispan.jmx.annotations.MBean;
import org.infinispan.jmx.annotations.ManagedAttribute;
import org.infinispan.jmx.annotations.ManagedOperation;
import org.infinispan.mvcc.InternalMVCCEntry;
import org.infinispan.mvcc.SnapshotId;
import org.infinispan.remoting.responses.ExceptionResponse;
import org.infinispan.remoting.responses.Response;
import org.infinispan.remoting.responses.SuccessfulResponse;
import org.infinispan.remoting.transport.Address;
import org.infinispan.util.Util;
import org.infinispan.util.concurrent.NotifyingNotifiableFuture;
import org.rhq.helpers.pluginAnnotations.agent.DisplayType;
import org.rhq.helpers.pluginAnnotations.agent.MeasurementType;
import org.rhq.helpers.pluginAnnotations.agent.Metric;
import org.rhq.helpers.pluginAnnotations.agent.Operation;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author pruivo
 * @author <a href="mailto:peluso@gsd.inesc-id.pt">Sebastiano Peluso</a>
 * @since 5.0
 */
@MBean(objectName = "DistributionInterceptor", description = "Handles distribution of entries across a cluster, as well as transparent lookup.")
public class SerialDistributionInterceptor extends DistributionInterceptor {

    private boolean info, debug;
    
    private final AtomicLong totalNumOfInvolvedNodesPerPrepare = new AtomicLong(0L);
    private final AtomicLong totalPrepareSent = new AtomicLong(0L);
    


    @Start
    public void setLogBoolean() {
        info = log.isInfoEnabled();
        debug = log.isDebugEnabled();
    }

    @Override
    public Object visitPrepareCommand(TxInvocationContext ctx, PrepareCommand command) throws Throwable {
        Object retVal = invokeNextInterceptor(ctx, command);

        if (shouldInvokeRemoteTxCommand(ctx)) {
            if(info) {
                log.infof("Prepare Command received for %s and it will be multicast",
                        Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()));
            }
            
            
            
            


            //obtain writeSet
            Set<Object> writeSet = new HashSet<Object>(ctx.getAffectedKeys());
            
            
            //obtain readSet
            Set<Object> readSet = new HashSet<Object>();
            
            Object[] arrayReadSet = ((LocalTxInvocationContext) ctx).getRemoteReadSet();
            if(arrayReadSet!= null){
            	for(Object o: arrayReadSet){
            		readSet.add(o);
            	}
            }



            
            
            //get the members to contact
            List<Address> recipients = dm.getAffectedNodesAndOwners(writeSet, readSet);
            
            

            

            //something about L1 cache
            NotifyingNotifiableFuture<Object> f = null;
            if (isL1CacheEnabled && command.isOnePhaseCommit()) {
                f = l1Manager.flushCache(ctx.getLockedKeys(), null, null);
            }
            
            
            
            //Object[] readSet = command.getReadSet();
            //int readSetLength = (readSet == null)? 0 : readSet.length;

            //start = System.nanoTime();
            
            //send the command and wait for the vector clocks
            Map<Address, Response> responses = rpcManager.invokeRemotely(recipients, command, true, false);
            //something...
            
            //end = System.nanoTime();
            
            //String readSetEmpty = (readSetLength == 0)? "No ReadSet":"With ReadSet of legth "+readSetLength; 
            
            if(this.statisticsEnabled){
            	Set<Address> involvedNodes = responses.keySet();

            	int numResponses = (involvedNodes == null)? 0 : involvedNodes.size();


            	this.totalNumOfInvolvedNodesPerPrepare.addAndGet(numResponses);
            	this.totalPrepareSent.incrementAndGet();

            }
            
            
            //start = System.nanoTime();
            
            ((LocalTxInvocationContext) ctx).remoteLocksAcquired(recipients);
            
            //end = System.nanoTime();
            
            //log.error("set remoteLocksAcquired: "+ (end - start));

            if(debug) {
                log.debugf("Prepare Command multicasted for transaction %s and the responses are: %s",
                        Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()),
                        responses.toString());
            }
            
            //start = System.nanoTime();

            if (!responses.isEmpty()) {
                SnapshotId maxPreparedSnapshotId = new SnapshotId();

                //process all responses
                for (Response r : responses.values()) {
                    if (r instanceof SuccessfulResponse) {
                        SnapshotId preparedSnapshotId = (SnapshotId) ((SuccessfulResponse) r).getResponseValue();
                        maxPreparedSnapshotId.setMaximum(preparedSnapshotId);
                    } else if(r instanceof ExceptionResponse) {
                        Exception e = ((ExceptionResponse) r).getException();

                        if(info) {
                            log.infof("Transaction %s received a negative response %s (reason:%s) and it must be aborted",
                                    Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()), r,
                                    e.getLocalizedMessage());
                        }

                        throw e;
                    } else if(!r.isSuccessful()) {
                        if(info) {
                            log.debugf("Transaction %s received an unsuccessful response %s and it mus be aborted",
                                    Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()), r);
                        }

                        throw new CacheException("Unsuccessful response received... aborting transaction " +
                                Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()));
                    }
                }

                //this has the maximum vector clock of all
                retVal = maxPreparedSnapshotId;
                
                
                
                if(info) {
                    log.infof("Transaction %s receive only positive votes and it can commit. Prepare version is %s",
                            Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()), maxPreparedSnapshotId);
                }
            }
            
            //end = System.nanoTime();
            
            //log.error("parsing of responses: "+ (end - start));


            if (f != null) {
                f.get();
            }
        } else {
            if(info) {
                log.infof("Prepare Command received for %s and it will *NOT* be multicast",
                        Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()));
            }
        }
        return retVal;
    }
    
    @Override
    public Object visitCommitCommand(TxInvocationContext ctx, CommitCommand command) throws Throwable {
        if (shouldInvokeRemoteTxCommand(ctx)) {
            Collection<Address> preparedOn = ((LocalTxInvocationContext) ctx).getRemoteLocksAcquired();

          //obtain writeSet
            Set<Object> writeSet = new HashSet<Object>(ctx.getAffectedKeys());
            
            
            //obtain readSet
            Set<Object> readSet = new HashSet<Object>();
            
            Object[] arrayReadSet = ((LocalTxInvocationContext) ctx).getRemoteReadSet();
            if(arrayReadSet!= null){
            	for(Object o: arrayReadSet){
            		readSet.add(o);
            	}
            }
            
            //get the members to contact
            List<Address> recipients = dm.getAffectedNodesAndOwners(writeSet, readSet);
            

            // By default, use the configured commit sync settings
            boolean syncCommitPhase = configuration.isSyncCommitPhase();
            for (Address a : preparedOn) {
                if (!recipients.contains(a)) {
                    // However if we have prepared on some nodes and are now committing on different nodes, make sure we
                    // force sync commit so we can respond to prepare resend requests.
                    syncCommitPhase = true;
                }
            }
            NotifyingNotifiableFuture<Object> f = null;
            if (isL1CacheEnabled) {
                f = l1Manager.flushCache(ctx.getLockedKeys(), null, null);
            }

            Map<Address, Response> responses = rpcManager.invokeRemotely(recipients, command, syncCommitPhase, true);

            if (!responses.isEmpty()) {
                List<Address> resendTo = new LinkedList<Address>();
                for (Map.Entry<Address, Response> r : responses.entrySet()) {
                    if (needToResendPrepare(r.getValue()))
                        resendTo.add(r.getKey());
                }

                if (!resendTo.isEmpty()) {
                    log.debugf("Need to resend prepares for %s to %s", command.getGlobalTransaction(), resendTo);
                    // Make sure this is 1-Phase!!
                    PrepareCommand pc = cf.buildPrepareCommand(command.getGlobalTransaction(), ctx.getModifications(), true);
                    rpcManager.invokeRemotely(resendTo, pc, true, true);
                }
            }

            if (f != null && configuration.isSyncCommitPhase()) {
                try {
                    f.get();
                } catch (Exception e) {
                    if (log.isInfoEnabled()) log.failedInvalidatingRemoteCache(e);
                }
            }
        }
        return invokeNextInterceptor(ctx, command);
    }
    
    @Override
    public Object visitRollbackCommand(TxInvocationContext ctx, RollbackCommand command) throws Throwable {
        if (shouldInvokeRemoteTxCommand(ctx)  && ctx.isOriginLocal() && ((LocalTxInvocationContext)ctx).isLocallyValidated()){
        	
        	
        	//obtain writeSet
            Set<Object> writeSet = new HashSet<Object>(ctx.getAffectedKeys());
            
            
            //obtain readSet
            Set<Object> readSet = new HashSet<Object>();
            
            Object[] arrayReadSet = ((LocalTxInvocationContext) ctx).getRemoteReadSet();
            if(arrayReadSet!= null){
            	for(Object o: arrayReadSet){
            		readSet.add(o);
            	}
            }
            
            //get the members to contact
            List<Address> recipients = dm.getAffectedNodesAndOwners(writeSet, readSet);
        	
        	
        	

            rpcManager.invokeRemotely(recipients, command, configuration.isSyncRollbackPhase(), true);
        }    
        return invokeNextInterceptor(ctx, command);
    }

    // ---- READ commands

    // if we don't have the key locally, fetch from one of the remote servers
    // and if L1 is enabled, cache in L1
    // then return

    @Override
    public Object visitGetKeyValueCommand(InvocationContext ctx, GetKeyValueCommand command) throws Throwable {

        // If you are a joiner then even if a rehash has completed you still may not have integrated all remote state.
        // so we need to check whether join has completed as well.
        boolean isRehashInProgress = !dm.isJoinComplete() || dm.isRehashInProgress();
        Object returnValue = invokeNextInterceptor(ctx, command);

        // If L1 caching is enabled, this is a remote command, and we found a value in our cache
        // we store it so that we can later invalidate it
        if (isL1CacheEnabled && !ctx.isOriginLocal() && returnValue != null) {
            l1Manager.addRequestor(command.getKey(), ctx.getOrigin());
        }

        // need to check in the context as well since a null retval is not necessarily an indication of the entry not being
        // available.  It could just have been removed in the same tx beforehand.
        if (needsRemoteGet(ctx, command.getKey(), returnValue == null)) {
            returnValue = remoteGetAndStoreInL1(ctx, command.getKey(), isRehashInProgress, false);

            if(ctx.isInTxScope() && ctx.isOriginLocal()){
                if(!((LocalTxInvocationContext)ctx).isFirstReadPerformed()){
                    ((LocalTxInvocationContext)ctx).setFirstReadPerformed(true);

                    InternalMVCCEntry ime = ctx.getRemoteReadKey(command.getKey());   //This is the last remotely read key

                    if(ime!=null){
                        ctx.setSnapshotId(ime.getAcquiredSnapshotId());
                    }

                    //if ime == null dont' worry. The snapshotId of the current transaction is already initialized to the snapshot id of the origin node.
                }
            }

        }
        else{
            if(ctx.isInTxScope() && ctx.isOriginLocal()){
                ((LocalTxInvocationContext)ctx).setFirstReadPerformed(true);//This means that if we are on the origin node the read has been performed here.
            //Then if this is the first read, the snapshot acquired at the beginning will be the final snapshot of the transaction
            }
        }

        return returnValue;
    }


    
    protected Object realRemoteGet(InvocationContext ctx, Object key, boolean storeInL1, boolean isWrite) throws Throwable {
        if (trace) log.tracef("Doing a remote get for key %s", key);
        // attempt a remote lookup
        InternalCacheEntry ice = dm.retrieveFromRemoteSource(key, ctx);

        if (ice != null) {
            if (storeInL1) {
                if (isL1CacheEnabled) {
                    if (trace) log.tracef("Caching remotely retrieved entry for key %s in L1", key);
                    long lifespan = ice.getLifespan() < 0 ? configuration.getL1Lifespan() : Math.min(ice.getLifespan(), configuration.getL1Lifespan());
                    PutKeyValueCommand put = cf.buildPutKeyValueCommand(ice.getKey(), ice.getValue(), lifespan, -1, ctx.getFlags());
                    entryFactory.wrapEntryForWriting(ctx, key, true, false, ctx.hasLockedKey(key), false, false);
                    invokeNextInterceptor(ctx, put);
                } else {
                    CacheEntry ce = ctx.lookupEntry(key);
                    if (ce == null || ce.isNull() || ce.isLockPlaceholder() || ce.getValue() == null) {
                        if (ce != null && ce.isChanged()) {
                            ce.setValue(ice.getValue());
                        } else {
                            if (isWrite)
                                entryFactory.wrapEntryForWriting(ctx, ice, true, false, ctx.hasLockedKey(key), false, false);
                            //else
                               // ctx.putLookedUpEntry(key, ice);
                        }
                    }
                }
            } else {
                if (trace) log.tracef("Not caching remotely retrieved entry for key %s in L1", key);
            }
            return ice.getValue();
        }
        return null;
    }
    
    
    @ManagedOperation(description = "Resets statistics gathered by this component")
    @Operation(displayName = "Reset Statistics")
    public void resetStatistics() {
        
    	this.totalNumOfInvolvedNodesPerPrepare.set(0L);
    	this.totalPrepareSent.set(0L);
        
    }
    
    @ManagedAttribute(description = "Total number of involved nodes per prepare phase.")
    @Metric(displayName = "TotalNumOfInvolvedNodesPerPrepare", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getTotalNumOfInvolvedNodesPerPrepare() {
        return this.totalNumOfInvolvedNodesPerPrepare.get();
    }
    
    @ManagedAttribute(description = "Total number of prepare message sent.")
    @Metric(displayName = "TotalPrepareSent", measurementType = MeasurementType.TRENDSUP, displayType = DisplayType.SUMMARY)
    public long getTotalPrepareSent() {
        return this.totalPrepareSent.get();
    }
}
