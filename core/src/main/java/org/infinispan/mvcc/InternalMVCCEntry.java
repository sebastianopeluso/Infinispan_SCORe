package org.infinispan.mvcc;

import org.infinispan.container.entries.InternalCacheEntry;
import org.infinispan.marshall.AbstractExternalizer;
import org.infinispan.marshall.Ids;
import org.infinispan.util.Util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Set;

/**
 *
 * @author <a href="mailto:peluso@gsd.inesc-id.pt">Sebastiano Peluso</a>
 * @since 5.0
 */
public class InternalMVCCEntry {
    private InternalCacheEntry value;
    private SnapshotId maxSeenSnapshotId;
    private boolean mostRecent;
    private SnapshotId acquiredSnapshotId;
    private SnapshotId readSnapshotId;

    public InternalMVCCEntry(InternalCacheEntry value, SnapshotId maxSeenSnapshotId, SnapshotId acquiredSnapshotId,boolean mostRecent, SnapshotId readSnapshotId) {
        this.value = value;
        this.maxSeenSnapshotId = maxSeenSnapshotId;
        this.acquiredSnapshotId = acquiredSnapshotId;
        this.mostRecent = mostRecent;
        this.readSnapshotId = readSnapshotId;
    }

   
    public InternalMVCCEntry(SnapshotId maxSeenSnapshotId, SnapshotId acquiredSnapshotId, boolean mostRecent, SnapshotId readSnapshotId) {
        this(null, maxSeenSnapshotId, acquiredSnapshotId, mostRecent, readSnapshotId);
    }

    public InternalCacheEntry getValue() {
        
    	return value;
    }

    public SnapshotId getMaxSeenSnapshotId() {
        return this.maxSeenSnapshotId;
    }

    public boolean isMostRecent() {
        return mostRecent;
    }

    public SnapshotId getAcquiredSnapshotId() {
        return acquiredSnapshotId;
    }

    public void setAcquiredSnapshotId(SnapshotId acquiredSnapshotId) {
        this.acquiredSnapshotId = acquiredSnapshotId;
    }

    public SnapshotId getReadSnapshotId() {
        return readSnapshotId;
    }

    public void setReadSnapshotId(SnapshotId readSnapshotId) {
        this.readSnapshotId = readSnapshotId;
    }

    @Override
    public String toString() {
        return new StringBuilder("InternalMVCCEntry{")
                .append(" value=").append(value)
                .append(" ,maxSeenSnapshotId=").append(maxSeenSnapshotId)
                .append(" ,acquiredSnapshotId=").append(acquiredSnapshotId)
                .append(" ,readSnapshotId=").append(readSnapshotId)
                .append(" ,mostRecent?=").append(mostRecent)
                .append("}").toString();
    }

    public static class Externalizer extends AbstractExternalizer<InternalMVCCEntry> {

        @Override
        public Set<Class<? extends InternalMVCCEntry>> getTypeClasses() {
            return Util.<Class<? extends InternalMVCCEntry>>asSet(InternalMVCCEntry.class);
        }

        @Override
        public void writeObject(ObjectOutput output, InternalMVCCEntry object) throws IOException {
            output.writeObject(object.value);
            output.writeObject(object.maxSeenSnapshotId);
            output.writeBoolean(object.mostRecent);


            if(object.acquiredSnapshotId != null){
                output.writeBoolean(true);

            }
            else{
                output.writeBoolean(false);
            }

            if(object.readSnapshotId != null){
                output.writeBoolean(true);

            }
            else{
                output.writeBoolean(false);
            }

            if(object.acquiredSnapshotId != null){

                output.writeObject(object.acquiredSnapshotId);
            }

            if(object.readSnapshotId != null){

                output.writeObject(object.readSnapshotId);
            }

        }

        @Override
        public InternalMVCCEntry readObject(ObjectInput input) throws IOException, ClassNotFoundException {
            InternalCacheEntry ice = (InternalCacheEntry) input.readObject();
            SnapshotId maxSeenSnapshotId = (SnapshotId) input.readObject();
            boolean mostRecent = input.readBoolean();

            boolean newSnapshotIdAcquired = input.readBoolean();
            boolean validReadSnapshotId = input.readBoolean();


            SnapshotId acquiredSnapshotId = null;
            SnapshotId readSnapshotId = null;

            if(newSnapshotIdAcquired){
                 acquiredSnapshotId = (SnapshotId) input.readObject();
            }
            if(validReadSnapshotId){
                readSnapshotId = (SnapshotId) input.readObject();
            }

            return new InternalMVCCEntry(ice, maxSeenSnapshotId, acquiredSnapshotId,mostRecent, readSnapshotId);
        }

        @Override
        public Integer getId() {
            return Ids.INTERNAL_MVCC_ENTRY;
        }
    }
}
