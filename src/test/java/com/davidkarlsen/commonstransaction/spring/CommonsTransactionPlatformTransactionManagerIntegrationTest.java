package com.davidkarlsen.commonstransaction.spring;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class CommonsTransactionPlatformTransactionManagerIntegrationTest
{
    @Autowired
    private SomeService someService;

    private File storeDir;

    @Before
    public void before()
        throws IOException
    {
        storeDir = new File( SystemUtils.JAVA_IO_TMPDIR, "storedir" );
        FileUtils.cleanDirectory( storeDir );
    }

    private void assertFileExists( String resourceId )
    {
        File file = new File( storeDir, resourceId );
        Assert.assertTrue( file.exists() );
    }

    private void assertFileNotExists( String resourceId )
    {
        File file = new File( storeDir, resourceId );
        Assert.assertFalse( file.exists() );
    }

    @Test
    public void testCreateDelete()
        throws ResourceManagerException
    {
        final String resourceId = "someId";

        someService.create( resourceId );
        assertFileExists( resourceId );

        someService.delete( resourceId );
        assertFileNotExists( resourceId );
    }

    @Test
    public void testRollback()
        throws IOException, ResourceManagerException
    {
        final String fileName = "someFileName";
        try
        {
            someService.writeStringToFile( "someString", fileName, new RuntimeException() );
        }
        catch ( RuntimeException e )
        {
            assertFileNotExists( fileName );
        }
    }

    @Test
    public void testWriteRead()
        throws Exception
    {
        final String fileName = "someExistFile";
        final String fileContents = "someString";
        someService.writeStringToFile( fileContents, fileName, null );
        assertFileExists( fileName );

        String compareString = someService.readFile( fileName );
        Assert.assertEquals( fileContents, compareString );
    }

    @Test( expected = ResourceManagerException.class )
    public void testInvalidFileName()
        throws IOException, ResourceManagerException
    {
        someService.writeStringToFile( "", "", null );
    }

    @Test( expected = ResourceManagerException.class )
    public void testNullFileName()
        throws IOException, ResourceManagerException
    {
        someService.writeStringToFile( "", null, null );
    }

    @Test
    public void testDeleteNonExisting()
        throws ResourceManagerException
    {
        someService.delete( "notExisting" );
    }

}
