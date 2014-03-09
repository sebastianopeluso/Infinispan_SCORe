package org.infinispan.util.concurrent.locks.readwritelock;

import org.infinispan.context.InvocationContextContainer;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author pedro
 *         Date: 25-07-2011
 */
public class OwnableReentrantReadWriteLock implements ReadWriteLock {
    private static final Log log = LogFactory.getLog(OwnableReentrantReadWriteLock.class);

    private static final boolean DEBUG = false;

    private  Map<Object, Integer> readers;
    private volatile Object writer;
    private  int writeAccesses;

    private  long noTxReadAccesses; //This is for shared lock/unlock performed by remote (hence non-transactional in Infinispan) visible reads.

    private final Object mutex = new Object();

    private transient InvocationContextContainer icc;

    private final Lock readLock = new Lock() {
        @Override
        public void lock() {
            Object requestor = icc.getInvocationContext().getLockOwner();
            synchronized (mutex) {
                while(!canGrantReadAccess(requestor)) {
                    try {
                        mutex.wait();
                    } catch (InterruptedException e) {
                        //ignore
                    }
                }

                if(!(requestor instanceof Thread)){
                    int accesses = getReadAccessCount(requestor) + 1;
                    readers.put(requestor, accesses);
                }
                else{
                    //This is a non-transactional (remote) visible read
                    noTxReadAccesses++;

                }
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            Object requestor = icc.getInvocationContext().getLockOwner();
            synchronized (mutex) {
                while(!canGrantReadAccess(requestor)) {
                    mutex.wait();
                }



                if(!(requestor instanceof Thread)){
                    int accesses = getReadAccessCount(requestor) + 1;
                    readers.put(requestor, accesses);
                }
                else{
                    //This is a non-transactional (remote) visible read
                    noTxReadAccesses++;

                }
            }
        }

        @Override
        public boolean tryLock() {
            Object requestor = icc.getInvocationContext().getLockOwner();
            synchronized (mutex) {
                if(canGrantReadAccess(requestor)) {
                    if(!(requestor instanceof Thread)){
                        int accesses = getReadAccessCount(requestor) + 1;
                        readers.put(requestor, accesses);
                    }
                    else{
                        //This is a non-transactional (remote) visible read
                        noTxReadAccesses++;

                    }

                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
            Object requestor = icc.getInvocationContext().getLockOwner();
            synchronized (mutex) {
                if(canGrantReadAccess(requestor)) {

                    if(!(requestor instanceof Thread)){
                        int accesses = getReadAccessCount(requestor) + 1;
                        readers.put(requestor, accesses);
                    }
                    else{
                        //This is a non-transactional (remote) visible read
                        noTxReadAccesses++;

                    }

                    if(DEBUG){
                        log.debugf(""+System.nanoTime()+" LockStatus: writer="+writer+"; writeAccesses="+writeAccesses+"; noTxReadAccesses="+noTxReadAccesses+"; readers="+readers);
                    }

                    return true;
                }
                mutex.wait(timeUnit.toMillis(l),0);


                if(canGrantReadAccess(requestor)) {
                    if(!(requestor instanceof Thread)){
                        int accesses = getReadAccessCount(requestor) + 1;
                        readers.put(requestor, accesses);
                    }
                    else{
                        //This is a non-transactional (remote) visible read
                        noTxReadAccesses++;

                    }
                    if(DEBUG){
                        log.debugf(""+System.nanoTime()+" LockStatus: writer="+writer+"; writeAccesses="+writeAccesses+"; noTxReadAccesses="+noTxReadAccesses+"; readers="+readers);
                    }

                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public void unlock() {
            Object requestor = icc.getInvocationContext().getLockOwner();
            synchronized (mutex) {



                if(!(requestor instanceof Thread)){

                    if(!readers.containsKey(requestor)) {

                        if(DEBUG){
                        log.debugf(""+System.nanoTime()+" LockStatus: writer="+writer+"; writeAccesses="+writeAccesses+"; noTxReadAccesses="+noTxReadAccesses+"; readers="+readers);
                        }

                        return ;// throw new IllegalMonitorStateException() //pshiiuuu!!
                    }
                    int i = getReadAccessCount(requestor) - 1;
                    if(i == 0) {
                        readers.remove(requestor);
                    } else {
                        readers.put(requestor,i);
                    }

                    if(DEBUG){
                        log.debugf(""+System.nanoTime()+" LockStatus: writer="+writer+"; writeAccesses="+writeAccesses+"; noTxReadAccesses="+noTxReadAccesses+"; readers="+readers);
                    }


                    mutex.notifyAll();

                }
                else{

                    noTxReadAccesses--; //Pay attention. In the case of non-transactional visible reads we don't want to identify the owner.

                    if(DEBUG){
                        log.debugf(""+System.nanoTime()+" LockStatus: writer="+writer+"; writeAccesses="+writeAccesses+"; noTxReadAccesses="+noTxReadAccesses+"; readers="+readers);
                    }

                    mutex.notifyAll();

                }
            }
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    };

    private final Lock writeLock = new Lock() {
        @Override
        public void lock() {
            Object requestor = icc.getInvocationContext().getLockOwner();
            synchronized (mutex) {
                while(!canGrantWriteAccess(requestor)) {
                    try {
                        mutex.wait();
                    } catch (InterruptedException e) {
                        //ignore
                    }
                }
                writeAccesses++;
                writer = requestor;
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            Object requestor = icc.getInvocationContext().getLockOwner();
            synchronized (mutex) {
                while(!canGrantWriteAccess(requestor)) {
                    mutex.wait();
                }
                writeAccesses++;
                writer = requestor;
            }
        }

        @Override
        public boolean tryLock() {
            Object requestor = icc.getInvocationContext().getLockOwner();
            synchronized (mutex) {
                if(canGrantWriteAccess(requestor)) {
                    writeAccesses++;
                    writer = requestor;
                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
            Object requestor = icc.getInvocationContext().getLockOwner();
            synchronized (mutex) {
                if(canGrantWriteAccess(requestor)) {
                    writeAccesses++;
                    writer = requestor;

                    if(DEBUG){
                        log.debugf(""+System.nanoTime()+" LockStatus: writer="+writer+"; writeAccesses="+writeAccesses+"; noTxReadAccesses="+noTxReadAccesses+"; readers="+readers);
                    }



                    return true;
                }
                mutex.wait(timeUnit.toMillis(l),0);
                if(canGrantWriteAccess(requestor)) {
                    writeAccesses++;
                    writer = requestor;

                    if(DEBUG){
                        log.debugf(""+System.nanoTime()+" LockStatus: writer="+writer+"; writeAccesses="+writeAccesses+"; noTxReadAccesses="+noTxReadAccesses+"; readers="+readers);
                    }


                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public void unlock() {
            Object requestor = icc.getInvocationContext().getLockOwner();
            synchronized (mutex) {
                if(writer == null || !writer.equals(requestor)) {

                    if(DEBUG){
                        log.debugf(""+System.nanoTime()+" LockStatus: writer="+writer+"; writeAccesses="+writeAccesses+"; noTxReadAccesses="+noTxReadAccesses+"; readers="+readers);
                    }


                    return ;// throw new IllegalMonitorStateException() //pshiiuuu!!
                }

                writeAccesses--;

                if(writeAccesses == 0) {
                    writer = null;
                }

                if(DEBUG){
                        log.debugf(""+System.nanoTime()+" LockStatus: writer="+writer+"; writeAccesses="+writeAccesses+"; noTxReadAccesses="+noTxReadAccesses+"; readers="+readers);
                }


                mutex.notifyAll();
            }
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    };

    public OwnableReentrantReadWriteLock(InvocationContextContainer icc) {
        readers = new HashMap<Object, Integer>();
        writer = null;
        writeAccesses = 0;
        noTxReadAccesses = 0;
        this.icc = icc;
    }


    //------------ readers functions -------------------//
    private boolean canGrantReadAccess(Object req) {

        if(!(req instanceof Thread)){
            if(writer != null && writer.equals(req)) {
                return true;
            } else if(writer != null) {
                return false;
            }
            return true;

        }
        else{ //Visible read executed in non-transactional context

            if(writer!=null){
                return false;
            }



            return true;


        }
    }

    private int getReadAccessCount(Object req) {
        Integer i = readers.get(req);
        return i != null ? i : 0;
    }


    //-------------- writer functions ------------------ //
    private boolean canGrantWriteAccess(Object req) {

       if(noTxReadAccesses>0){      //A remote visibile read is not completed yet.
           return false;
       }


        if(readers.size() == 1 && readers.containsKey(req)) {
            return true;
        } else if(!readers.isEmpty()) {
            return false;
        } else if(writer == null) {
            return true;
        }

        return writer.equals(req);
    }

    @Override
    public Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }

    public boolean isReadOrWriteLocked() {
        return writer != null || !readers.isEmpty();
    }

    public boolean isWriteLock() {
        return writer != null;
    }

    public Object getOwner() {
        return writer;
    }

    @Override
    public String toString() {
        return new StringBuilder("OwnableReentrantReadWriteLock{")
                .append("readers=").append(readers.keySet())
                .append("writer=").append(writer)
                .append("}").toString();
    }

    public boolean hasReadOrWriteLock(Object owner) {
        return owner != null  && (owner.equals(writer) || readers.containsKey(owner));
    }
}
