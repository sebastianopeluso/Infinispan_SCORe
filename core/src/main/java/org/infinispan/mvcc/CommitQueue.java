package org.infinispan.mvcc;

import org.infinispan.commands.write.WriteCommand;
import org.infinispan.context.InvocationContext;
import org.infinispan.context.InvocationContextContainer;
import org.infinispan.context.impl.TxInvocationContext;
import org.infinispan.distribution.DistributionManager;
import org.infinispan.factories.annotations.Inject;
import org.infinispan.factories.annotations.Start;
import org.infinispan.interceptors.InterceptorChain;
import org.infinispan.interceptors.LockingInterceptor;
import org.infinispan.interceptors.base.CommandInterceptor;
import org.infinispan.transaction.xa.GlobalTransaction;
import org.infinispan.util.Util;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author <a href="mailto:peluso@gsd.inesc-id.pt">Sebastiano Peluso</a>
 * @since 5.0
 */
public class CommitQueue {

    private final static Log log = LogFactory.getLog(CommitQueue.class);


    private final AtomicLong versionGenerator;




    private final ArrayList<ListEntry> commitQueue;
    private CommitInstance commitInvocationInstance;
    private InterceptorChain ic;
    private InvocationContextContainer icc;

    private DistributionManager dm;


    private CommitLog commitLog;


    private final AtomicReference<SnapshotId> maxSeenSnapshotId;

    private boolean trace, debug, info;

    public CommitQueue() {
        
        commitQueue = new ArrayList<ListEntry>();
        versionGenerator = new AtomicLong(1L); //1 is safe since the snapshotId starts with version 0

        maxSeenSnapshotId = new AtomicReference<SnapshotId>(null);


    }
    
    
    
   


    private int searchInsertIndexPos(SnapshotId snapshotId) {
        if(commitQueue.isEmpty()) {
            return 0;
        }
        int idx = 0;
        ListIterator<ListEntry> lit = commitQueue.listIterator();
        ListEntry le;
        while(lit.hasNext()) {
            le = lit.next();
            if(le.snapshotId.compareTo(snapshotId) > 0){
            
                return idx;
            }
            idx++;
        }
        return idx;
    }

    //a transaction can commit if it is ready, it is on head of the queue and
    //  the following transactions has a version higher than this one
    //if the following transactions has the same vector clock, then their must be ready to commit
    //  and their modifications will be batched with this transaction

    //return values:
    //   -2: not ready to commit (must wait)
    //   -1: it was already committed (can be removed)
    //    n (>=0): it is ready to commit and commits the 'n'th following txs

    //WARNING: it is assumed that this is called inside a synchronized block!!
    private int getCommitCode(GlobalTransaction gtx) {
        ListEntry toSearch = new ListEntry();
        toSearch.gtx = gtx;
        int idx = commitQueue.indexOf(toSearch);
        if(idx < 0) {
            return -1;
        } else if(idx > 0) {
            return -2;
        }

        toSearch = commitQueue.get(0);

        if(toSearch.applied) {
            return -1;
        }

        SnapshotId tempCommitSnapshotId = toSearch.snapshotId;

        int queueSize = commitQueue.size();
        idx = 1;

        while(idx < queueSize) {
            ListEntry other = commitQueue.get(idx);
            if(tempCommitSnapshotId.equals(other.snapshotId)){
            
                if(!other.ready) {
                    return -2;
                }
                
                idx++;
            } else {
                break;
            }
        }

        return idx - 1;
    }

   
    
    @Inject
    public void inject(InterceptorChain ic, InvocationContextContainer icc, DistributionManager dm, CommitLog commitLog) {

        this.ic = ic;
        this.icc = icc;

        this.dm = dm;
        this.commitLog = commitLog;
    }

    ///AFTER THE DistributionManagerImpl
    @Start(priority = 31)
    public void start() {
        trace = log.isTraceEnabled();
        debug = log.isDebugEnabled();
        info = log.isInfoEnabled();

        int selfId = dm.getSelfID();


        maxSeenSnapshotId.set(new SnapshotId(0, selfId));
        


        if(commitInvocationInstance == null) {
            List<CommandInterceptor> all = ic.getInterceptorsWhichExtend(LockingInterceptor.class);
            if(log.isInfoEnabled()) {
                log.infof("Starting Commit Queue Component. Searching interceptors with interface CommitInstance. " +
                        "Found: %s", all);
            }
            for(CommandInterceptor ci : all) {
                if(ci instanceof CommitInstance) {
                    if(debug) {
                        log.debugf("Interceptor implementing CommitInstance found! It is %s", ci);
                    }
                    commitInvocationInstance = (CommitInstance) ci;
                    break;
                }
            }
        }
        if(commitInvocationInstance == null) {
            throw new NullPointerException("Commit Invocation Instance must not be null in serializable mode.");
        }
    }


