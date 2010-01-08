package com.davidkarlsen.commonstransaction.spring;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.transaction.file.ResourceManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TestService
{
    @Autowired
    private TransactionAwareFileResourceManager transactionAwareFileResourceManager;

    public void writeStringToFile( String string, String filename, RuntimeException exceptionToThrow )
        throws IOException, ResourceManagerException
    {
        OutputStream outputStream = transactionAwareFileResourceManager.writeResource( filename );
        outputStream.write( string.getBytes() );
        outputStream.flush();
        outputStream.close();
        
        if ( exceptionToThrow != null )
            throw exceptionToThrow;
    }

}
