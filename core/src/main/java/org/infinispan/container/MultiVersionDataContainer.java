package org.infinispan.container;

import org.infinispan.container.entries.InternalCacheEntry;
import org.infinispan.container.entries.InternalEntryFactory;
import org.infinispan.distribution.DistributionManager;
import org.infinispan.factories.annotations.Inject;
import org.infinispan.factories.annotations.Start;
import org.infinispan.factories.annotations.Stop;
import org.infinispan.mvcc.*;
import org.infinispan.util.Immutables;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author pedro
 * @author <a href="mailto:peluso@gsd.inesc-id.pt">Sebastiano Peluso</a>
 * @since 5.0
 */
public class MultiVersionDataContainer implements DataContainer {
    /*
     * This class is not finished yet! it has some bugs and I don't know how to expire the key
     * Beside that, a garbage collector is needed to remove old versions
     * ... and some other things that I don't remember
     *
     * note: this class has big performance issue. it is recomended to redo it in a better way
     */

    private static final Log log = LogFactory.getLog(MultiVersionDataContainer.class);

    private CommitLog commitLog;
    private final InternalEntryFactory entryFactory;
    private final ConcurrentMap<Object, VBox> entries;
    
    


    private boolean trace, debug, info;

    public MultiVersionDataContainer(int concurrencyLevel) {



        log.info("SOCS algorithm");


        entryFactory = new InternalEntryFactory();
        entries = new ConcurrentHashMap<Object, VBox>(128, 0.75f, concurrencyLevel);
    }
    


    @Start
    public void setLogBoolean() {
        trace = log.isTraceEnabled();
        debug = log.isDebugEnabled();
        info = log.isInfoEnabled();


    }

    @Stop
    public void stop() {
        entries.clear();
    }

    private InternalMVCCEntry wrap(VBox vbox, SnapshotId maxSeenSnapshotId,  boolean mostRecent, boolean touch, boolean ignoreExpire) {
        if(vbox == null) {
            return new InternalMVCCEntry(maxSeenSnapshotId, null,mostRecent, null);
        } else if(mostRecent && vbox.isExpired() && !ignoreExpire) {
            return new InternalMVCCEntry(maxSeenSnapshotId, null,mostRecent, new SnapshotId(vbox.getVersion(), vbox.getVersionRank()));
        }
        return new InternalMVCCEntry(vbox.getValue(touch), maxSeenSnapshotId, null, mostRecent, new SnapshotId(vbox.getVersion(), vbox.getVersionRank()));
    }

    private VBox getFromMap(Object k, long version, int versionRank) {
        VBox vbox = entries.get(k);
        
        if(version < 0){
        	return vbox;
        }
        
        while(vbox != null) {
            if(vbox.version < version || ((vbox.version == version) && vbox.versionRank <= versionRank)) {
                break;
            } else {
                vbox = vbox.previous;
            }
        }
        return vbox;
    }



    @Inject
    public void inject(CommitLog commitLog) {
        this.commitLog = commitLog;
    }

    @Override
    public InternalCacheEntry get(Object k) {
        return get(k, null, true).getValue();
    }

    @Override
    public InternalCacheEntry peek(Object k) {
        return peek(k, null, true).getValue();
    }

    @Override
    public void put(Object k, Object v, long lifespan, long maxIdle) {
        put(k, v, lifespan, maxIdle, commitLog.getActualSnapshotId());
    }

    @Override
    public boolean containsKey(Object k) {
        InternalCacheEntry ice = peek(k, null, true).getValue();
        return ice != null;
    }

    @Override
    public InternalCacheEntry remove(Object k) {
        return remove(k, commitLog.getActualSnapshotId());
    }

