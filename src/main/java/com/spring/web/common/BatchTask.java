package com.spring.web.common;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

abstract public class BatchTask {
    abstract public void executeTask(StepContribution contribution, ChunkContext context) throws Exception;
}
