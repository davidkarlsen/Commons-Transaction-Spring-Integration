package com.davidkarlsen.commonstransaction.spring;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.util.Assert;

/**
 * A {@link FileResourceManager} which is transaction aware, thus transaction handling can be handled outside of the
 * manager. Suitable when transaction demarcation are declared with AOP.
 * 
 * @author <a href="mailto:david@davidkarlsen.com">David J. M Karlsen</a>
 */
public class TransactionAwareFileResourceManager
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private FileResourceManager fileResourceManager;

    public void setFileResourceManager( FileResourceManager fileResourceManager )
    {
        this.fileResourceManager = fileResourceManager;
    }

    public void createResource( String resourceId )
        throws ResourceManagerException
    {
        createResource( resourceId, false );
    }

    public void createResource( String resourceId, boolean overwrite )
        throws NoTransactionException, ResourceManagerException
    {
        fileResourceManager.createResource( getCurrentNativeTransaction(), resourceId, overwrite );
    }

    public InputStream readResource( String resourceId )
        throws ResourceManagerException
    {
        try
        {
            Object tx = getCurrentNativeTransaction();
            return fileResourceManager.readResource( tx, resourceId );
        }
        catch ( NoTransactionException e )
        {
            logger.warn( "Reading outside of active transaction" );
            return fileResourceManager.readResource( resourceId );
        }
    }

    public OutputStream writeResource( String resourceId )
        throws NoTransactionException, ResourceManagerException
    {
        return writeResource( resourceId, false );
    }

    public OutputStream writeResource( String resourceId, boolean overwrite )
        throws NoTransactionException, ResourceManagerException
    {
        return fileResourceManager.writeResource( getCurrentNativeTransaction(), resourceId, overwrite );
    }

    public void deleteResource( String resourceId )
        throws NoTransactionException, ResourceManagerException
    {
        fileResourceManager.deleteResource( getCurrentNativeTransaction(), resourceId );
    }

    /**
     * Finds the current native transaction.
     * 
     * @return An object representing the commons transaction transaction
     * @throws NoTransactionException in case of no current transaction
     */
    private Object getCurrentNativeTransaction()
        throws NoTransactionException
    {
        TransactionStatus transactionStatus = TransactionAspectSupport.currentTransactionStatus();
        Assert.isInstanceOf( DefaultTransactionStatus.class, transactionStatus );
        Object nativeTransaction = ( (DefaultTransactionStatus) transactionStatus ).getTransaction();

        return nativeTransaction;
    }
}
