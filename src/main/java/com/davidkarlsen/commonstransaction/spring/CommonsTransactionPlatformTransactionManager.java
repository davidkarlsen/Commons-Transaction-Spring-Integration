package com.davidkarlsen.commonstransaction.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.apache.commons.transaction.file.ResourceManagerSystemException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.util.Assert;

/**
 * A {@linkplain PlatformTransactionManager} implementation for commons transaction.
 * 
 *  
 * @see PlatformTransactionManager
 * @see ResourceTransactionManager
 * @see http://commons.apache.org/transaction/
 * @see http://myjavatricks.com/jtfs.aspx
 * 
 * @author <a href="mailto:david@davidkarlsen.com">David J. M Karlsen</a>
 *
 */
public class CommonsTransactionPlatformTransactionManager
    extends AbstractPlatformTransactionManager
    implements InitializingBean, DisposableBean, ResourceTransactionManager
{
    private static final long serialVersionUID = 8316646351941218318L;

    private final Log log = LogFactory.getLog( getClass() );

    private transient FileResourceManager fileResourceManager;


    /**
     * Required.
     * The underlying {@linkplain FileResourceManager} to operate on.
     * @param fileResourceManager
     */
    public void setFileResourceManager( FileResourceManager fileResourceManager )
    {
        this.fileResourceManager = fileResourceManager;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isExistingTransaction( Object transaction )
        throws TransactionException
    {
        try
        {
            boolean noTransaction = fileResourceManager.getTransactionState( transaction ) == FileResourceManager.STATUS_NO_TRANSACTION;
            return !noTransaction;
        }
        catch ( ResourceManagerException e )
        {
            log.error( e );
            throw new TransactionSystemException( e.getMessage(), e );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCommit( DefaultTransactionStatus status )
        throws TransactionException
    {
        try
        {
            log.debug( "Commiting transaction: " + status );
            fileResourceManager.commitTransaction( status.getTransaction() );
        }
        catch ( ResourceManagerException e )
        {
            throw new TransactionSystemException( e.getMessage(), e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetTransaction()
        throws TransactionException
    {
        try
        {
            String txId;
            synchronized ( fileResourceManager )
            {
                txId = fileResourceManager.generatedUniqueTxId();
            }
            log.debug( "Created txId: " + txId );

            return txId;
        }
        catch ( ResourceManagerSystemException e )
        {
            log.error( e );
            throw new CannotCreateTransactionException( e.getMessage(), e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doBegin( Object transaction, TransactionDefinition definition )
        throws TransactionException
    {
        try
        {
            log.debug( "Starting transaction: " + transaction );
            fileResourceManager.startTransaction( transaction );
            
            switch ( definition.getIsolationLevel() ) {
                case TransactionDefinition.ISOLATION_READ_COMMITTED:
                    fileResourceManager.setIsolationLevel( transaction, FileResourceManager.ISOLATION_LEVEL_READ_COMMITTED);
                    break;
                case TransactionDefinition.ISOLATION_READ_UNCOMMITTED:
                    fileResourceManager.setIsolationLevel( transaction, FileResourceManager.ISOLATION_LEVEL_READ_UNCOMMITTED);
                    break;
                case TransactionDefinition.ISOLATION_REPEATABLE_READ:
                    fileResourceManager.setIsolationLevel( transaction, FileResourceManager.ISOLATION_LEVEL_REPEATABLE_READ);
                    break;
                case TransactionDefinition.ISOLATION_SERIALIZABLE:
                    fileResourceManager.setIsolationLevel( transaction, FileResourceManager.ISOLATION_LEVEL_SERIALIZABLE );
                    break;
                case TransactionDefinition.ISOLATION_DEFAULT:
                default:
            }
        }
        catch ( ResourceManagerException e )
        {
            log.error( e );
            throw new TransactionSystemException( e.getMessage(), e );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareForCommit( DefaultTransactionStatus status )
    {
        try
        {
            log.debug( "Prepare commit: " + status );
            fileResourceManager.prepareTransaction( status.getTransaction() );
        }
        catch ( ResourceManagerException e )
        {
            log.error( e );
            throw new RuntimeException( e );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetRollbackOnly( DefaultTransactionStatus status )
        throws TransactionException
    {
        try
        {
            log.debug( "Marking transaction for rollback: " + status );
            fileResourceManager.markTransactionForRollback( status.getTransaction() );
        }
        catch ( ResourceManagerException e )
        {
            log.error( e );
            throw new TransactionSystemException( e.getMessage(), e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRollback( DefaultTransactionStatus status )
        throws TransactionException
    {
        try
        {
            log.debug( "Rolling back transactionStatus: " + status );
            fileResourceManager.rollbackTransaction( status.getTransaction() );
        }
        catch ( ResourceManagerException e )
        {
            log.error( e );
            throw new TransactionSystemException( e.getMessage(), e );
        }
    }

    /**
     * Initialises a {@linkplain FileResourceManager} and starts it.
     */
    public void afterPropertiesSet()
        throws Exception
    {
        Assert.notNull( fileResourceManager, "fileResourceManager must be set" );
        fileResourceManager.setDefaultTransactionTimeout( getDefaultTimeout() );

        fileResourceManager.start();
    }

    /**
     * Stops the {@linkplain FileResourceManager}.
     */
    public void destroy()
        throws Exception
    {
        log.info( "Shutting down transaction manager" );
        fileResourceManager.stop( FileResourceManager.SHUTDOWN_MODE_NORMAL );
    }

    /**
     * {@inheritDoc}
     * Co-variant version of {@linkplain ResourceTransactionManager#getResourceFactory()}
     */
    public FileResourceManager getResourceFactory()
    {
        return this.fileResourceManager;
    }

}
