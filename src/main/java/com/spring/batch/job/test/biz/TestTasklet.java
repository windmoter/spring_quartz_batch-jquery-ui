package com.spring.batch.job.test.biz;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class TestTasklet implements Tasklet {

    @Autowired
    TestTask testTask;

    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        testTask.executeTask(stepContribution, chunkContext);
        return RepeatStatus.FINISHED;
    }
}
