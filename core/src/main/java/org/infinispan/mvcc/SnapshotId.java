package org.infinispan.mvcc;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 * @author <a href="mailto:peluso@gsd.inesc-id.pt">Sebastiano Peluso</a>
 * @since 5.0
 */
public class SnapshotId implements Comparable, Cloneable, Externalizable {

    private long version;
    private int nodeId;

    public SnapshotId(){
        this.version = -1;
        this.nodeId = -1;
    }

    public SnapshotId(long version, int nodeId) {
        this.version = version;
        this.nodeId = nodeId;
    }

    public long getVersion() {
        return version;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void setMaximum(SnapshotId other){

        if(other!=null && this.compareTo(other) < 0){
            this.version = other.version;
            this.nodeId = other.nodeId;
        }
    }

    @Override
    public int compareTo(Object o) {


        if (o == null || getClass() != o.getClass()) return 1;



        SnapshotId other = (SnapshotId) o;

        if(this.version == other.version){
            if(this.nodeId == other.nodeId){
                return 0;
            }
            else if(this.nodeId<other.nodeId){
                return -1;
            }
            else{
                return 1;
            }
        }
        else if(this.version<other.version){
            return -1;
        }
        else{
            return 1;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnapshotId that = (SnapshotId) o;

        if (nodeId != that.nodeId) return false;
        if (version != that.version) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (version ^ (version >>> 32));
        result = 31 * result + nodeId;
        return result;
    }

     @Override
    public SnapshotId clone() throws CloneNotSupportedException {
    	SnapshotId dolly=(SnapshotId) super.clone();


        return dolly;
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {


        objectOutput.writeLong(this.version);
        objectOutput.writeInt(this.nodeId);

    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {

        this.version = objectInput.readLong();
        this.nodeId = objectInput.readInt();

    }

    @Override
    public String toString() {
        return "SnapshotId[version="+version+", nodeId="+nodeId+"]";
    }
}
