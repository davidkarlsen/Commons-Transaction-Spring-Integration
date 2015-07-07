package com.davidkarlsen.commonstransaction.spring.issue1;

import com.davidkarlsen.commonstransaction.spring.CommonsTransactionPlatformTransactionManager;
import com.davidkarlsen.commonstransaction.spring.FileResourceManagerFactory;
import com.davidkarlsen.commonstransaction.spring.TransactionAwareFileResourceManager;
import org.apache.commons.transaction.file.FileResourceManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.IOException;

/**
 * @author et2448
 * @since 7/7/15
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = Issue1Test.TestConfig.class )
public class Issue1Test
    extends Assert
{
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder( new File( "target" ) );
    static File workDir;
    static File storeDir;

    //@Autowired
    //private FileServiceTwo fileServiceTwo;

    @Autowired
    private FileService fileService;

    @BeforeClass
    public static void before() throws IOException {
        workDir = temporaryFolder.newFolder( "workDir" );
        storeDir = temporaryFolder.newFolder( "storeDir" );
    }

    @Configuration
    @EnableTransactionManagement( proxyTargetClass = true )
    public static class TestConfig
    {
        @Bean
        public FileService fileService()
        {
            return new FileService();
        }

        @Bean
        public FileServiceTwo fileServiceTwo()
        {
            return new FileServiceTwo();
        }

        @Bean
        public FileResourceManagerFactory fileResourceManagerFactory()
            throws IOException
        {
            FileResourceManagerFactory fileResourceManagerFactory = new FileResourceManagerFactory();
            fileResourceManagerFactory.setStoreDir( storeDir );
            fileResourceManagerFactory.setWorkDir( workDir );

            return fileResourceManagerFactory;
        }

        @Bean
        public TransactionAwareFileResourceManager transactionAwareFileResourceManager(
            FileResourceManager fileResourceManager )
        {
            TransactionAwareFileResourceManager transactionAwareFileResourceManager =
                new TransactionAwareFileResourceManager();
            transactionAwareFileResourceManager.setFileResourceManager( fileResourceManager );

            return transactionAwareFileResourceManager;
        }

        @Bean
        public CommonsTransactionPlatformTransactionManager fileTransactionManager(
            FileResourceManager fileResourceManager )
            throws Exception
        {
            CommonsTransactionPlatformTransactionManager commonsTransactionPlatformTransactionManager =
                new CommonsTransactionPlatformTransactionManager();
            commonsTransactionPlatformTransactionManager.setFileResourceManager( fileResourceManager );

            return commonsTransactionPlatformTransactionManager;
        }
    }

    @Test
    public void testIssue1()
        throws Exception
    {
        try
        {
            fileService.test();
            fail();
        }
        catch ( RuntimeException e ) {
            assertEmptyDirectory( workDir );
            assertEmptyDirectory( storeDir );
        }
    }



    private void assertEmptyDirectory( File file ) {
        assertTrue( file.isDirectory() && file.list().length == 0 );
    }
}