    public SnapshotId addTransaction(GlobalTransaction gtx, int nodeId, InvocationContext ctx) {






        synchronized (commitQueue) {

            long newVersion = versionGenerator.incrementAndGet();


            ListEntry le = new ListEntry();
            le.gtx = gtx;
            le.snapshotId = new SnapshotId(newVersion, nodeId);
            le.ctx = ctx;


            int idx = searchInsertIndexPos(le.snapshotId);
            if(info){
                log.infof("Adding transaction %s [%s] to queue in position %s. queue state is %s", Util.prettyPrintGlobalTransaction(gtx), le.snapshotId, idx, commitQueue.toString());
            }
            commitQueue.add(idx, le);
            //commitQueue.notifyAll();

            return le.snapshotId;
        }




    }

    public SnapshotId currentPropose(int nodeId){

        return new SnapshotId(versionGenerator.get(), nodeId);
    }

    public SnapshotId newPropose(int nodeId){
        return new SnapshotId(versionGenerator.incrementAndGet(), nodeId);
    }

    public void updateVersionGeneratorValue(long version) {

        long versionGeneratorValue = this.versionGenerator.get();

        long updateValue = version +1; //I don't want to manage parity here. Adding 1 is safe.

        while(versionGeneratorValue < updateValue){

            if(versionGenerator.compareAndSet(versionGeneratorValue, updateValue)){
                break;
            }

            versionGeneratorValue = this.versionGenerator.get();

        }

    }



    public void updateOnlyCurrentPropose(SnapshotId commitSnapshotId){


        updateVersionGeneratorValue(commitSnapshotId.getVersion());


    }



    public void updateAndWait(GlobalTransaction gtx, SnapshotId commitSnapshotId) throws InterruptedException {

        updateOnlyCurrentPropose(commitSnapshotId);

        List<ListEntry> toCommit = new LinkedList<ListEntry>();

        
        int commitCode=-1;
        
        synchronized (commitQueue) {
            ListEntry toSearch = new ListEntry();
            toSearch.gtx = gtx;
            int idx = commitQueue.indexOf(toSearch);

            if(info){
                log.infof("Pre Update transaction %s position in queue. Final index is %s and commit version is %s",
                        Util.prettyPrintGlobalTransaction(gtx),
                        idx, commitSnapshotId);
            }

            if(idx == -1) {
                return ;
            }

            //update the position on the queue
            ListEntry le = commitQueue.get(idx);
            commitQueue.remove(idx);
            le.snapshotId = commitSnapshotId;
            idx = searchInsertIndexPos(le.snapshotId);
            commitQueue.add(idx, le);

            commitQueue.get(0).headStartTs = System.nanoTime();


            if(info){
                log.infof("Post Update transaction %s position in queue. Final index is %s and commit version is %s",
                        Util.prettyPrintGlobalTransaction(gtx),
                        idx, commitSnapshotId);
            }

            le.ready = true;
            commitQueue.notifyAll();

            while(true) {

                commitCode = getCommitCode(gtx);

                if(commitCode == -2) { //it is not it turn
                    try{

                    commitQueue.wait(5000);
                    }
                    catch(IllegalArgumentException e1){

                    }
                    catch(IllegalMonitorStateException e2){

                    }
                    catch(InterruptedException e3){

                    }

                    continue;
                } else if(commitCode == -1) { //already committed
                    commitQueue.remove(le); //We don't really need this line
                    commitQueue.notifyAll();
                    
                    return;
                }

                if(le.headStartTs != 0) {
                	if(info){
                    log.infof("Transaction %s has %s nano seconds in the head of the queue",
                            Util.prettyPrintGlobalTransaction(gtx), System.nanoTime() - le.headStartTs);
                	}
                }

                if(commitCode >= commitQueue.size()) {
                	if(info){
                		log.infof("The commit code received is higher than the size of commit queue (%s > %s)",
                            commitCode, commitQueue.size());
                	}
                    commitCode = commitQueue.size() - 1;
                }

                toCommit.addAll(commitQueue.subList(0, commitCode + 1));
                break;
            }
            if(info){
            log.infof("Transaction %s will apply it write set now. Queue state is %s",
                        Util.prettyPrintGlobalTransaction(le.gtx), commitQueue.toString());
            }
            
        }

        if(info) {
            log.infof("This thread will aplly the write set of " + toCommit);
        }

        SnapshotId finalCommitSnapshotId = null;
        InvocationContext thisCtx = icc.suspend();
        for(ListEntry le : toCommit) {
            icc.resume(le.ctx);
            commitInvocationInstance.commit(le.ctx, le.snapshotId);
            icc.suspend();
            le.applied = true;
            finalCommitSnapshotId = le.snapshotId; //Get the only the value in the last iteration. This is OK. All values are equals.
        }
        icc.resume(thisCtx);
        

        


        //Insert only one entry in the commitLog. This is the maximum vector clock.
        commitInvocationInstance.addTransaction(finalCommitSnapshotId);

        synchronized (commitQueue) {
            if(commitQueue.removeAll(toCommit)) {

                //At this stage if the queue is empty or the first in the queue has a snapshotId greater than
                //the commitLog.maxSeenSnapshotId, I can change the actual snapshotId  to the maxSeenSnapshotId


                //We can advance the commitLog to a snapshot with version number less than the minimum
                //version number that could be committed from now on.

                if(commitQueue.isEmpty()){
                    SnapshotId maxSeen = this.maxSeenSnapshotId.get();

                    updateVersionGeneratorValue(maxSeen.getVersion());

                    commitLog.addNewVersion(maxSeen);
                }
                /*
                else{
                    SnapshotId minToCommit = commitQueue.get(0).snapshotId;
                    SnapshotId maxSeen = this.maxSeenSnapshotId.get();

                    if(maxSeen != null && maxSeen.compareTo(minToCommit) < 0){

                        commitLog.addNewVersion(maxSeen);

                    }

                }

                */




            }

            commitQueue.notifyAll();

            if(!commitQueue.isEmpty()) {
                commitQueue.get(0).headStartTs = System.nanoTime();
            }
        }
    }

