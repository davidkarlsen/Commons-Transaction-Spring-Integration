package com.davidkarlsen.commonstransaction.spring;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.NoOpTransactionIdToPathMapper;
import org.apache.commons.transaction.file.ResourceIdToPathMapper;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.apache.commons.transaction.file.TransactionIdToPathMapper;
import org.apache.commons.transaction.file.URLEncodeIdMapper;
import org.apache.commons.transaction.util.CommonsLoggingLogger;
import org.apache.commons.transaction.util.LoggerFacade;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;

/**
 * Creates {@link FileResourceManager} instances.
 * 
 * @author <a href="mailto:david@davidkarlsen.com">David J. M Karlsen</a>
 */
public class FileResourceManagerFactory
    extends AbstractFactoryBean<FileResourceManager>
{
    private final Log log = LogFactory.getLog( getClass() );

    private File storeDir;

    private File workDir;

    private ResourceIdToPathMapper resourceIdToPathMapper;

    private TransactionIdToPathMapper transactionIdToPathMapper;

    private LoggerFacade loggerFacade;

    /**
     * Required. Where the commited work will get persisted. Should reside on the same filesystem as {@link #workDir}
     * for performance reasons.
     * 
     * @param storeDir
     */
    public void setStoreDir( File storeDir )
    {
        this.storeDir = storeDir;
    }

    /**
     * Required. The working directory for uncommited work. Should reside on same filesystem as {@link #storeDir} for
     * performance reasons.
     * 
     * @param workDir
     */
    public void setWorkDir( File workDir )
    {
        this.workDir = workDir;
    }

    /**
     * Optional. The {@link LoggerFacade} for the underlying resource manager.
     * 
     * @param loggerFacade
     */
    public void setLoggerFacade( LoggerFacade loggerFacade )
    {
        this.loggerFacade = loggerFacade;
    }

    /**
     * Optional. If none defined no mapping will be performed.
     * 
     * @param resourceIdToPathMapper
     */
    public void setResourceIdToPathMapper( ResourceIdToPathMapper resourceIdToPathMapper )
    {
        this.resourceIdToPathMapper = resourceIdToPathMapper;
    }

    /**
     * Optional. Maps transactionIds to path. If nonde defined no mapping will be performed.
     * 
     * @param transactionIdToPathMapper
     */
    public void setTransactionIdToPathMapper( TransactionIdToPathMapper transactionIdToPathMapper )
    {
        this.transactionIdToPathMapper = transactionIdToPathMapper;
    }

    /**
     * Bootstraps the instance and sanity checks configuration.
     */
    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        Assert.notNull( storeDir, "StoreDir must be defined" );
        Assert.notNull( workDir, "workDir must be defined" );


        if ( transactionIdToPathMapper == null ) {
            this.transactionIdToPathMapper = new NoOpTransactionIdToPathMapper();
        }
        
        if ( loggerFacade == null )
        {
            log.info( "Using commons logging logger facade" );
            Log targetLogger = LogFactory.getLog( FileResourceManager.class );
            loggerFacade = new CommonsLoggingLogger( targetLogger );
        }

        super.afterPropertiesSet();

        log.info( "LoggerFacade: " + loggerFacade );
        log.info( "ResourceIdPathMapper: " + resourceIdToPathMapper );
        log.info( "TranscationIdToPathMapper: " + transactionIdToPathMapper );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileResourceManager createInstance()
        throws Exception
    {
        FileResourceManager fileResourceManager =
            new FileResourceManager( storeDir.getAbsolutePath(), workDir.getAbsolutePath(),
                                     this.resourceIdToPathMapper, this.transactionIdToPathMapper, loggerFacade,
                                     loggerFacade.isFineEnabled() );

        return fileResourceManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends FileResourceManager> getObjectType()
    {
        return FileResourceManager.class;
    }
}
