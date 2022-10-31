package com.spring.web.schedule;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;


@DisallowConcurrentExecution
public class SchedulerJob extends QuartzJobBean {
	private final Logger LOGGER = LogManager.getLogger(SchedulerJob.class);

/*	private String batchJob;
	public void setBatchJob(String batchJob){
		this.batchJob = batchJob;
	}*/
	@Autowired
	private JobLocator jobLocator;
	
	@Autowired
	private JobLauncher jobLauncher;
	
	/*@Resource(name="springBatchJob")
	private SpringBatchJob springBatchJob;
	*/
	
	
	@Override
	protected void executeInternal(JobExecutionContext context){
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

		JobDataMap jobDataMap = context.getMergedJobDataMap();
		LOGGER.info("Quartz jobLauncher started:{} ", jobLauncher);
		LOGGER.info("Quartz jobLocator started:{} ", jobLocator);
		try{
			JobParameters jobParameters = BatchHelper.getJobParameters(context);
			String jobName = (String) jobDataMap.get("job");
			LOGGER.info("[{}] started.", jobName);
	        LOGGER.info("[{}] completed, parameter[{}]", jobName, jobParameters.getParameters().toString());
	            
			//JobExecution result = jobLauncher.run(jobLocator.getJob("JOBAA0001"), new JobParameters(map));
			JobExecution result = jobLauncher.run(jobLocator.getJob(jobName), jobParameters);
			
			//springBatchJob.performJob();
			
		}catch(Exception exception){
			//System.out.println("Job "+ batchJob+" could not be executed : "+ exception.getStackTrace());
			LOGGER.error("Job could not be executed :{} ", exception);
		}
		LOGGER.debug("Quartz job end");
	}
}