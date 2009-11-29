package com.davidkarlsen.commonstransaction.spring;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.transaction.file.ResourceManagerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Transactional;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
@Transactional
@TransactionConfiguration( defaultRollback=true )
public class CommonsTransactionPlatformTransactionManagerIntegrationTest
{
    @Autowired
    private TransactionAwareFileResourceManager transactionAwareFileResourceManager;
    
    @Test
    public void testCreate() throws ResourceManagerException {
        transactionAwareFileResourceManager.createResource( "someName" );
    }
    
    @Test
    public void testWrite() throws IOException, NoTransactionException, ResourceManagerException {
        OutputStream outputStream = transactionAwareFileResourceManager.writeResource( "someName" );
        outputStream.write( 1 );
        outputStream.flush();
        outputStream.close();
    }

}
