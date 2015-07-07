package com.davidkarlsen.commonstransaction.spring.issue1;

import com.davidkarlsen.commonstransaction.spring.TransactionAwareFileResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author et2448
 * @since 7/7/15
 */
@Service
@Transactional("fileTransactionManager")
public class FileServiceTwo
{
    @Autowired
    protected TransactionAwareFileResourceManager transactionAwareFileResourceManager;

    public void test() throws Exception {
        transactionAwareFileResourceManager.createResource("1");
        throw new RuntimeException();

    }

    public void test1() throws Exception {
       transactionAwareFileResourceManager.createResource("2");
    }
}