    @Override
    public int size() {
        return size(null);
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    @Override
    public Collection<Object> values() {
        return values(null);
    }

    @Override
    public Set<InternalCacheEntry> entrySet() {
        return new EntrySet(null);
    }

    @Override
    public void purgeExpired() {
        purgeExpired(null);
    }

    @Override
    public InternalMVCCEntry get(Object k, SnapshotId snapshotId, boolean fromRemote) {

        VBox vbox;
        if(snapshotId!=null){
            vbox = getFromMap(k,snapshotId.getVersion(), snapshotId.getNodeId());
        }
        else{
            vbox = getFromMap(k,-1, -1);
        }


        InternalMVCCEntry ime = wrap(vbox, fromRemote?commitLog.getActualSnapshotId():snapshotId, vbox == entries.get(k), true, false);


        return ime;

    }



    @Override
    public InternalMVCCEntry peek(Object k, SnapshotId snapshotId, boolean fromRemote) {
    	
    	VBox vbox;
        if(snapshotId!=null){
            vbox = getFromMap(k,snapshotId.getVersion(), snapshotId.getNodeId());
        }
        else{
            vbox = getFromMap(k,-1, -1);
        }
        

        InternalMVCCEntry ime = wrap(vbox, fromRemote?commitLog.getActualSnapshotId():snapshotId, vbox == entries.get(k), false, true);

        return ime;
    }

    @Override
    public void put(Object k, Object v, long lifespan, long maxIdle, SnapshotId snapshotId) {

    	
    	
        VBox prev = entries.get(k);
        InternalCacheEntry e = entryFactory.createNewEntry(k, v, lifespan, maxIdle);
        VBox newVbox = new VBox(snapshotId.getVersion(), snapshotId.getNodeId(), e, prev);

      //  synchronized (entries) {
            //if the entry does not exist
            if(prev == null) {
                prev = entries.putIfAbsent(k, newVbox);
                if(prev == null) {
                    if(debug) {
                        log.debugf("added new value to key [%s] with version %s and value %s", k, newVbox.getVersion(), v);
                    }
                    return ;
                }
                //ops... maybe it exists now... lets replace it
                newVbox.setPrevious(prev);
                newVbox.updatedVersion();
            }

            while(!entries.replace(k, prev, newVbox)) {
                prev = entries.get(k);
                newVbox.setPrevious(prev);
                newVbox.updatedVersion();
            }
            if(debug) {
                log.debugf("added new value to key [%s] with version %s and value %s", k, newVbox.getVersion(), v);
            }
      //  }
    }

    @Override
    public boolean containsKey(Object k, SnapshotId snapshotId) {


    	VBox vbox;
        if(snapshotId!=null){
            vbox = getFromMap(k,snapshotId.getVersion(), snapshotId.getNodeId());
        }
        else{
            vbox = getFromMap(k,-1, -1);
        }


        InternalMVCCEntry ime = wrap(vbox, snapshotId, vbox == entries.get(k), false, false);
        if(ime.getValue() == null) {

            return false;
        }

        return true;
    }

    @Override
    public InternalCacheEntry remove(Object k, SnapshotId snapshotId) {
        VBox prev = entries.get(k);
        VBox newVbox = new VBox(snapshotId.getVersion(), snapshotId.getNodeId(), null, prev);

        //if the entry does not exist
        if(prev == null) {
            prev = entries.putIfAbsent(k, newVbox);
            if(prev == null) {
                if(debug) {
                    log.debugf("Remove key [%s]. Create empty value with version %s",
                            k, snapshotId);
                }
                return null;
            }
            //ops... maybe it exists now... lets replace it
            newVbox.setPrevious(prev);
            newVbox.updatedVersion();
        }

        while(!entries.replace(k, prev, newVbox)) {
            prev = entries.get(k);
            newVbox.setPrevious(prev);
            newVbox.updatedVersion();
        }

        if(debug) {
            log.debugf("Remove key [%s]. Create empty value with version %s",
                    k, newVbox.getVersion());
        }
        return prev == null || prev.getValue(false) == null || prev.getValue(false).isExpired() ? null : prev.getValue(false);
    }

    @Override
    public int size(SnapshotId snapshotId) {
        Set<Object> keys = entries.keySet();
        int size = 0;
        for(Object k : keys) {
            InternalMVCCEntry ime = peek(k, snapshotId, false);
            if(ime.getValue() != null) {
                size++;
            }
        }

        return size;
    }

    @Override
    public void clear(SnapshotId snapshotId) {
        if(trace) {
            log.tracef("Clear the map (i.e. remove all key by putting a new empty value version");
        }
        Set<Object> keys = entries.keySet();
        for(Object k : keys) {
            remove(k,snapshotId);
        }
    }

    @Override
    public Set<Object> keySet(SnapshotId snapshotId) {
        Set<Object> result = new HashSet<Object>();


        for(Map.Entry<Object, VBox> entry : entries.entrySet()) {
            Object key = entry.getKey();
            VBox value = entry.getValue();
            
            
            if(snapshotId == null){
            	result.add(key);
            	continue;
            }
            
            while(value != null) {
                if(value.version < snapshotId.getVersion() || ((value.version == snapshotId.getVersion()) && value.versionRank <= snapshotId.getNodeId())) {
                	result.add(key);
                    break;
                } else {
                    value = value.getPrevious();
                }
            }
            
        }



        return Collections.unmodifiableSet(result);
    }

    @Override
    public Collection<Object> values(SnapshotId snapshotId) {

        return new Values(snapshotId, size(snapshotId));
    }

    @Override
    public Set<InternalCacheEntry> entrySet(SnapshotId snapshotId) {

        return new EntrySet(snapshotId);
    }

    @Override
    public void purgeExpired(SnapshotId snapshotId) {

        
        for (Iterator<VBox> purgeCandidates = entries.values().iterator(); purgeCandidates.hasNext();) {
            VBox vbox = purgeCandidates.next();
            if ((vbox.version < snapshotId.getVersion() || ((vbox.version == snapshotId.getVersion()) && vbox.versionRank <= snapshotId.getNodeId())) && vbox.isExpired()) {
                purgeCandidates.remove();
            }
        }
    }

    @Override
    public Iterator<InternalCacheEntry> iterator() {

        return new EntryIterator(new VBoxIterator(entries.values().iterator(), commitLog.getActualSnapshotId()));
    }

    public void addNewCommittedTransaction(SnapshotId snapshotId) {
        commitLog.addNewVersion(snapshotId);
    }


    @Override
    public boolean validateKey(Object key, SnapshotId snapshotId) {
        VBox actual = entries.get(key);
        if(actual == null) {
            if(debug) {
                log.debugf("Validate key [%s], but it is null in data container. return true", key);
            }
            return true;
        }

        



        return (actual.version < snapshotId.getVersion() || ((actual.version == snapshotId.getVersion()) && actual.versionRank <= snapshotId.getNodeId()));
    }

    /**
     * Minimal implementation needed for unmodifiable Collection
     *
     */
    private class Values extends AbstractCollection<Object> {
        private SnapshotId snapshotId;
        private int size;

        private Values(SnapshotId snapshotId, int size) {
            this.snapshotId = snapshotId;
            this.size = size;
        }

        @Override
        public Iterator<Object> iterator() {
            return new ValueIterator(new VBoxIterator(entries.values().iterator(), snapshotId));
        }

        @Override
        public int size() {
            return size;
        }
    }

    private static class ValueIterator implements Iterator<Object> {
        Iterator<VBox> currentIterator;

        private ValueIterator(Iterator<VBox> it) {
            currentIterator = it;
        }

        public boolean hasNext() {
            return currentIterator.hasNext();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Object next() {
            return currentIterator.next().getValue(false).getValue();
        }
    }

    private static class EntryIterator implements Iterator<InternalCacheEntry> {
        Iterator<VBox> currentIterator;

        private EntryIterator(Iterator<VBox> it) {
            currentIterator = it;
        }

        public boolean hasNext() {
            return currentIterator.hasNext();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public InternalCacheEntry next() {
            return currentIterator.next().getValue(false);
        }
    }

    private static class ImmutableEntryIterator extends EntryIterator {
        ImmutableEntryIterator(Iterator<VBox> it){
            super(it);
        }

        @Override
        public InternalCacheEntry next() {
            return Immutables.immutableInternalCacheEntry(super.next());
        }
    }

    private class EntrySet extends AbstractSet<InternalCacheEntry> {

        private SnapshotId snapshotId;
        public EntrySet(SnapshotId snapshotId) {
            this.snapshotId = snapshotId;
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }

            @SuppressWarnings("rawtypes")
            Map.Entry e = (Map.Entry) o;
            InternalCacheEntry ice;
            if(snapshotId == null) {
                ice = get(e.getKey());
            } else {
                ice = get(e.getKey(), snapshotId, false).getValue();
            }

            return ice != null && ice.getValue().equals(e.getValue());
        }

        @Override
        public Iterator<InternalCacheEntry> iterator() {
            if(snapshotId != null) {
                return new ImmutableEntryIterator(new VBoxIterator(entries.values().iterator(), snapshotId));
            } else {
                return new ImmutableEntryIterator(new VBoxIterator(entries.values().iterator(), commitLog.getActualSnapshotId()));
            }
        }

        @Override
        public int size() {
            return entries.size();
        }
    }

    private static class VBoxIterator implements Iterator<VBox> {
        Iterator<VBox> currentIterator;
        VBox next;
        SnapshotId snapshotId;

        private VBoxIterator(Iterator<VBox> it, SnapshotId  snapshotId) {
            currentIterator = it;
            next = null;
            this.snapshotId = snapshotId;
            findNext();
        }

        public boolean hasNext() {
            return next != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public VBox next() {
            if(next == null) {
                throw new NoSuchElementException();
            }
            VBox vbox = next;
            findNext();
            return vbox;
        }

        private void findNext() {
            next = null;
            while(currentIterator.hasNext()) {
                VBox vbox = currentIterator.next();
                while(vbox != null) {
                    if(vbox.version < snapshotId.getVersion() || ((vbox.version == snapshotId.getVersion()) && vbox.versionRank <= snapshotId.getNodeId())) {
                        next = vbox;
                        return;
                    } else {
                        vbox = vbox.getPrevious();
                    }
                }
            }
        }
    }
    
    
    /**
     * @author pedro
     * @author <a href="mailto:peluso@gsd.inesc-id.pt">Sebastiano Peluso</a>
     * @since 5.0        
     */
     class VBox {
    	

        long version;  //Version to identify the VBox
        int versionRank; //Identifier used to manage parity in the values between two version fields
        InternalCacheEntry value;
        VBox previous;

        public VBox(long version, int versionRank, InternalCacheEntry value, VBox previous) {
            this.version = version;
            this.value = value;
            this.versionRank = versionRank;
            this.previous = previous;
            updatedVersion();
        }

        public long getVersion() {
            return version;
        }

        public int getVersionRank(){
            return this.versionRank;
        }

        public InternalCacheEntry getValue(boolean touch) {
            if(touch && value != null) {
                value.touch();
            }
            return value;
        }

        public VBox getPrevious() {
            return previous;
        }

        public void setPrevious(VBox previous) {
            this.previous = previous;
        }

        public boolean isExpired() {
            return value != null && value.isExpired();
        }
        
        public void updatedVersion() {
        	
        	if(previous == null) {
        		return ;
        	}
        		
        	//We need this line for non-transactional writes
        	if(previous.version > version){
        		version = previous.version;
        		versionRank = previous.versionRank;
        	}
            else if(previous.version == version && previous.versionRank > versionRank){
                versionRank = previous.versionRank;
            }
        	
        		
        	
        }
   

        public String getVBoxChain() {
            return new StringBuilder("VBox{")
                    .append("version=").append(version)
                    .append("versionRank=").append(versionRank)
                    .append(",value=").append(value).append("};")
                    .append(previous != null ? previous.getVBoxChain() : "null")
                    .toString();
        }

        @Override
        public String toString() {
            return new StringBuilder("VBox{")
                    .append("version=").append(version)
                    .append("versionRank=").append(versionRank)
                    .append(",value=").append(value)
                    .append('}').toString();
        }
    }
}
