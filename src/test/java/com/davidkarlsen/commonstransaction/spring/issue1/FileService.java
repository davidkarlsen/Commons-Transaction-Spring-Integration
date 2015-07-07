package com.davidkarlsen.commonstransaction.spring.issue1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author et2448
 * @since 7/7/15
 */
@Service
@Transactional("fileTransactionManager")
public class FileService
{
    @Autowired
    private FileServiceTwo fileServiceTwo;

    public void test() throws Exception {
        fileServiceTwo.test1();
        fileServiceTwo.test();
    }


}