    public void addSeenSnapshotId(SnapshotId seenSnapshotId){


        SnapshotId maxSeen = this.maxSeenSnapshotId.get();



        while(maxSeen.compareTo(seenSnapshotId) < 0){

            if(this.maxSeenSnapshotId.compareAndSet(maxSeen, seenSnapshotId)){

                if(seenSnapshotId != null){

                    //This means that the current node knows that a remote node is arrived to maxSeen.
                    //In fact maxSeen is the maximum snapshot identifier seen by the current node.

                    //In this case I want to update the version generator on the commitQueue in such a way new update transactions
                    //will be committed with a commit snapshot identifier at least greater than maxSeen.

                    //In this way I guarantee that eventually new committed values will be accessible from the current node even if on the current node
                    //I don't commit new update transactions.

                    updateVersionGeneratorValue(seenSnapshotId.getVersion());


                    synchronized (commitQueue){
                        if(commitQueue.isEmpty()){


                            commitLog.addNewVersion(seenSnapshotId);
                        }
                        else{
                            SnapshotId minToCommit = commitQueue.get(0).snapshotId;


                            if(seenSnapshotId != null && seenSnapshotId.compareTo(minToCommit) < 0){

                                commitLog.addNewVersion(maxSeen);

                            }

                        }

                    }
                }

                break;
            }

            maxSeen = this.maxSeenSnapshotId.get();




        }
    }

    /**
     * remove the transaction (ie. if the transaction rollbacks)
     * @param gtx the transaction identifier
     */
    @SuppressWarnings({"SuspiciousMethodCalls"})
    public void remove(GlobalTransaction gtx) {
        ListEntry toSearch = new ListEntry();
        toSearch.gtx = gtx;
        synchronized (commitQueue) {
            if(trace) {
                log.tracef("Remove transaction %s from queue. Queue is %s",
                        Util.prettyPrintGlobalTransaction(gtx), commitQueue.toString());
            }
            if(commitQueue.remove(toSearch)) {
                commitQueue.notifyAll();
            }
        }
    }

    /**
     * removes the first element of the queue
     */
    public void removeFirst() {
        synchronized (commitQueue) {
            if(trace) {
                log.tracef("Remove first transaction from queue. Queue is %s", commitQueue.toString());
            }
            commitQueue.remove(0);
            commitQueue.notifyAll();
        }
    }

    /**
     * removes all the elements
     */
    public void clear() {
        synchronized (commitQueue) {
            commitQueue.clear();
            commitQueue.notifyAll();
        }
    }

    public long getVersionGeneratorValue() {
        return this.versionGenerator.get();
    }

    public GlobalTransaction checkPending(Object key) {


        synchronized (commitQueue){

            Iterator<ListEntry> itr = commitQueue.iterator();
            ListEntry le = null;
            while(itr.hasNext()){
               le = itr.next();

               if(check(((TxInvocationContext) le.ctx).getModifications(), key)){
                   return le.gtx;
               }
            }

        }


        return null;
    }

    private boolean check(List<WriteCommand> writes, Object key){
        if(writes != null && key!= null){
            Iterator<WriteCommand> itr = writes.iterator();

            WriteCommand c;

            while(itr.hasNext()){
                c = itr.next();

                for(Object k: c.getAffectedKeys()){

                    if(dm.getLocality(key).isLocal() && key.equals(k)){
                        return true;
                    }
                }
            }
        }

        return false;

    }


    private static class ListEntry {
        private GlobalTransaction gtx;
        private volatile SnapshotId snapshotId;
        private InvocationContext ctx;
        private volatile boolean ready = false;
        private volatile boolean applied = false;
        private volatile long headStartTs = 0;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;

            if(getClass() == o.getClass()) {
                ListEntry listEntry = (ListEntry) o;
                return gtx != null ? gtx.equals(listEntry.gtx) : listEntry.gtx == null;
            }

            return false;
        }

        @Override
        public int hashCode() {
            return gtx != null ? gtx.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "ListEntry{gtx=" + Util.prettyPrintGlobalTransaction(gtx) + ",snapshotId=" + snapshotId + ",ctx=" +
                    ctx + ",ready?=" + ready + ",applied?=" + applied + "}";
        }
    }

    public static interface CommitInstance {
        void commit(InvocationContext ctx, SnapshotId commitSnapshotId);
        void addTransaction(SnapshotId commitSnapshotId);
    }
}
