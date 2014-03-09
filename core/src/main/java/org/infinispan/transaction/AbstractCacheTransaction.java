/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.infinispan.transaction;

import org.infinispan.commands.write.WriteCommand;
import org.infinispan.container.entries.CacheEntry;
import org.infinispan.context.impl.LocalTxInvocationContext;
import org.infinispan.mvcc.*;
import org.infinispan.transaction.xa.CacheTransaction;
import org.infinispan.transaction.xa.GlobalTransaction;
import org.infinispan.util.BidirectionalLinkedHashMap;
import org.infinispan.util.BidirectionalMap;
import org.infinispan.util.Util;

import java.util.*;

/**
 * Base class for local and remote transaction.
 * Impl note: The aggregated modification list and lookedUpEntries are not instantiated here but in subclasses.
 * This is done in order to take advantage of the fact that, for remote transactions we already know the size of the
 * modifications list at creation time.
 *
 * @author Mircea.Markus@jboss.com
 * @author Galder Zamarreño
 * @since 4.2
 */
public abstract class AbstractCacheTransaction implements CacheTransaction {

    protected List<WriteCommand> modifications;
    protected BidirectionalLinkedHashMap<Object, CacheEntry> lookedUpEntries;
    protected GlobalTransaction tx;
    protected Set<Object> affectedKeys = null;

    //changes by Pedro & Sebastiano: added a read-set, bit set and version and their manipulation
    
    protected Deque<ReadSetEntry> localReadSet;
    protected Deque<ReadSetEntry> remoteReadSet;
    
    
    protected BitSet alreadyRead = new BitSet();
    //protected BitSet realAlreadyRead = new BitSet();



    protected boolean firstReadPerformed=false;
    
    protected CacheEntry lastReadKey = null;
    
    
    protected SnapshotId snapshotId;

    protected SnapshotId maxSeenSnapshotId;

    protected volatile boolean prepared;

    public GlobalTransaction getGlobalTransaction() {
        return tx;
    }

    public List<WriteCommand> getModifications() {
        return modifications;
    }

    public void setModifications(WriteCommand[] modifications) {
        this.modifications = Arrays.asList(modifications);
    }

    public BidirectionalMap<Object, CacheEntry> getLookedUpEntries() {
        return lookedUpEntries;
    }

    public CacheEntry lookupEntry(Object key) {
        if (lookedUpEntries == null) return null;
        return lookedUpEntries.get(key);
    }

    public void removeLookedUpEntry(Object key) {
        if (lookedUpEntries != null) lookedUpEntries.remove(key);
    }

    public void clearLookedUpEntries() {
        if (lookedUpEntries != null) lookedUpEntries.clear();
    }

    public void setLookedUpEntries(BidirectionalMap<Object, CacheEntry> lookedUpEntries) {
        this.lookedUpEntries = new BidirectionalLinkedHashMap<Object, CacheEntry>(lookedUpEntries);
    }

    public Set<Object> getAffectedKeys() {
        return affectedKeys == null ? Collections.emptySet() : affectedKeys;
    }

    public void setAffectedKeys(Set<Object> affectedKeys) {
        this.affectedKeys = affectedKeys;
    }

    public SnapshotId getSnapshotId(){
        return this.snapshotId;
    }

    public void setSnapshotId(SnapshotId snapshotId){

        this.snapshotId = snapshotId;
        this.maxSeenSnapshotId = snapshotId;

    //    logSnapshotId();
    }

    //public abstract void logSnapshotId();


    public InternalMVCCEntry getLocalReadKey(Object key) {
        return localReadSet == null ? null : find(localReadSet, key);
    }
    
    public InternalMVCCEntry getRemoteReadKey(Object key) {
        return remoteReadSet == null ? null : find(remoteReadSet, key);
    }

    public Object[] getLocalReadSet() {
    	return filterKeys(localReadSet);
    }
    
    public Object[] getRemoteReadSet() {
    	
        return filterKeys(remoteReadSet);
    }
    
    private InternalMVCCEntry find(Deque<ReadSetEntry> d, Object key){
    	if(d != null){
        	Iterator<ReadSetEntry> itr = d.descendingIterator();
        	while(itr.hasNext()){
        		ReadSetEntry entry = itr.next();
        		
        		if(entry.getKey() != null && entry.getKey().equals(key)){
        			return entry.getIme();
        		}
        	}
        }
    	
    	return null;
    }
    
    private Object[] filterKeys(Deque<ReadSetEntry> d){
    	
    	if(d == null || d.isEmpty()) return null;
    	
    	
    	Object[] result = new Object[d.size()];
    	
        Iterator<ReadSetEntry> itr = d.iterator();
        int i=0;
        while(itr.hasNext()){
        		result[i] = itr.next().getKey();
        		
        		i++;
        }
        
    	
    	return result;
    }

    public abstract void addLocalReadKey(Object key, InternalMVCCEntry ime);
    
    public abstract void removeLocalReadKey(Object key);
    
    public abstract void removeRemoteReadKey(Object key);
    
    public abstract void addRemoteReadKey(Object key, InternalMVCCEntry ime);

    public boolean hasAlreadyReadFrom(int idx) {
        return alreadyRead != null && alreadyRead.get(idx);
    }

    public void setAlreadyRead(int idx) {
        alreadyRead.set(idx);
    }
    
    
    
    public BitSet getAlreadyRead(){
    	return (BitSet) alreadyRead.clone();
    }

    public void initSnapshotId(SnapshotId newSnapshotId) {

        this.snapshotId = newSnapshotId;
        this.maxSeenSnapshotId = newSnapshotId;

        this.firstReadPerformed = false; //Possibility to change the snapshotId till the first read is performed
        
    }

    public void setFirstReadPerformed(boolean firstReadPerformed) {
        this.firstReadPerformed = firstReadPerformed;
    }

    public boolean isFirstReadPerformed() {
        return firstReadPerformed;
    }

    public void addSeenSnapshotId(SnapshotId mostRecentSeenSnapshotId) {
        if(this.maxSeenSnapshotId.compareTo(mostRecentSeenSnapshotId) < 0){
            this.maxSeenSnapshotId = mostRecentSeenSnapshotId;
        }

    }

    public SnapshotId getSeenSnapshotId() {
        return this.maxSeenSnapshotId;
    }
}
