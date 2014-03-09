package org.infinispan.mvcc;


import org.infinispan.distribution.DistributionManager;
import org.infinispan.factories.annotations.Inject;
import org.infinispan.factories.annotations.Start;
import org.infinispan.factories.annotations.Stop;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author <a href="mailto:peluso@gsd.inesc-id.pt">Sebastiano Peluso</a>
 * @since 5.0
 */
public final class CommitLog {
	private static final Log log = LogFactory.getLog(CommitLog.class);


    private final AtomicReference<SnapshotId> snapshotId;   //This is because I want a read method not synchronized (isUnsafeToRead() )

    private DistributionManager dm;






	private boolean debug;

	public CommitLog() {
		snapshotId = new AtomicReference<SnapshotId>(null);


	}

	@Inject
	public void inject(DistributionManager dm){
		this.dm = dm;
	}

	///AFTER THE DistributionManagerImpl
    @Start(priority = 30)
	public void start() {
        int selfId = dm.getSelfID();

        snapshotId.set(new SnapshotId(0, selfId));




		debug = log.isDebugEnabled();
	}

	@Stop
	public void stop() {

	}

	public synchronized SnapshotId getActualSnapshotId() {

			try {
				return snapshotId.get().clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

	}

    public boolean isUnsafeToRead(SnapshotId fromSnapshotId){


        SnapshotId current = this.snapshotId.get();

        if(current.compareTo(fromSnapshotId) < 0){
            return true;
        }

        return false;



    }



	public synchronized void addNewVersion(SnapshotId other) {

        SnapshotId current = this.snapshotId.get();

        if(current.compareTo(other)>0){


            return;
        }

        this.snapshotId.set(other);


	}






}
