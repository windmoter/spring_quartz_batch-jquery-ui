package com.spring.web.schedule;

 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.spring.web.listener.BatchJobExecutionListener;

/**
 * @Project : LGCOM Tax Project
 * @Class : SpringBatchJob.java
 * @Description : 
 * @Author : LGCNS
 * @Since : 2018. 3. 13.
 *
 * @Copyright ⓒ LG CNS-HHI Consortium
 *-------------------------------------------------------
 * Modification Information
 *-------------------------------------------------------
 * Date            Modifier             Reason 
 *-------------------------------------------------------
 * 2018. 3. 13.        LGCNS              initial
 *-------------------------------------------------------
 */
@DisallowConcurrentExecution
public class BatchJobExecutor implements Job {
    private final Logger LOGGER = LogManager.getLogger(BatchJobExecutor.class);

    @Autowired
    private JobLocator jobLocator;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    BatchJobExecutionListener batchJobExecutionListener;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    	try {
            String jobName = BatchHelper.getJobName(context.getMergedJobDataMap());
            LOGGER.debug("jobName::: {}", jobName);
            JobParameters jobParameters = BatchHelper.getJobParameters(context);

            LOGGER.info("[{}] STARTED, PARAMETERS:[{}]", jobName, jobParameters.getParameters().toString());

            AbstractJob job =  (AbstractJob) jobLocator.getJob(jobName);
            job.registerJobExecutionListener(batchJobExecutionListener);
            //jobLauncher.run(jobLocator.getJob(jobName), jobParameters);
            jobLauncher.run(job, jobParameters);
            
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException e) {
            LOGGER.error("Job Already execution Exception! - ", e.getMessage());
        } 
    	catch (NoSuchJobException | JobRestartException | JobParametersInvalidException | SchedulerException e) {
            LOGGER.error("Job execution exception! - ", e);
            throw new JobExecutionException();
        }
    }
}
