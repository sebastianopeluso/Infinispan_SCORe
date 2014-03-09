package org.infinispan.interceptors;

import org.infinispan.CacheException;
import org.infinispan.commands.tx.PrepareCommand;
import org.infinispan.context.impl.TxInvocationContext;
import org.infinispan.mvcc.SnapshotId;
import org.infinispan.remoting.responses.ExceptionResponse;
import org.infinispan.remoting.responses.Response;
import org.infinispan.remoting.responses.SuccessfulResponse;
import org.infinispan.remoting.transport.Address;
import org.infinispan.util.Util;

import java.util.Map;

/**
 * @author pedro
 *         Date: 26-08-2011
 */
public class SerialReplicationInterceptor extends ReplicationInterceptor {
	

    @Override
    public Object visitPrepareCommand(TxInvocationContext ctx, PrepareCommand command) throws Throwable {
        Object retVal = invokeNextInterceptor(ctx, command);
        if (shouldInvokeRemoteTxCommand(ctx)) {
            //meaning: broadcast to everybody, in sync mode (we need the others vector clock) without
            //replication queue
            Map<Address, Response> responses = rpcManager.invokeRemotely(null, command, true, false);
            log.debugf("broadcast prepare command for transaction %s. responses are: %s",
                    Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()),
                    responses.toString());

            if (!responses.isEmpty()) {
                SnapshotId allPrepared = new SnapshotId();

                //process all responses
                for (Response r : responses.values()) {
                    if (r instanceof SuccessfulResponse) {
                        SnapshotId prepared = (SnapshotId) ((SuccessfulResponse) r).getResponseValue();
                        allPrepared.setMaximum(prepared);
                        log.debugf("[%s] received response %s. all vector clock together is %s",
                                Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()),
                                prepared, allPrepared);
                    } else if(r instanceof ExceptionResponse) {
                        log.debugf("[%s] received a negative response %s",
                                Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()),r);
                        throw ((ExceptionResponse) r).getException();
                    } else if(!r.isSuccessful()) {
                        log.debugf("[%s] received a negative response %s",
                                Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()),r);
                        throw new CacheException("Unsuccessful response received... aborting transaction " +
                                Util.prettyPrintGlobalTransaction(command.getGlobalTransaction()));
                    }
                }
                retVal = allPrepared;
            }
        }
        return retVal;
    }


}
