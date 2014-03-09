
package org.infinispan.util.logging;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import javax.annotation.Generated;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.ObjectName;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import org.infinispan.CacheException;
import org.infinispan.commands.tx.PrepareCommand;
import org.infinispan.commands.write.WriteCommand;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.loaders.bucket.Bucket;
import org.infinispan.loaders.decorators.SingletonStore.PushStateException;
import org.infinispan.statetransfer.StateTransferException;
import org.infinispan.transaction.xa.GlobalTransaction;
import org.infinispan.transaction.xa.LocalXaTransaction;
import org.infinispan.transaction.xa.recovery.RecoveryAwareRemoteTransaction;
import org.infinispan.transaction.xa.recovery.RecoveryAwareTransaction;
import org.infinispan.util.TypedProperties;
import org.jboss.logging.Logger;


/**
 * Warning this class consists of generated code.
 * 
 */
@Generated(value = "org.jboss.logging.model.MessageLoggerImplementor", date = "2013-04-16T23:17:50+0200")
public class Log_$logger implements Serializable, Log
{

    private final static long serialVersionUID = 1L;
    protected final Logger log;
    private final static String projectCode = "ISPN";
    private final static String FQCN = Log_$logger.class.getName();
    private final static String couldNotResolveConfigurationSchema = "Infinispan configuration schema could not be resolved locally nor fetched from URL. Local path=%s, schema path=%s, schema URL=%s";
    private final static String problemsRemovingFile = "Had problems removing file %s";
    private final static String unableToReadVersionId = "Unable to read version id from first two bytes of stream, barfing.";
    private final static String unableToClearAsyncStore = "Clear() operation in async store could not be performed";
    private final static String errorSavingBucket = "Exception while saving bucket %s";
    private final static String unableToConvertStringPropertyToInt = "Unable to convert string property [%s] to an int!  Using default value of %d";
    private final static String unableToRollbackGlobalTx = "Unable to roll back global transaction %s";
    private final static String participatingInRehash = "I %s am participating in rehash, state providers %s, state receivers %s";
    private final static String streamingStateTransferNotPresent = "Channel does not contain STREAMING_STATE_TRANSFER.  Cannot support state transfers!";
    private final static String unexpectedErrorFromResourceManager = "Unexpected error from resource manager!";
    private final static String localExecutionFailed = "Failed local execution ";
    private final static String errorChangingSingletonStoreStatus = "Exception reported changing cache active status";
    private final static String noAnnotateMethodsFoundInListener = "Attempted to register listener of class %s, but no valid, public methods annotated with method-level event annotations found! Ignoring listener.";
    private final static String unableToLoadFromCacheLoader = "Unable to load %s from cache loader";
    private final static String errorCreatingChannelFromXML = "Error while trying to create a channel using config XML: %s";
    private final static String unexpectedErrorReplicating = "Unexpected error while replicating";
    private final static String unableToProcessLeaver = "Unable to process leaver!!";
    private final static String channelNotSetUp = "Channel not set up properly!";
    private final static String failedToInvalidateKeys = "Error invalidating keys from L1 after rehash";
    private final static String recoveryIgnored = "Recovery call will be ignored as recovery is disabled. More on recovery: http://community.jboss.org/docs/DOC-16646";
    private final static String queriedAttributeNotFound = "Did not find queried attribute with name %s";
    private final static String viewChangeDetected = "Detected a view change.  Member list changed from %s to %s";
    private final static String errorPurgingAsyncStore = "Error performing purging expired from async store";
    private final static String unableToUnregisterMBeanWithPattern = "Unable to unregister Cache MBeans with pattern %s";
    private final static String receivedMergedView = "Received new, MERGED cluster view: %s";
    private final static String writeManagedAttributeAlreadyPresent = "Not adding annotated method %s since we already have writable attribute";
    private final static String unableToCopyEntryForUpdate = "Detected write skew on key [%s].  Another process has changed the entry since we last read it!  Unable to copy entry for update.";
    private final static String passivatedEntries = "Passivated %d entries in %s";
    private final static String asyncStoreCoordinatorInterrupted = "AsyncStoreCoordinator interrupted";
    private final static String localAndPhysicalAddress = "Cache local address is %s, physical addresses are %s";
    private final static String unableToSetValue = "Unable to set value!";
    private final static String leaveEvent = "This is a LEAVE event!  Node %s has just left";
    private final static String joinEvent = "This is a JOIN event!  Wait for notification from new joiner %s";
    private final static String couldNotFindPeerForState = "Could not find available peer for state, backing off and retrying";
    private final static String errorRollingBack = "Exception while rollback";
    private final static String passivatingAllEntries = "Passivating all entries to disk";
    private final static String stopBeforeDestroyFailed = "Needed to call stop() before destroying but stop() threw exception. Proceeding to destroy";
    private final static String distributionManagerNotJoined = "DistributionManager not yet joined the cluster.  Cannot do anything about other concurrent joiners.";
    private final static String unableToPassivateEntry = "Unable to passivate entry under %s";
    private final static String notStartingEvictionThread = "wakeUpInterval is <= 0, not starting eviction thread";
    private final static String keyDoesNotMapToLocalNode = "Received a key that doesn't map to this node: %s, mapped to %s";
    private final static String errorCommittingTx = "exception while committing";
    private final static String msgOrMsgBufferEmpty = "Message or message buffer is null or empty.";
    private final static String viewChangeInterrupted = "View change interrupted; not rehashing!";
    private final static String errorReadingFromFile = "Error while reading from file: %s";
    private final static String invalidManagedAttributeMethod = "Method %s must have a valid return type and zero parameters";
    private final static String unsupportedTransactionConfiguration = "Unsupported combination (dldEnabled, recoveryEnabled, xa) = (%s, %s, %s)";
    private final static String failedSynchronizationRegistration = "Failed synchronization registration";
    private final static String successfullyAppliedState = "Successfully retrieved and applied state from %s";
    private final static String couldNotFindDescriptionField = "Could not reflect field description of this class. Was it removed?";
    private final static String wrongTypeForJGroupsChannelLookup = "Class [%s] cannot be cast to JGroupsChannelLookup!  Not using a channel lookup.";
    private final static String errorCreatingChannelFromConfigFile = "Error while trying to create a channel using config files: %s";
    private final static String errorCreatingChannelFromConfigString = "Error while trying to create a channel using config string: %s";
    private final static String failedLoadingValueFromCacheStore = "Failed loading value for key %s from cache store";
    private final static String afterCompletionFailed = "afterCompletion() failed for %s";
    private final static String unprocessedTxLogEntries = "Unprocessed Transaction Log Entries! = %d";
    private final static String unableToStopTransactionLogging = "Unable to stop transaction logging!";
    private final static String unableToInvokeGetterOnConfiguration = "Unable to invoke getter %s on Configuration.class!";
    private final static String preparedTxAlreadyExists = "There's already a prepared transaction with this xid: %s. New transaction is %s. Are there two different transactions having same Xid in the cluster?";
    private final static String errorClearinAsyncStore = "Error performing clear in async store";
    private final static String unableToApplyPrepare = "Unable to apply prepare %s";
    private final static String joinRehashCompleted = "%s completed join rehash in %s!";
    private final static String problemApplyingStateForKey = "Problem %s encountered when applying state for key %s!";
    private final static String errorEnlistingResource = "Error enlisting resource";
    private final static String joinRehashAborted = "%s aborted join rehash after %s!";
    private final static String unableToUseJGroupsPropertiesProvided = "Unable to use any JGroups configuration mechanisms provided in properties %s.  Using default JGroups configuration!";
    private final static String ignoringException = "Invocation of %s threw an exception %s. Exception is ignored.";
    private final static String namedCacheDoesNotExist = "Cache named %s does not exist on this cache manager!";
    private final static String retrievingTm = "Retrieving transaction manager %s";
    private final static String couldNotInitializeModule = "Module %s loaded, but could not be initialized";
    private final static String couldNotAcquireSharedLock = "Couldn't acquire shared lock";
    private final static String couldNotRegisterObjectName = "Could not register object with name: %s (%s)";
    private final static String remoteTxAlreadyRegistered = "A remote transaction with the given id was already registered!!!";
    private final static String beforeCompletionFailed = "beforeCompletion() failed for %s";
    private final static String fieldNotFound = "Field %s not found!!";
    private final static String errorMarshallingObject = "Exception while marshalling object";
    private final static String unbindingDummyTmFailed = "Unbinding of DummyTransactionManager failed";
    private final static String unableToLockToInvalidate = "Could not lock key %s in order to invalidate from L1 at node %s, skipping....";
    private final static String problemsUnregisteringMBeans = "Problems un-registering MBeans";
    private final static String failedToCreateInitialCtx = "Failed creating initial JNDI context";
    private final static String cacheManagerAlreadyRegistered = "There's already an cache manager instance registered under '%s' JMX domain. If you want to allow multiple instances configured with same JMX domain enable 'allowDuplicateDomains' attribute in 'globalJmxStatistics' config element";
    private final static String asyncStoreShutdownTimeoutTooHigh = "The async store shutdown timeout (%d ms) is too high compared to cache stop timeout (%d ms), so instead using %d ms for async store stop wait";
    private final static String propertyCouldNotBeReplaced = "Property %s could not be replaced as intended!";
    private final static String interruptedWaitingForCoordinator = "getCoordinator(): Interrupted while waiting for members to be set";
    private final static String exceptionPurgingDataContainer = "Caught exception purging data container!";
    private final static String receivedClusterView = "Received new cluster view: %s";
    private final static String errorTransferringState = "Error transferring state to node after rehash";
    private final static String unableToInvokeListenerMethod = "Unable to invoke method %s on Object instance %s - removing this target object from list of listeners!";
    private final static String unexpectedErrorInAsyncProcessor = "Unexpected error";
    private final static String problemsCreatingDirectory = "Problems creating the directory: %s";
    private final static String errorRequestingOrApplyingState = "Caught while requesting or applying state";
    private final static String disconnectAndCloseJGroups = "Disconnecting and closing JGroups Channel";
    private final static String problemClosingChannel = "Problem closing channel; setting it to null";
    private final static String expectedJustOneResponse = "Expected just one response; got %s";
    private final static String errorPushingTxLog = "Error pushing tx log";
    private final static String unableToConvertStringPropertyToBoolean = "Unable to convert string property [%s] to a boolean!  Using default value of %b";
    private final static String ignoringManagedAttribute = "Method name %s doesn't start with \"get\", \"set\", or \"is\" but is annotated with @ManagedAttribute: will be ignored";
    private final static String errorReadingProperties = "Unexpected error reading properties";
    private final static String problemPurgingExpired = "Problems encountered while purging expired";
    private final static String failedToUpdateAtribute = "Failed to update attribute name %s with value %s";
    private final static String errorModifyingAsyncStore = "Error while handling Modification in AsyncStore";
    private final static String errorDuringRehash = "Error during rehash";
    private final static String errorWritingValueForAttribute = "Exception while writing value for attribute %s";
    private final static String cacheCanNotHandleInvocations = "Cache named [%s] exists but isn't in a state to handle invocations.  Its state is %s.";
    private final static String lazyDeserializationDeprecated = "Lazy deserialization configuration is deprecated, please use storeAsBinary instead";
    private final static String missingListPreparedTransactions = "Missing the list of prepared transactions from node %s. Received response is %s";
    private final static String failedToCallStopAfterFailure = "Attempted to stop() from FAILED state, but caught exception; try calling destroy()";
    private final static String couldNotRollbackPrepared1PcTransaction = "Could not rollback prepared 1PC transaction. This transaction will be rolled back by the recovery process, if enabled. Transaction: %s";
    private final static String readManagedAttributeAlreadyPresent = "Not adding annotated method %s since we already have read attribute";
    private final static String unableToAcquireLockToPurgeStore = "Unable to acquire global lock to purge cache store";
    private final static String errorGeneratingState = "Caught while responding to state transfer request";
    private final static String exceptionHandlingCommand = "Caught exception when handling command %s";
    private final static String errorInstantiatingJGroupsChannelLookup = "Errors instantiating [%s]!  Not using a channel lookup.";
    private final static String cacheNotStarted = "Received a remote call but the cache is not in STARTED state - ignoring call.";
    private final static String ignoreClusterGetCall = "Quietly ignoring clustered get call %s since unable to acquire processing lock, even after %s";
    private final static String unableToProcessAsyncModifications = "Unable to process some async modifications after %d retries!";
    private final static String couldNotLoadModuleAtUrl = "Could not load module at URL %s";
    private final static String unexpectedErrorInAsyncStoreCoordinator = "Unexpected error in AsyncStoreCoordinator thread. AsyncStore is dead!";
    private final static String errorDoingRemoteCall = "Error while doing remote call";
    private final static String failedInvalidatingRemoteCache = "Failed invalidating remote cache";
    private final static String couldNotFindAttribute = "Did not find attribute %s";
    private final static String startingJGroupsChannel = "Starting JGroups Channel";
    private final static String couldNotAcquireLockForEviction = "Could not acquire lock for eviction of %s";
    private final static String remoteExecutionFailed = "Failed remote execution on node %s";
    private final static String errorProcessing1pcPrepareCommand = "Error while processing 1PC PrepareCommand";
    private final static String exceptionWhenReplaying = "Caught exception replaying %s";
    private final static String waitForCacheToStart = "Will try and wait for the cache to start";
    private final static String interruptedWaitingAsyncStorePush = "Interrupted or timeout while waiting for AsyncStore worker threads to push all state to the decorated store";
    private final static String tryingToFetchState = "Trying to fetch state from %s";
    private final static String abortingJoin = "Caught exception!  Aborting join.";
    private final static String failedReplicatingQueue = "Failed replicating %d elements in replication queue";
    private final static String unableToInvokeWebsphereStaticGetTmMethod = "Found WebSphere TransactionManager factory class [%s], but couldn't invoke its static 'getTransactionManager' method";
    private final static String couldNotInvokeSetOnAttribute = "Could not invoke set on attribute %s with value %s";
    private final static String distributionManagerNotStarted = "DistributionManager not started after waiting up to 5 minutes!  Not rehashing!";
    private final static String interruptedAcquiringLock = "Interrupted on acquireLock for %d milliseconds!";
    private final static String problemsPurgingFile = "Problems purging file %s";
    private final static String executionError = "Execution error";
    private final static String unfinishedTransactionsRemain = "Stopping but there're transactions that did not finish in time: localTransactions=%s, remoteTransactions%s";
    private final static String cannotSelectRandomMembers = "Can not select %s random members for %s";
    private final static String invalidTimeoutValue = "Invalid %s value of %s. It can not be higher than %s which is %s";
    private final static String unableToConvertStringPropertyToLong = "Unable to convert string property [%s] to a long!  Using default value of %d";
    private final static String version = "Infinispan version: %s";
    private final static String unknownResponsesFromRemoteCache = "Unknown responses from remote cache: %s";
    private final static String stoppingRpcDispatcher = "Stopping the RpcDispatcher";
    private final static String unableToRetrieveState = "Unable to retrieve state from member %s";
    private final static String fallingBackToDummyTm = "Falling back to DummyTransactionManager from Infinispan";
    private final static String completedLeaveRehash = "Completed leave rehash on node %s in %s - leavers now are %s";
    private final static String mbeansSuccessfullyRegistered = "MBeans were successfully registered to the platform mbean server.";

