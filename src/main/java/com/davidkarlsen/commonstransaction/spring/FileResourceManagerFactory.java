package com.davidkarlsen.commonstransaction.spring;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.util.CommonsLoggingLogger;
import org.apache.commons.transaction.util.LoggerFacade;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;

public class FileResourceManagerFactory
    extends AbstractFactoryBean
{
    private final Log log = LogFactory.getLog( getClass() );

    private File storeDir;
    private File workDir;
    private boolean urlEncodePath;
    private LoggerFacade loggerFacade;
    
    public void setStoreDir( File storeDir )
    {
        this.storeDir = storeDir;
    }
    
    public void setWorkDir( File workDir )
    {
        this.workDir = workDir;
    }
    
    public void setUrlEncodePath( boolean urlEncodePath )
    {
        this.urlEncodePath = urlEncodePath;
    }
    
    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        Assert.notNull( storeDir, "StoreDir must be defined" );
        Assert.notNull( workDir, "workDir must be defined" );
        
        if ( loggerFacade == null ) {
            log.info( "Using commons logging logger facade" );
            Log targetLogger = LogFactory.getLog( FileResourceManager.class );
            loggerFacade = new CommonsLoggingLogger( targetLogger );
        }
        
        super.afterPropertiesSet();
    }

    @Override
    protected Object createInstance()
        throws Exception
    {
        FileResourceManager fileResourceManager =
            new FileResourceManager( storeDir.getAbsolutePath(), workDir.getAbsolutePath(), urlEncodePath, loggerFacade );

        return fileResourceManager;
    }

    @Override
    public Class<?> getObjectType()
    {
        return FileResourceManager.class;
    }

}
