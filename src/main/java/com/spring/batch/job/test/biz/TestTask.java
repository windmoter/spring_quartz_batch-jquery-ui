package com.spring.batch.job.test.biz;

  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.spring.batch.job.test.mapperInf.TestMapper;
import com.spring.web.common.BatchTask;

public class TestTask extends BatchTask {
    private final Logger LOGGER = LoggerFactory.getLogger(TestTask.class);

    @Autowired
    TestMapper testMapper;

    public void executeTask(StepContribution contribution, ChunkContext context) throws Exception {
        String name = testMapper.selectName(3);
        LOGGER.info("name: {}", name);
    }
}
