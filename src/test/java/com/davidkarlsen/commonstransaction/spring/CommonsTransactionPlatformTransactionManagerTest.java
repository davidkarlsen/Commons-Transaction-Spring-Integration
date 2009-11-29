package com.davidkarlsen.commonstransaction.spring;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.util.Assert;

/**
 * 
 */
public class CommonsTransactionPlatformTransactionManagerTest
{
    private final DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();

    private CommonsTransactionPlatformTransactionManager commonsTransactionPlatformTransactionManager;

    private FileResourceManager fileResourceManager;

    private File workDir;

    private File storeDir;

    @Before
    public void before()
        throws Exception
    {
        commonsTransactionPlatformTransactionManager = new CommonsTransactionPlatformTransactionManager();
        File tmpDir = SystemUtils.getJavaIoTmpDir();
        storeDir = new File( tmpDir, "storedir" );
        workDir = new File( tmpDir, "workDir" );

        FileUtils.deleteDirectory( storeDir );
        FileUtils.deleteDirectory( workDir );

        FileResourceManagerFactory fileResourceManagerFactory = new FileResourceManagerFactory();
        fileResourceManagerFactory.setStoreDir( storeDir );
        fileResourceManagerFactory.setWorkDir( workDir );
        fileResourceManagerFactory.setUrlEncodePath( false );
        fileResourceManagerFactory.afterPropertiesSet();

        fileResourceManager = (FileResourceManager) fileResourceManagerFactory.createInstance();

        commonsTransactionPlatformTransactionManager.setFileResourceManager( fileResourceManager );
        commonsTransactionPlatformTransactionManager.afterPropertiesSet();
    }

    @Test
    public void testCommit()
        throws ResourceManagerException
    {
        TransactionStatus transactionStatus =
            commonsTransactionPlatformTransactionManager.getTransaction( defaultTransactionDefinition );
        String fileName = "someFileName";
        fileResourceManager.createResource( ( (DefaultTransactionStatus) transactionStatus ).getTransaction(), fileName );
        commonsTransactionPlatformTransactionManager.commit( transactionStatus );

        Assert.isTrue( new File( storeDir, fileName ).exists() );
        // commonsTransactionPlatformTransactionManager.rollback( transactionStatus );
    }
    
    @Test
    public void testRollback()
        throws ResourceManagerException
    {
        TransactionStatus transactionStatus =
            commonsTransactionPlatformTransactionManager.getTransaction( defaultTransactionDefinition );
        String fileName = "someFileName";
        fileResourceManager.createResource( ( (DefaultTransactionStatus) transactionStatus ).getTransaction(), fileName );
        commonsTransactionPlatformTransactionManager.rollback( transactionStatus );

        Assert.isTrue( ! new File( storeDir, fileName ).exists() );
    }


}