    public Log_$logger(final Logger log) {
        this.log = log;
    }

    @Override
    public final void couldNotResolveConfigurationSchema(final String localPath, final String schemaPath, final String schemaURL) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00139: ")+ couldNotResolveConfigurationSchema$str()), localPath, schemaPath, schemaURL);
    }

    protected String couldNotResolveConfigurationSchema$str() {
        return couldNotResolveConfigurationSchema;
    }

    @Override
    public final void problemsRemovingFile(final File f) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00059: ")+ problemsRemovingFile$str()), f);
    }

    protected String problemsRemovingFile$str() {
        return problemsRemovingFile;
    }

    @Override
    public final void unableToReadVersionId() {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00066: ")+ unableToReadVersionId$str()));
    }

    protected String unableToReadVersionId$str() {
        return unableToReadVersionId;
    }

    @Override
    public final void unableToClearAsyncStore() {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00057: ")+ unableToClearAsyncStore$str()));
    }

    protected String unableToClearAsyncStore$str() {
        return unableToClearAsyncStore;
    }

    @Override
    public final void errorSavingBucket(final Bucket b, final IOException ex) {
        log.logf(FQCN, (Logger.Level.ERROR), (ex), ((projectCode +"00063: ")+ errorSavingBucket$str()), b);
    }

    protected String errorSavingBucket$str() {
        return errorSavingBucket;
    }

    @Override
    public final void unableToConvertStringPropertyToInt(final String value, final int defaultValue) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00122: ")+ unableToConvertStringPropertyToInt$str()), value, defaultValue);
    }

    protected String unableToConvertStringPropertyToInt$str() {
        return unableToConvertStringPropertyToInt;
    }

    @Override
    public final void unableToRollbackGlobalTx(final GlobalTransaction gtx, final Throwable e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00102: ")+ unableToRollbackGlobalTx$str()), gtx);
    }

    protected String unableToRollbackGlobalTx$str() {
        return unableToRollbackGlobalTx;
    }

    @Override
    public final void participatingInRehash(final org.infinispan.remoting.transport.Address address, final List stateProviders, final List receiversOfLeaverState) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00013: ")+ participatingInRehash$str()), address, stateProviders, receiversOfLeaverState);
    }

    protected String participatingInRehash$str() {
        return participatingInRehash;
    }

    @Override
    public final void streamingStateTransferNotPresent() {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00091: ")+ streamingStateTransferNotPresent$str()));
    }

    protected String streamingStateTransferNotPresent$str() {
        return streamingStateTransferNotPresent;
    }

    @Override
    public final void unexpectedErrorFromResourceManager(final Throwable t) {
        log.logf(FQCN, (Logger.Level.ERROR), (t), ((projectCode +"00110: ")+ unexpectedErrorFromResourceManager$str()));
    }

    protected String unexpectedErrorFromResourceManager$str() {
        return unexpectedErrorFromResourceManager;
    }

    @Override
    public final void localExecutionFailed(final Throwable t) {
        log.logf(FQCN, (Logger.Level.WARN), (t), ((projectCode +"00007: ")+ localExecutionFailed$str()));
    }

    protected String localExecutionFailed$str() {
        return localExecutionFailed;
    }

    @Override
    public final void errorChangingSingletonStoreStatus(final PushStateException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00058: ")+ errorChangingSingletonStoreStatus$str()));
    }

    protected String errorChangingSingletonStoreStatus$str() {
        return errorChangingSingletonStoreStatus;
    }

    @Override
    public final void noAnnotateMethodsFoundInListener(final Class listenerClass) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00133: ")+ noAnnotateMethodsFoundInListener$str()), listenerClass);
    }

    protected String noAnnotateMethodsFoundInListener$str() {
        return noAnnotateMethodsFoundInListener;
    }

    @Override
    public final void unableToLoadFromCacheLoader(final Object key, final org.infinispan.loaders.CacheLoaderException cle) {
        log.logf(FQCN, (Logger.Level.WARN), (cle), ((projectCode +"00001: ")+ unableToLoadFromCacheLoader$str()), key);
    }

    protected String unableToLoadFromCacheLoader$str() {
        return unableToLoadFromCacheLoader;
    }

    @Override
    public final void errorCreatingChannelFromXML(final String cfg) {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00086: ")+ errorCreatingChannelFromXML$str()), cfg);
    }

    protected String errorCreatingChannelFromXML$str() {
        return errorCreatingChannelFromXML;
    }

    @Override
    public final void unexpectedErrorReplicating(final Throwable t) {
        log.logf(FQCN, (Logger.Level.ERROR), (t), ((projectCode +"00073: ")+ unexpectedErrorReplicating$str()));
    }

    protected String unexpectedErrorReplicating$str() {
        return unexpectedErrorReplicating;
    }

    @Override
    public final void unableToProcessLeaver(final Exception e) {
        log.logf(FQCN, (Logger.Level.FATAL), (e), ((projectCode +"00012: ")+ unableToProcessLeaver$str()));
    }

    protected String unableToProcessLeaver$str() {
        return unableToProcessLeaver;
    }

    @Override
    public final void channelNotSetUp() {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00092: ")+ channelNotSetUp$str()));
    }

    protected String channelNotSetUp$str() {
        return channelNotSetUp;
    }

    @Override
    public final void failedToInvalidateKeys(final Throwable t) {
        log.logf(FQCN, (Logger.Level.ERROR), (t), ((projectCode +"00147: ")+ failedToInvalidateKeys$str()));
    }

    protected String failedToInvalidateKeys$str() {
        return failedToInvalidateKeys;
    }

    @Override
    public final void recoveryIgnored() {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00115: ")+ recoveryIgnored$str()));
    }

    protected String recoveryIgnored$str() {
        return recoveryIgnored;
    }

    @Override
    public final void queriedAttributeNotFound(final String attributeName) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00042: ")+ queriedAttributeNotFound$str()), attributeName);
    }

    protected String queriedAttributeNotFound$str() {
        return queriedAttributeNotFound;
    }

    @Override
    public final void viewChangeDetected(final List oldMembers, final List newMembers) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00009: ")+ viewChangeDetected$str()), oldMembers, newMembers);
    }

    protected String viewChangeDetected$str() {
        return viewChangeDetected;
    }

    @Override
    public final void errorPurgingAsyncStore(final org.infinispan.loaders.CacheLoaderException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00050: ")+ errorPurgingAsyncStore$str()));
    }

    protected String errorPurgingAsyncStore$str() {
        return errorPurgingAsyncStore;
    }

    @Override
    public final void unableToUnregisterMBeanWithPattern(final String pattern, final MBeanRegistrationException e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00033: ")+ unableToUnregisterMBeanWithPattern$str()), pattern);
    }

    protected String unableToUnregisterMBeanWithPattern$str() {
        return unableToUnregisterMBeanWithPattern;
    }

    @Override
    public final void receivedMergedView(final org.jgroups.View newView) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00093: ")+ receivedMergedView$str()), newView);
    }

    protected String receivedMergedView$str() {
        return receivedMergedView;
    }

    @Override
    public final void writeManagedAttributeAlreadyPresent(final String methodName) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00041: ")+ writeManagedAttributeAlreadyPresent$str()), methodName);
    }

    protected String writeManagedAttributeAlreadyPresent$str() {
        return writeManagedAttributeAlreadyPresent;
    }

    @Override
    public final void unableToCopyEntryForUpdate(final Object key) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00005: ")+ unableToCopyEntryForUpdate$str()), key);
    }

    protected String unableToCopyEntryForUpdate$str() {
        return unableToCopyEntryForUpdate;
    }

    @Override
    public final void passivatedEntries(final int numEntries, final String duration) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00030: ")+ passivatedEntries$str()), numEntries, duration);
    }

    protected String passivatedEntries$str() {
        return passivatedEntries;
    }

    @Override
    public final void asyncStoreCoordinatorInterrupted(final InterruptedException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00054: ")+ asyncStoreCoordinatorInterrupted$str()));
    }

    protected String asyncStoreCoordinatorInterrupted$str() {
        return asyncStoreCoordinatorInterrupted;
    }

    @Override
    public final void localAndPhysicalAddress(final org.infinispan.remoting.transport.Address address, final List physicalAddresses) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00079: ")+ localAndPhysicalAddress$str()), address, physicalAddresses);
    }

    protected String localAndPhysicalAddress$str() {
        return localAndPhysicalAddress;
    }

    @Override
    public final void unableToSetValue(final Exception e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00121: ")+ unableToSetValue$str()));
    }

    protected String unableToSetValue$str() {
        return unableToSetValue;
    }

    @Override
    public final void leaveEvent(final org.infinispan.remoting.transport.Address leaver) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00011: ")+ leaveEvent$str()), leaver);
    }

    protected String leaveEvent$str() {
        return leaveEvent;
    }

    @Override
    public final void joinEvent(final org.infinispan.remoting.transport.Address joiner) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00010: ")+ joinEvent$str()), joiner);
    }

    protected String joinEvent$str() {
        return joinEvent;
    }

    @Override
    public final void couldNotFindPeerForState() {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00075: ")+ couldNotFindPeerForState$str()));
    }

    protected String couldNotFindPeerForState$str() {
        return couldNotFindPeerForState;
    }

    @Override
    public final void errorRollingBack(final Throwable e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00098: ")+ errorRollingBack$str()));
    }

    protected String errorRollingBack$str() {
        return errorRollingBack;
    }

    @Override
    public final void passivatingAllEntries() {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00029: ")+ passivatingAllEntries$str()));
    }

    protected String passivatingAllEntries$str() {
        return passivatingAllEntries;
    }

    @Override
    public final void stopBeforeDestroyFailed(final CacheException e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00127: ")+ stopBeforeDestroyFailed$str()));
    }

    protected String stopBeforeDestroyFailed$str() {
        return stopBeforeDestroyFailed;
    }

    @Override
    public final void distributionManagerNotJoined() {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00014: ")+ distributionManagerNotJoined$str()));
    }

    protected String distributionManagerNotJoined$str() {
        return distributionManagerNotJoined;
    }

    @Override
    public final void unableToPassivateEntry(final Object key, final Exception e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00028: ")+ unableToPassivateEntry$str()), key);
    }

    protected String unableToPassivateEntry$str() {
        return unableToPassivateEntry;
    }

    @Override
    public final void notStartingEvictionThread() {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00025: ")+ notStartingEvictionThread$str()));
    }

    protected String notStartingEvictionThread$str() {
        return notStartingEvictionThread;
    }

    @Override
    public final void keyDoesNotMapToLocalNode(final Object key, final Collection nodes) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00143: ")+ keyDoesNotMapToLocalNode$str()), key, nodes);
    }

    protected String keyDoesNotMapToLocalNode$str() {
        return keyDoesNotMapToLocalNode;
    }

    @Override
    public final void errorCommittingTx(final XAException e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00112: ")+ errorCommittingTx$str()));
    }

    protected String errorCommittingTx$str() {
        return errorCommittingTx;
    }

    @Override
    public final void msgOrMsgBufferEmpty() {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00077: ")+ msgOrMsgBufferEmpty$str()));
    }

    protected String msgOrMsgBufferEmpty$str() {
        return msgOrMsgBufferEmpty;
    }

    @Override
    public final void viewChangeInterrupted() {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00017: ")+ viewChangeInterrupted$str()));
    }

    protected String viewChangeInterrupted$str() {
        return viewChangeInterrupted;
    }

    @Override
    public final void errorReadingFromFile(final File f, final Exception e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00062: ")+ errorReadingFromFile$str()), f);
    }

    protected String errorReadingFromFile$str() {
        return errorReadingFromFile;
    }

    @Override
    public final void invalidManagedAttributeMethod(final String methodName) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00039: ")+ invalidManagedAttributeMethod$str()), methodName);
    }

    protected String invalidManagedAttributeMethod$str() {
        return invalidManagedAttributeMethod;
    }

    @Override
    public final void unsupportedTransactionConfiguration(final boolean dldEnabled, final boolean recoveryEnabled, final boolean xa) {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00114: ")+ unsupportedTransactionConfiguration$str()), dldEnabled, recoveryEnabled, xa);
    }

    protected String unsupportedTransactionConfiguration$str() {
        return unsupportedTransactionConfiguration;
    }

    @Override
    public final void failedSynchronizationRegistration(final Exception e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00101: ")+ failedSynchronizationRegistration$str()));
    }

    protected String failedSynchronizationRegistration$str() {
        return failedSynchronizationRegistration;
    }

    @Override
    public final void successfullyAppliedState(final org.infinispan.remoting.transport.Address member) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00076: ")+ successfullyAppliedState$str()), member);
    }

    protected String successfullyAppliedState$str() {
        return successfullyAppliedState;
    }

    @Override
    public final void couldNotFindDescriptionField() {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00035: ")+ couldNotFindDescriptionField$str()));
    }

    protected String couldNotFindDescriptionField$str() {
        return couldNotFindDescriptionField;
    }

    @Override
    public final void wrongTypeForJGroupsChannelLookup(final String channelLookupClassName) {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00083: ")+ wrongTypeForJGroupsChannelLookup$str()), channelLookupClassName);
    }

    protected String wrongTypeForJGroupsChannelLookup$str() {
        return wrongTypeForJGroupsChannelLookup;
    }

    @Override
    public final void errorCreatingChannelFromConfigFile(final String cfg) {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00085: ")+ errorCreatingChannelFromConfigFile$str()), cfg);
    }

    protected String errorCreatingChannelFromConfigFile$str() {
        return errorCreatingChannelFromConfigFile;
    }

    @Override
    public final void errorCreatingChannelFromConfigString(final String cfg) {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00087: ")+ errorCreatingChannelFromConfigString$str()), cfg);
    }

    protected String errorCreatingChannelFromConfigString$str() {
        return errorCreatingChannelFromConfigString;
    }

    @Override
    public final void failedLoadingValueFromCacheStore(final Object key) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00144: ")+ failedLoadingValueFromCacheStore$str()), key);
    }

    protected String failedLoadingValueFromCacheStore$str() {
        return failedLoadingValueFromCacheStore;
    }

    @Override
    public final void afterCompletionFailed(final javax.transaction.Synchronization s, final Throwable t) {
        log.logf(FQCN, (Logger.Level.ERROR), (t), ((projectCode +"00111: ")+ afterCompletionFailed$str()), s);
    }

    protected String afterCompletionFailed$str() {
        return afterCompletionFailed;
    }

    @Override
    public final void unprocessedTxLogEntries(final int size) {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00099: ")+ unprocessedTxLogEntries$str()), size);
    }

    protected String unprocessedTxLogEntries$str() {
        return unprocessedTxLogEntries;
    }

    @Override
    public final void unableToStopTransactionLogging(final IllegalMonitorStateException imse) {
        log.logf(FQCN, (Logger.Level.WARN), (imse), ((projectCode +"00024: ")+ unableToStopTransactionLogging$str()));
    }

    protected String unableToStopTransactionLogging$str() {
        return unableToStopTransactionLogging;
    }

    @Override
    public final void unableToInvokeGetterOnConfiguration(final Method getter, final Exception e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00125: ")+ unableToInvokeGetterOnConfiguration$str()), getter);
    }

    protected String unableToInvokeGetterOnConfiguration$str() {
        return unableToInvokeGetterOnConfiguration;
    }

    @Override
    public final void preparedTxAlreadyExists(final RecoveryAwareTransaction previous, final RecoveryAwareRemoteTransaction remoteTransaction) {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00117: ")+ preparedTxAlreadyExists$str()), previous, remoteTransaction);
    }

    protected String preparedTxAlreadyExists$str() {
        return preparedTxAlreadyExists;
    }

    @Override
    public final void errorClearinAsyncStore(final org.infinispan.loaders.CacheLoaderException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00049: ")+ errorClearinAsyncStore$str()));
    }

    protected String errorClearinAsyncStore$str() {
        return errorClearinAsyncStore;
    }

    @Override
    public final void unableToApplyPrepare(final PrepareCommand pc, final Throwable t) {
        log.logf(FQCN, (Logger.Level.WARN), (t), ((projectCode +"00018: ")+ unableToApplyPrepare$str()), pc);
    }

    protected String unableToApplyPrepare$str() {
        return unableToApplyPrepare;
    }

    @Override
    public final void joinRehashCompleted(final org.infinispan.remoting.transport.Address self, final String duration) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00131: ")+ joinRehashCompleted$str()), self, duration);
    }

    protected String joinRehashCompleted$str() {
        return joinRehashCompleted;
    }

    @Override
    public final void problemApplyingStateForKey(final String msg, final Object key) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00016: ")+ problemApplyingStateForKey$str()), msg, key);
    }

    protected String problemApplyingStateForKey$str() {
        return problemApplyingStateForKey;
    }

    @Override
    public final void errorEnlistingResource(final XAException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00108: ")+ errorEnlistingResource$str()));
    }

    protected String errorEnlistingResource$str() {
        return errorEnlistingResource;
    }

    @Override
    public final void joinRehashAborted(final org.infinispan.remoting.transport.Address self, final String duration) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00132: ")+ joinRehashAborted$str()), self, duration);
    }

    protected String joinRehashAborted$str() {
        return joinRehashAborted;
    }

    @Override
    public final void unableToUseJGroupsPropertiesProvided(final TypedProperties props) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00088: ")+ unableToUseJGroupsPropertiesProvided$str()), props);
    }

    protected String unableToUseJGroupsPropertiesProvided$str() {
        return unableToUseJGroupsPropertiesProvided;
    }

    @Override
    public final void ignoringException(final String name, final Throwable t) {
        log.logf(FQCN, (Logger.Level.WARN), (t), ((projectCode +"00120: ")+ ignoringException$str()), name);
    }

    protected String ignoringException$str() {
        return ignoringException;
    }

    @Override
    public final void namedCacheDoesNotExist(final String cacheName) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00068: ")+ namedCacheDoesNotExist$str()), cacheName);
    }

    protected String namedCacheDoesNotExist$str() {
        return namedCacheDoesNotExist;
    }

    @Override
    public final void retrievingTm(final TransactionManager tm) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00107: ")+ retrievingTm$str()), tm);
    }

    protected String retrievingTm$str() {
        return retrievingTm;
    }

    @Override
    public final void couldNotInitializeModule(final Object key, final Exception ex) {
        log.logf(FQCN, (Logger.Level.WARN), (ex), ((projectCode +"00119: ")+ couldNotInitializeModule$str()), key);
    }

    protected String couldNotInitializeModule$str() {
        return couldNotInitializeModule;
    }

    @Override
    public final void couldNotAcquireSharedLock() {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00019: ")+ couldNotAcquireSharedLock$str()));
    }

    protected String couldNotAcquireSharedLock$str() {
        return couldNotAcquireSharedLock;
    }

    @Override
    public final void couldNotRegisterObjectName(final ObjectName objectName, final InstanceAlreadyExistsException e) {
        log.logf(FQCN, (Logger.Level.INFO), (e), ((projectCode +"00138: ")+ couldNotRegisterObjectName$str()), objectName);
    }

    protected String couldNotRegisterObjectName$str() {
        return couldNotRegisterObjectName;
    }

    @Override
    public final void remoteTxAlreadyRegistered() {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00103: ")+ remoteTxAlreadyRegistered$str()));
    }

    protected String remoteTxAlreadyRegistered$str() {
        return remoteTxAlreadyRegistered;
    }

    @Override
    public final void beforeCompletionFailed(final javax.transaction.Synchronization s, final Throwable t) {
        log.logf(FQCN, (Logger.Level.ERROR), (t), ((projectCode +"00109: ")+ beforeCompletionFailed$str()), s);
    }

    protected String beforeCompletionFailed$str() {
        return beforeCompletionFailed;
    }

    @Override
    public final void fieldNotFound(final String fieldName) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00002: ")+ fieldNotFound$str()), fieldName);
    }

    protected String fieldNotFound$str() {
        return fieldNotFound;
    }

    @Override
    public final void errorMarshallingObject(final IOException ioe) {
        log.logf(FQCN, (Logger.Level.ERROR), (ioe), ((projectCode +"00065: ")+ errorMarshallingObject$str()));
    }

    protected String errorMarshallingObject$str() {
        return errorMarshallingObject;
    }

    @Override
    public final void unbindingDummyTmFailed(final NamingException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00113: ")+ unbindingDummyTmFailed$str()));
    }

    protected String unbindingDummyTmFailed$str() {
        return unbindingDummyTmFailed;
    }

    @Override
    public final void unableToLockToInvalidate(final Object key, final org.infinispan.remoting.transport.Address address) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00135: ")+ unableToLockToInvalidate$str()), key, address);
    }

    protected String unableToLockToInvalidate$str() {
        return unableToLockToInvalidate;
    }

    @Override
    public final void problemsUnregisteringMBeans(final Exception e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00032: ")+ problemsUnregisteringMBeans$str()));
    }

    protected String problemsUnregisteringMBeans$str() {
        return problemsUnregisteringMBeans;
    }

    @Override
    public final void failedToCreateInitialCtx(final NamingException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00105: ")+ failedToCreateInitialCtx$str()));
    }

    protected String failedToCreateInitialCtx$str() {
        return failedToCreateInitialCtx;
    }

    @Override
    public final void cacheManagerAlreadyRegistered(final String jmxDomain) {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00034: ")+ cacheManagerAlreadyRegistered$str()), jmxDomain);
    }

    protected String cacheManagerAlreadyRegistered$str() {
        return cacheManagerAlreadyRegistered;
    }

    @Override
    public final void asyncStoreShutdownTimeoutTooHigh(final long configuredAsyncStopTimeout, final long cacheStopTimeout, final long asyncStopTimeout) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00142: ")+ asyncStoreShutdownTimeoutTooHigh$str()), configuredAsyncStopTimeout, cacheStopTimeout, asyncStopTimeout);
    }

    protected String asyncStoreShutdownTimeoutTooHigh$str() {
        return asyncStoreShutdownTimeoutTooHigh;
    }

    @Override
    public final void propertyCouldNotBeReplaced(final String line) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00003: ")+ propertyCouldNotBeReplaced$str()), line);
    }

    protected String propertyCouldNotBeReplaced$str() {
        return propertyCouldNotBeReplaced;
    }

    @Override
    public final void interruptedWaitingForCoordinator(final InterruptedException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00089: ")+ interruptedWaitingForCoordinator$str()));
    }

    protected String interruptedWaitingForCoordinator$str() {
        return interruptedWaitingForCoordinator;
    }

    @Override
    public final void exceptionPurgingDataContainer(final Exception e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00026: ")+ exceptionPurgingDataContainer$str()));
    }

    protected String exceptionPurgingDataContainer$str() {
        return exceptionPurgingDataContainer;
    }

    @Override
    public final void receivedClusterView(final org.jgroups.View newView) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00094: ")+ receivedClusterView$str()), newView);
    }

    protected String receivedClusterView$str() {
        return receivedClusterView;
    }

    @Override
    public final void errorTransferringState(final Exception e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00146: ")+ errorTransferringState$str()));
    }

    protected String errorTransferringState$str() {
        return errorTransferringState;
    }

    @Override
    public final void unableToInvokeListenerMethod(final Method m, final Object target, final IllegalAccessException e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00134: ")+ unableToInvokeListenerMethod$str()), m, target);
    }

    protected String unableToInvokeListenerMethod$str() {
        return unableToInvokeListenerMethod;
    }

    @Override
    public final void unexpectedErrorInAsyncProcessor(final Throwable t) {
        log.logf(FQCN, (Logger.Level.ERROR), (t), ((projectCode +"00051: ")+ unexpectedErrorInAsyncProcessor$str()));
    }

    protected String unexpectedErrorInAsyncProcessor$str() {
        return unexpectedErrorInAsyncProcessor;
    }

    @Override
    public final void problemsCreatingDirectory(final File dir) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00064: ")+ problemsCreatingDirectory$str()), dir);
    }

    protected String problemsCreatingDirectory$str() {
        return problemsCreatingDirectory;
    }

    @Override
    public final void errorRequestingOrApplyingState(final Exception e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00096: ")+ errorRequestingOrApplyingState$str()));
    }

    protected String errorRequestingOrApplyingState$str() {
        return errorRequestingOrApplyingState;
    }

    @Override
    public final void disconnectAndCloseJGroups() {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00080: ")+ disconnectAndCloseJGroups$str()));
    }

    protected String disconnectAndCloseJGroups$str() {
        return disconnectAndCloseJGroups;
    }

    @Override
    public final void problemClosingChannel(final Exception e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00081: ")+ problemClosingChannel$str()));
    }

    protected String problemClosingChannel$str() {
        return problemClosingChannel;
    }

    @Override
    public final void expectedJustOneResponse(final Map lr) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00021: ")+ expectedJustOneResponse$str()), lr);
    }

    protected String expectedJustOneResponse$str() {
        return expectedJustOneResponse;
    }

    @Override
    public final void errorPushingTxLog(final ExecutionException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00023: ")+ errorPushingTxLog$str()));
    }

    protected String errorPushingTxLog$str() {
        return errorPushingTxLog;
    }

    @Override
    public final void unableToConvertStringPropertyToBoolean(final String value, final boolean defaultValue) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00124: ")+ unableToConvertStringPropertyToBoolean$str()), value, defaultValue);
    }

    protected String unableToConvertStringPropertyToBoolean$str() {
        return unableToConvertStringPropertyToBoolean;
    }

    @Override
    public final void ignoringManagedAttribute(final String methodName) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00038: ")+ ignoringManagedAttribute$str()), methodName);
    }

    protected String ignoringManagedAttribute$str() {
        return ignoringManagedAttribute;
    }

    @Override
    public final void errorReadingProperties(final IOException e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00004: ")+ errorReadingProperties$str()));
    }

    protected String errorReadingProperties$str() {
        return errorReadingProperties;
    }

    @Override
    public final void problemPurgingExpired(final Exception e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00045: ")+ problemPurgingExpired$str()));
    }

    protected String problemPurgingExpired$str() {
        return problemPurgingExpired;
    }

    @Override
    public final void failedToUpdateAtribute(final String name, final Object value) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00037: ")+ failedToUpdateAtribute$str()), name, value);
    }

    protected String failedToUpdateAtribute$str() {
        return failedToUpdateAtribute;
    }

    @Override
    public final void errorModifyingAsyncStore(final Exception e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00056: ")+ errorModifyingAsyncStore$str()));
    }

    protected String errorModifyingAsyncStore$str() {
        return errorModifyingAsyncStore;
    }

    @Override
    public final void errorDuringRehash(final Throwable th) {
        log.logf(FQCN, (Logger.Level.ERROR), (th), ((projectCode +"00145: ")+ errorDuringRehash$str()));
    }

    protected String errorDuringRehash$str() {
        return errorDuringRehash;
    }

    @Override
    public final void errorWritingValueForAttribute(final String attributeName, final Exception e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00043: ")+ errorWritingValueForAttribute$str()), attributeName);
    }

    protected String errorWritingValueForAttribute$str() {
        return errorWritingValueForAttribute;
    }

    @Override
    public final void cacheCanNotHandleInvocations(final String cacheName, final ComponentStatus status) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00069: ")+ cacheCanNotHandleInvocations$str()), cacheName, status);
    }

    protected String cacheCanNotHandleInvocations$str() {
        return cacheCanNotHandleInvocations;
    }

    @Override
    public final void lazyDeserializationDeprecated() {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00140: ")+ lazyDeserializationDeprecated$str()));
    }

    protected String lazyDeserializationDeprecated$str() {
        return lazyDeserializationDeprecated;
    }

    @Override
    public final void missingListPreparedTransactions(final Object key, final Object value) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00116: ")+ missingListPreparedTransactions$str()), key, value);
    }

    protected String missingListPreparedTransactions$str() {
        return missingListPreparedTransactions;
    }

    @Override
    public final void failedToCallStopAfterFailure(final Throwable t) {
        log.logf(FQCN, (Logger.Level.WARN), (t), ((projectCode +"00126: ")+ failedToCallStopAfterFailure$str()));
    }

    protected String failedToCallStopAfterFailure$str() {
        return failedToCallStopAfterFailure;
    }

    @Override
    public final void couldNotRollbackPrepared1PcTransaction(final LocalXaTransaction localTransaction, final XAException e1) {
        log.logf(FQCN, (Logger.Level.WARN), (e1), ((projectCode +"00141: ")+ couldNotRollbackPrepared1PcTransaction$str()), localTransaction);
    }

    protected String couldNotRollbackPrepared1PcTransaction$str() {
        return couldNotRollbackPrepared1PcTransaction;
    }

    @Override
    public final void readManagedAttributeAlreadyPresent(final Method m) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00040: ")+ readManagedAttributeAlreadyPresent$str()), m);
    }

    protected String readManagedAttributeAlreadyPresent$str() {
        return readManagedAttributeAlreadyPresent;
    }

    @Override
    public final void unableToAcquireLockToPurgeStore() {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00061: ")+ unableToAcquireLockToPurgeStore$str()));
    }

    protected String unableToAcquireLockToPurgeStore$str() {
        return unableToAcquireLockToPurgeStore;
    }

    @Override
    public final void errorGeneratingState(final StateTransferException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00095: ")+ errorGeneratingState$str()));
    }

    protected String errorGeneratingState$str() {
        return errorGeneratingState;
    }

    @Override
    public final void exceptionHandlingCommand(final org.infinispan.commands.remote.CacheRpcCommand cmd, final Throwable t) {
        log.logf(FQCN, (Logger.Level.WARN), (t), ((projectCode +"00071: ")+ exceptionHandlingCommand$str()), cmd);
    }

    protected String exceptionHandlingCommand$str() {
        return exceptionHandlingCommand;
    }

    @Override
    public final void errorInstantiatingJGroupsChannelLookup(final String channelLookupClassName) {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00084: ")+ errorInstantiatingJGroupsChannelLookup$str()), channelLookupClassName);
    }

    protected String errorInstantiatingJGroupsChannelLookup$str() {
        return errorInstantiatingJGroupsChannelLookup;
    }

    @Override
    public final void cacheNotStarted() {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00129: ")+ cacheNotStarted$str()));
    }

    protected String cacheNotStarted$str() {
        return cacheNotStarted;
    }

    @Override
    public final void ignoreClusterGetCall(final org.infinispan.commands.remote.CacheRpcCommand cmd, final String time) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00070: ")+ ignoreClusterGetCall$str()), cmd, time);
    }

    protected String ignoreClusterGetCall$str() {
        return ignoreClusterGetCall;
    }

    @Override
    public final void unableToProcessAsyncModifications(final int retries) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00053: ")+ unableToProcessAsyncModifications$str()), retries);
    }

    protected String unableToProcessAsyncModifications$str() {
        return unableToProcessAsyncModifications;
    }

    @Override
    public final void couldNotLoadModuleAtUrl(final URL url, final Exception ex) {
        log.logf(FQCN, (Logger.Level.WARN), (ex), ((projectCode +"00118: ")+ couldNotLoadModuleAtUrl$str()), url);
    }

    protected String couldNotLoadModuleAtUrl$str() {
        return couldNotLoadModuleAtUrl;
    }

    @Override
    public final void unexpectedErrorInAsyncStoreCoordinator(final Throwable t) {
        log.logf(FQCN, (Logger.Level.ERROR), (t), ((projectCode +"00055: ")+ unexpectedErrorInAsyncStoreCoordinator$str()));
    }

    protected String unexpectedErrorInAsyncStoreCoordinator$str() {
        return unexpectedErrorInAsyncStoreCoordinator;
    }

    @Override
    public final void errorDoingRemoteCall(final Exception e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00047: ")+ errorDoingRemoteCall$str()));
    }

    protected String errorDoingRemoteCall$str() {
        return errorDoingRemoteCall;
    }

    @Override
    public final void failedInvalidatingRemoteCache(final Exception e) {
        log.logf(FQCN, (Logger.Level.INFO), (e), ((projectCode +"00137: ")+ failedInvalidatingRemoteCache$str()));
    }

    protected String failedInvalidatingRemoteCache$str() {
        return failedInvalidatingRemoteCache;
    }

    @Override
    public final void couldNotFindAttribute(final String name) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00036: ")+ couldNotFindAttribute$str()), name);
    }

    protected String couldNotFindAttribute$str() {
        return couldNotFindAttribute;
    }

    @Override
    public final void startingJGroupsChannel() {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00078: ")+ startingJGroupsChannel$str()));
    }

    protected String startingJGroupsChannel$str() {
        return startingJGroupsChannel;
    }

    @Override
    public final void couldNotAcquireLockForEviction(final Object key, final Exception e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00027: ")+ couldNotAcquireLockForEviction$str()), key);
    }

    protected String couldNotAcquireLockForEviction$str() {
        return couldNotAcquireLockForEviction;
    }

    @Override
    public final void remoteExecutionFailed(final org.infinispan.remoting.transport.Address address, final Throwable t) {
        log.logf(FQCN, (Logger.Level.WARN), (t), ((projectCode +"00006: ")+ remoteExecutionFailed$str()), address);
    }

    protected String remoteExecutionFailed$str() {
        return remoteExecutionFailed;
    }

    @Override
    public final void errorProcessing1pcPrepareCommand(final Throwable e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00097: ")+ errorProcessing1pcPrepareCommand$str()));
    }

    protected String errorProcessing1pcPrepareCommand$str() {
        return errorProcessing1pcPrepareCommand;
    }

    @Override
    public final void exceptionWhenReplaying(final WriteCommand cmd, final Exception e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00020: ")+ exceptionWhenReplaying$str()), cmd);
    }

    protected String exceptionWhenReplaying$str() {
        return exceptionWhenReplaying;
    }

    @Override
    public final void waitForCacheToStart() {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00067: ")+ waitForCacheToStart$str()));
    }

    protected String waitForCacheToStart$str() {
        return waitForCacheToStart;
    }

    @Override
    public final void interruptedWaitingAsyncStorePush(final InterruptedException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00048: ")+ interruptedWaitingAsyncStorePush$str()));
    }

    protected String interruptedWaitingAsyncStorePush$str() {
        return interruptedWaitingAsyncStorePush;
    }

    @Override
    public final void tryingToFetchState(final org.infinispan.remoting.transport.Address member) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00074: ")+ tryingToFetchState$str()), member);
    }

    protected String tryingToFetchState$str() {
        return tryingToFetchState;
    }

    @Override
    public final void abortingJoin(final Exception e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00130: ")+ abortingJoin$str()));
    }

    protected String abortingJoin$str() {
        return abortingJoin;
    }

    @Override
    public final void failedReplicatingQueue(final int size, final Throwable t) {
        log.logf(FQCN, (Logger.Level.ERROR), (t), ((projectCode +"00072: ")+ failedReplicatingQueue$str()), size);
    }

    protected String failedReplicatingQueue$str() {
        return failedReplicatingQueue;
    }

    @Override
    public final void unableToInvokeWebsphereStaticGetTmMethod(final Exception ex) {
        log.logf(FQCN, (Logger.Level.ERROR), (ex), ((projectCode +"00106: ")+ unableToInvokeWebsphereStaticGetTmMethod$str()));
    }

    protected String unableToInvokeWebsphereStaticGetTmMethod$str() {
        return unableToInvokeWebsphereStaticGetTmMethod;
    }

    @Override
    public final void couldNotInvokeSetOnAttribute(final String attributeName, final Object value) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00044: ")+ couldNotInvokeSetOnAttribute$str()), attributeName, value);
    }

    protected String couldNotInvokeSetOnAttribute$str() {
        return couldNotInvokeSetOnAttribute;
    }

    @Override
    public final void distributionManagerNotStarted() {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00015: ")+ distributionManagerNotStarted$str()));
    }

    protected String distributionManagerNotStarted$str() {
        return distributionManagerNotStarted;
    }

    @Override
    public final void interruptedAcquiringLock(final long ms, final InterruptedException e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00052: ")+ interruptedAcquiringLock$str()), ms);
    }

    protected String interruptedAcquiringLock$str() {
        return interruptedAcquiringLock;
    }

    @Override
    public final void problemsPurgingFile(final File buckedFile, final org.infinispan.loaders.CacheLoaderException e) {
        log.logf(FQCN, (Logger.Level.WARN), (e), ((projectCode +"00060: ")+ problemsPurgingFile$str()), buckedFile);
    }

    protected String problemsPurgingFile$str() {
        return problemsPurgingFile;
    }

    @Override
    public final void executionError(final Throwable t) {
        log.logf(FQCN, (Logger.Level.ERROR), (t), ((projectCode +"00136: ")+ executionError$str()));
    }

    protected String executionError$str() {
        return executionError;
    }

    @Override
    public final void unfinishedTransactionsRemain(final ConcurrentMap localTransactions, final ConcurrentMap remoteTransactions) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00100: ")+ unfinishedTransactionsRemain$str()), localTransactions, remoteTransactions);
    }

    protected String unfinishedTransactionsRemain$str() {
        return unfinishedTransactionsRemain;
    }

    @Override
    public final void cannotSelectRandomMembers(final int numNeeded, final List members) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00008: ")+ cannotSelectRandomMembers$str()), numNeeded, members);
    }

    protected String cannotSelectRandomMembers$str() {
        return cannotSelectRandomMembers;
    }

    @Override
    public final void invalidTimeoutValue(final Object configName1, final Object value1, final Object configName2, final Object value2) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00148: ")+ invalidTimeoutValue$str()), configName1, value1, configName2, value2);
    }

    protected String invalidTimeoutValue$str() {
        return invalidTimeoutValue;
    }

    @Override
    public final void unableToConvertStringPropertyToLong(final String value, final long defaultValue) {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00123: ")+ unableToConvertStringPropertyToLong$str()), value, defaultValue);
    }

    protected String unableToConvertStringPropertyToLong$str() {
        return unableToConvertStringPropertyToLong;
    }

    @Override
    public final void version(final String version) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00128: ")+ version$str()), version);
    }

    protected String version$str() {
        return version;
    }

    @Override
    public final void unknownResponsesFromRemoteCache(final Collection responses) {
        log.logf(FQCN, (Logger.Level.ERROR), null, ((projectCode +"00046: ")+ unknownResponsesFromRemoteCache$str()), responses);
    }

    protected String unknownResponsesFromRemoteCache$str() {
        return unknownResponsesFromRemoteCache;
    }

    @Override
    public final void stoppingRpcDispatcher() {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00082: ")+ stoppingRpcDispatcher$str()));
    }

    protected String stoppingRpcDispatcher$str() {
        return stoppingRpcDispatcher;
    }

    @Override
    public final void unableToRetrieveState(final org.infinispan.remoting.transport.Address member, final Exception e) {
        log.logf(FQCN, (Logger.Level.ERROR), (e), ((projectCode +"00090: ")+ unableToRetrieveState$str()), member);
    }

    protected String unableToRetrieveState$str() {
        return unableToRetrieveState;
    }

    @Override
    public final void fallingBackToDummyTm() {
        log.logf(FQCN, (Logger.Level.WARN), null, ((projectCode +"00104: ")+ fallingBackToDummyTm$str()));
    }

    protected String fallingBackToDummyTm$str() {
        return fallingBackToDummyTm;
    }

    @Override
    public final void completedLeaveRehash(final org.infinispan.remoting.transport.Address self, final String duration, final List leavers) {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00022: ")+ completedLeaveRehash$str()), self, duration, leavers);
    }

    protected String completedLeaveRehash$str() {
        return completedLeaveRehash;
    }

    @Override
    public final void mbeansSuccessfullyRegistered() {
        log.logf(FQCN, (Logger.Level.INFO), null, ((projectCode +"00031: ")+ mbeansSuccessfullyRegistered$str()));
    }

    protected String mbeansSuccessfullyRegistered$str() {
        return mbeansSuccessfullyRegistered;
    }

    @Override
    public final void log(final Logger.Level arg0, final String arg1, final Object arg2, final Throwable arg3) {
        this.log.log(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void log(final Logger.Level arg0, final Object arg1, final Object[] arg2) {
        this.log.log(arg0, arg1, arg2);
    }

    @Override
    public final void log(final String arg0, final Logger.Level arg1, final Object arg2, final Object[] arg3, final Throwable arg4) {
        this.log.log(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void log(final Logger.Level arg0, final Object arg1, final Object[] arg2, final Throwable arg3) {
        this.log.log(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void log(final Logger.Level arg0, final Object arg1) {
        this.log.log(arg0, arg1);
    }

    @Override
    public final void log(final Logger.Level arg0, final Object arg1, final Throwable arg2) {
        this.log.log(arg0, arg1, arg2);
    }

    @Override
    public final void debug(final Object arg0) {
        this.log.debug(arg0);
    }

    @Override
    public final void debug(final Object arg0, final Object[] arg1, final Throwable arg2) {
        this.log.debug(arg0, arg1, arg2);
    }

    @Override
    public final void debug(final Object arg0, final Object[] arg1) {
        this.log.debug(arg0, arg1);
    }

    @Override
    public final void debug(final String arg0, final Object arg1, final Throwable arg2) {
        this.log.debug(arg0, arg1, arg2);
    }

    @Override
    public final void debug(final String arg0, final Object arg1, final Object[] arg2, final Throwable arg3) {
        this.log.debug(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void debug(final Object arg0, final Throwable arg1) {
        this.log.debug(arg0, arg1);
    }

    @Override
    public final void error(final Object arg0) {
        this.log.error(arg0);
    }

    @Override
    public final void error(final Object arg0, final Object[] arg1) {
        this.log.error(arg0, arg1);
    }

    @Override
    public final void error(final Object arg0, final Object[] arg1, final Throwable arg2) {
        this.log.error(arg0, arg1, arg2);
    }

    @Override
    public final void error(final String arg0, final Object arg1, final Object[] arg2, final Throwable arg3) {
        this.log.error(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void error(final String arg0, final Object arg1, final Throwable arg2) {
        this.log.error(arg0, arg1, arg2);
    }

    @Override
    public final void error(final Object arg0, final Throwable arg1) {
        this.log.error(arg0, arg1);
    }

    @Override
    public final void info(final Object arg0, final Object[] arg1) {
        this.log.info(arg0, arg1);
    }

    @Override
    public final void info(final Object arg0) {
        this.log.info(arg0);
    }

    @Override
    public final void info(final Object arg0, final Throwable arg1) {
        this.log.info(arg0, arg1);
    }

    @Override
    public final void info(final String arg0, final Object arg1, final Throwable arg2) {
        this.log.info(arg0, arg1, arg2);
    }

    @Override
    public final void info(final Object arg0, final Object[] arg1, final Throwable arg2) {
        this.log.info(arg0, arg1, arg2);
    }

    @Override
    public final void info(final String arg0, final Object arg1, final Object[] arg2, final Throwable arg3) {
        this.log.info(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void warn(final Object arg0) {
        this.log.warn(arg0);
    }

    @Override
    public final void warn(final Object arg0, final Throwable arg1) {
        this.log.warn(arg0, arg1);
    }

    @Override
    public final void warn(final String arg0, final Object arg1, final Throwable arg2) {
        this.log.warn(arg0, arg1, arg2);
    }

    @Override
    public final void warn(final Object arg0, final Object[] arg1) {
        this.log.warn(arg0, arg1);
    }

    @Override
    public final void warn(final Object arg0, final Object[] arg1, final Throwable arg2) {
        this.log.warn(arg0, arg1, arg2);
    }

    @Override
    public final void warn(final String arg0, final Object arg1, final Object[] arg2, final Throwable arg3) {
        this.log.warn(arg0, arg1, arg2, arg3);
    }

    @Override
    public final boolean isDebugEnabled() {
        return this.log.isDebugEnabled();
    }

    @Override
    public final boolean isInfoEnabled() {
        return this.log.isInfoEnabled();
    }

    @Override
    public final boolean isEnabled(final Logger.Level arg0) {
        return this.log.isEnabled(arg0);
    }

    @Override
    public final void fatal(final Object arg0, final Object[] arg1) {
        this.log.fatal(arg0, arg1);
    }

    @Override
    public final void fatal(final String arg0, final Object arg1, final Throwable arg2) {
        this.log.fatal(arg0, arg1, arg2);
    }

    @Override
    public final void fatal(final Object arg0, final Throwable arg1) {
        this.log.fatal(arg0, arg1);
    }

    @Override
    public final void fatal(final Object arg0) {
        this.log.fatal(arg0);
    }

    @Override
    public final void fatal(final Object arg0, final Object[] arg1, final Throwable arg2) {
        this.log.fatal(arg0, arg1, arg2);
    }

    @Override
    public final void fatal(final String arg0, final Object arg1, final Object[] arg2, final Throwable arg3) {
        this.log.fatal(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void trace(final Object arg0, final Object[] arg1, final Throwable arg2) {
        this.log.trace(arg0, arg1, arg2);
    }

    @Override
    public final void trace(final Object arg0, final Throwable arg1) {
        this.log.trace(arg0, arg1);
    }

    @Override
    public final void trace(final String arg0, final Object arg1, final Object[] arg2, final Throwable arg3) {
        this.log.trace(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void trace(final Object arg0) {
        this.log.trace(arg0);
    }

    @Override
    public final void trace(final Object arg0, final Object[] arg1) {
        this.log.trace(arg0, arg1);
    }

    @Override
    public final void trace(final String arg0, final Object arg1, final Throwable arg2) {
        this.log.trace(arg0, arg1, arg2);
    }

    @Override
    public final boolean isTraceEnabled() {
        return this.log.isTraceEnabled();
    }

    @Override
    public final void tracev(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.tracev(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void tracev(final String arg0, final Object[] arg1) {
        this.log.tracev(arg0, arg1);
    }

    @Override
    public final void tracev(final String arg0, final Object arg1) {
        this.log.tracev(arg0, arg1);
    }

    @Override
    public final void tracev(final String arg0, final Object arg1, final Object arg2) {
        this.log.tracev(arg0, arg1, arg2);
    }

    @Override
    public final void tracev(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.tracev(arg0, arg1, arg2);
    }

    @Override
    public final void tracev(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.tracev(arg0, arg1, arg2);
    }

    @Override
    public final void tracev(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.tracev(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void tracev(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.tracev(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void tracef(final String arg0, final Object[] arg1) {
        this.log.tracef(arg0, arg1);
    }

    @Override
    public final void tracef(final String arg0, final Object arg1) {
        this.log.tracef(arg0, arg1);
    }

    @Override
    public final void tracef(final String arg0, final Object arg1, final Object arg2) {
        this.log.tracef(arg0, arg1, arg2);
    }

    @Override
    public final void tracef(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.tracef(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void tracef(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.tracef(arg0, arg1, arg2);
    }

    @Override
    public final void tracef(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.tracef(arg0, arg1, arg2);
    }

    @Override
    public final void tracef(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.tracef(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void tracef(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.tracef(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void debugv(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.debugv(arg0, arg1, arg2);
    }

    @Override
    public final void debugv(final String arg0, final Object[] arg1) {
        this.log.debugv(arg0, arg1);
    }

    @Override
    public final void debugv(final String arg0, final Object arg1) {
        this.log.debugv(arg0, arg1);
    }

    @Override
    public final void debugv(final String arg0, final Object arg1, final Object arg2) {
        this.log.debugv(arg0, arg1, arg2);
    }

    @Override
    public final void debugv(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.debugv(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void debugv(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.debugv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void debugv(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.debugv(arg0, arg1, arg2);
    }

    @Override
    public final void debugv(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.debugv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void debugf(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.debugf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void debugf(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.debugf(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void debugf(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.debugf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void debugf(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.debugf(arg0, arg1, arg2);
    }

    @Override
    public final void debugf(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.debugf(arg0, arg1, arg2);
    }

    @Override
    public final void debugf(final String arg0, final Object arg1, final Object arg2) {
        this.log.debugf(arg0, arg1, arg2);
    }

    @Override
    public final void debugf(final String arg0, final Object arg1) {
        this.log.debugf(arg0, arg1);
    }

    @Override
    public final void debugf(final String arg0, final Object[] arg1) {
        this.log.debugf(arg0, arg1);
    }

    @Override
    public final void infov(final String arg0, final Object[] arg1) {
        this.log.infov(arg0, arg1);
    }

    @Override
    public final void infov(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.infov(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void infov(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.infov(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void infov(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.infov(arg0, arg1, arg2);
    }

    @Override
    public final void infov(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.infov(arg0, arg1, arg2);
    }

    @Override
    public final void infov(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.infov(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void infov(final String arg0, final Object arg1, final Object arg2) {
        this.log.infov(arg0, arg1, arg2);
    }

    @Override
    public final void infov(final String arg0, final Object arg1) {
        this.log.infov(arg0, arg1);
    }

    @Override
    public final void infof(final String arg0, final Object arg1) {
        this.log.infof(arg0, arg1);
    }

    @Override
    public final void infof(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.infof(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void infof(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.infof(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void infof(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.infof(arg0, arg1, arg2);
    }

    @Override
    public final void infof(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.infof(arg0, arg1, arg2);
    }

    @Override
    public final void infof(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.infof(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void infof(final String arg0, final Object arg1, final Object arg2) {
        this.log.infof(arg0, arg1, arg2);
    }

    @Override
    public final void infof(final String arg0, final Object[] arg1) {
        this.log.infof(arg0, arg1);
    }

    @Override
    public final void warnv(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.warnv(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void warnv(final String arg0, final Object[] arg1) {
        this.log.warnv(arg0, arg1);
    }

    @Override
    public final void warnv(final String arg0, final Object arg1) {
        this.log.warnv(arg0, arg1);
    }

    @Override
    public final void warnv(final String arg0, final Object arg1, final Object arg2) {
        this.log.warnv(arg0, arg1, arg2);
    }

    @Override
    public final void warnv(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.warnv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void warnv(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.warnv(arg0, arg1, arg2);
    }

    @Override
    public final void warnv(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.warnv(arg0, arg1, arg2);
    }

    @Override
    public final void warnv(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.warnv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void warnf(final String arg0, final Object[] arg1) {
        this.log.warnf(arg0, arg1);
    }

    @Override
    public final void warnf(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.warnf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void warnf(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.warnf(arg0, arg1, arg2);
    }

    @Override
    public final void warnf(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.warnf(arg0, arg1, arg2);
    }

    @Override
    public final void warnf(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.warnf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void warnf(final String arg0, final Object arg1, final Object arg2) {
        this.log.warnf(arg0, arg1, arg2);
    }

    @Override
    public final void warnf(final String arg0, final Object arg1) {
        this.log.warnf(arg0, arg1);
    }

    @Override
    public final void warnf(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.warnf(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void errorv(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.errorv(arg0, arg1, arg2);
    }

    @Override
    public final void errorv(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.errorv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void errorv(final String arg0, final Object arg1, final Object arg2) {
        this.log.errorv(arg0, arg1, arg2);
    }

    @Override
    public final void errorv(final String arg0, final Object arg1) {
        this.log.errorv(arg0, arg1);
    }

    @Override
    public final void errorv(final String arg0, final Object[] arg1) {
        this.log.errorv(arg0, arg1);
    }

    @Override
    public final void errorv(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.errorv(arg0, arg1, arg2);
    }

    @Override
    public final void errorv(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.errorv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void errorv(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.errorv(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void errorf(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.errorf(arg0, arg1, arg2);
    }

    @Override
    public final void errorf(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.errorf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void errorf(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.errorf(arg0, arg1, arg2);
    }

    @Override
    public final void errorf(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.errorf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void errorf(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.errorf(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void errorf(final String arg0, final Object[] arg1) {
        this.log.errorf(arg0, arg1);
    }

    @Override
    public final void errorf(final String arg0, final Object arg1) {
        this.log.errorf(arg0, arg1);
    }

    @Override
    public final void errorf(final String arg0, final Object arg1, final Object arg2) {
        this.log.errorf(arg0, arg1, arg2);
    }

    @Override
    public final void fatalv(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.fatalv(arg0, arg1, arg2);
    }

    @Override
    public final void fatalv(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.fatalv(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void fatalv(final String arg0, final Object[] arg1) {
        this.log.fatalv(arg0, arg1);
    }

    @Override
    public final void fatalv(final String arg0, final Object arg1) {
        this.log.fatalv(arg0, arg1);
    }

    @Override
    public final void fatalv(final String arg0, final Object arg1, final Object arg2) {
        this.log.fatalv(arg0, arg1, arg2);
    }

    @Override
    public final void fatalv(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.fatalv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void fatalv(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.fatalv(arg0, arg1, arg2);
    }

    @Override
    public final void fatalv(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.fatalv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void fatalf(final Throwable arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.fatalf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void fatalf(final String arg0, final Object[] arg1) {
        this.log.fatalf(arg0, arg1);
    }

    @Override
    public final void fatalf(final String arg0, final Object arg1) {
        this.log.fatalf(arg0, arg1);
    }

    @Override
    public final void fatalf(final String arg0, final Object arg1, final Object arg2) {
        this.log.fatalf(arg0, arg1, arg2);
    }

    @Override
    public final void fatalf(final String arg0, final Object arg1, final Object arg2, final Object arg3) {
        this.log.fatalf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void fatalf(final Throwable arg0, final String arg1, final Object[] arg2) {
        this.log.fatalf(arg0, arg1, arg2);
    }

    @Override
    public final void fatalf(final Throwable arg0, final String arg1, final Object arg2) {
        this.log.fatalf(arg0, arg1, arg2);
    }

    @Override
    public final void fatalf(final Throwable arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.fatalf(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void logv(final Logger.Level arg0, final String arg1, final Object[] arg2) {
        this.log.logv(arg0, arg1, arg2);
    }

    @Override
    public final void logv(final Logger.Level arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.logv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void logv(final Logger.Level arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.logv(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void logv(final Logger.Level arg0, final Throwable arg1, final String arg2, final Object[] arg3) {
        this.log.logv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void logv(final Logger.Level arg0, final Throwable arg1, final String arg2, final Object arg3) {
        this.log.logv(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void logv(final Logger.Level arg0, final Throwable arg1, final String arg2, final Object arg3, final Object arg4) {
        this.log.logv(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void logv(final Logger.Level arg0, final Throwable arg1, final String arg2, final Object arg3, final Object arg4, final Object arg5) {
        this.log.logv(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public final void logv(final String arg0, final Logger.Level arg1, final Throwable arg2, final String arg3, final Object[] arg4) {
        this.log.logv(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void logv(final String arg0, final Logger.Level arg1, final Throwable arg2, final String arg3, final Object arg4) {
        this.log.logv(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void logv(final String arg0, final Logger.Level arg1, final Throwable arg2, final String arg3, final Object arg4, final Object arg5) {
        this.log.logv(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public final void logv(final String arg0, final Logger.Level arg1, final Throwable arg2, final String arg3, final Object arg4, final Object arg5, final Object arg6) {
        this.log.logv(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public final void logv(final Logger.Level arg0, final String arg1, final Object arg2) {
        this.log.logv(arg0, arg1, arg2);
    }

    @Override
    public final void logf(final String arg0, final Logger.Level arg1, final Throwable arg2, final String arg3, final Object[] arg4) {
        this.log.logf(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void logf(final Logger.Level arg0, final String arg1, final Object[] arg2) {
        this.log.logf(arg0, arg1, arg2);
    }

    @Override
    public final void logf(final Logger.Level arg0, final String arg1, final Object arg2) {
        this.log.logf(arg0, arg1, arg2);
    }

    @Override
    public final void logf(final Logger.Level arg0, final String arg1, final Object arg2, final Object arg3) {
        this.log.logf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void logf(final Logger.Level arg0, final String arg1, final Object arg2, final Object arg3, final Object arg4) {
        this.log.logf(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void logf(final Logger.Level arg0, final Throwable arg1, final String arg2, final Object[] arg3) {
        this.log.logf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void logf(final Logger.Level arg0, final Throwable arg1, final String arg2, final Object arg3) {
        this.log.logf(arg0, arg1, arg2, arg3);
    }

    @Override
    public final void logf(final Logger.Level arg0, final Throwable arg1, final String arg2, final Object arg3, final Object arg4) {
        this.log.logf(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void logf(final Logger.Level arg0, final Throwable arg1, final String arg2, final Object arg3, final Object arg4, final Object arg5) {
        this.log.logf(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public final void logf(final String arg0, final Logger.Level arg1, final Throwable arg2, final String arg3, final Object arg4) {
        this.log.logf(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public final void logf(final String arg0, final Logger.Level arg1, final Throwable arg2, final String arg3, final Object arg4, final Object arg5) {
        this.log.logf(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public final void logf(final String arg0, final Logger.Level arg1, final Throwable arg2, final String arg3, final Object arg4, final Object arg5, final Object arg6) {
        this.log.logf(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

}
