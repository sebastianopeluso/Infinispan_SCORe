package org.infinispan.util.concurrent.locks.containers.readwritelock;

import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static org.infinispan.util.Util.safeRelease;

/**
 * @author pedro
 *         Date: 09-08-2011
 */
public abstract class AbstractPerEntryReadWriteLockContainer implements ReadWriteLockContainer {
    //TODO: garbage collection of unused locks


    private static final Log log = LogFactory.getLog(AbstractPerEntryReadWriteLockContainer.class);
    private static final boolean DEBUG = false;

    protected ConcurrentMap<Object, ReadWriteLock> locks;

    protected AbstractPerEntryReadWriteLockContainer(int concurrencyLevel) {
        locks = new ConcurrentHashMap<Object, ReadWriteLock>(16, .75f, concurrencyLevel);
    }

    protected ReadWriteLock getLockFromMap(Object key) {
        ReadWriteLock lock = locks.get(key);
        if(lock == null) {
            lock = newLock();
            ReadWriteLock existing = locks.putIfAbsent(key, lock);
            if(existing != null) {
                lock = existing;
            }
        }
        return lock;
    }

    protected abstract ReadWriteLock newLock();
    protected abstract boolean isReadOrWriteLocked(ReadWriteLock lock);

    public final Lock getLock(Object key) {
        ReadWriteLock lock = getLockFromMap(key);
        return lock.writeLock();
    }

    @Override
    public final int getNumLocksHeld() {
        int size = 0;
        for(ReadWriteLock rwl : locks.values()) {
            if(isReadOrWriteLocked(rwl)) {
                size++;
            }
        }
        return size;
    }

    public int size() {
        return locks.size();
    }

    public final Lock acquireLock(Object key, long timeout, TimeUnit unit) throws InterruptedException {
        Lock lock = getLock(key);
        boolean locked;
        try {
            locked = lock.tryLock(timeout, unit);
        } catch (InterruptedException ie) {
            safeRelease(lock);
            throw ie;
        } catch (Throwable th) {
            safeRelease(lock);
            locked = false;
        }

        if(locked){

            if(DEBUG){

                log.debugf("Exclusive Lock. Key=%s", key);

            }

            return lock;
        }
        else{
            return null;
        }


    }

    public void releaseLock(Object key) {
        ReadWriteLock l = locks.get(key);
        if(l != null) {

            if(DEBUG){

                log.debugf("Exclusive Unlock. Key=%s", key);

            }
            l.writeLock().unlock();

        }
    }

    public int getLockId(Object key) {
        return System.identityHashCode(getLock(key));
    }

    @Override
    public final Lock getSharedLock(Object key) {
        ReadWriteLock lock = getLockFromMap(key);
        return lock.readLock();
    }

    @Override
    public final Lock acquireSharedLock(Object key, long timeout, TimeUnit unit) throws InterruptedException {
        Lock lock = getSharedLock(key);
        boolean locked;
        try {
            locked = lock.tryLock(timeout, unit);
        } catch (InterruptedException ie) {
            safeRelease(lock);
            throw ie;
        } catch (Throwable th) {
            safeRelease(lock);
            locked = false;
        }
        if (locked) {
            if(DEBUG){

                log.debugf("Shared Lock. Key=%s", key);

            }
            return lock;
        } else {
            return null;
        }
    }

    @Override
    public void releaseSharedLock(Object key) {
        ReadWriteLock l = locks.get(key);
        if(l != null) {

            if(DEBUG){

                log.debugf("Shared Unlock. Key=%s", key);

            }
            l.readLock().unlock();

        }
    }

    @Override
    public void clear() {
        locks.clear();
    }

    @Override
    public ReadWriteLock getReadWriteLock(Object key) {
        return locks.get(key);
    }
}
