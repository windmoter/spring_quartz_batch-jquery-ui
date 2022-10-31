package com.spring.web.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class BatchJobListener implements JobListener {
	private final Logger LOGGER = LoggerFactory.getLogger(BatchJobListener.class);
	public static final String BATCH_JOB_LISTENER_NAME = "BatchJobListener";

	@Override
	public String getName() {
		return BATCH_JOB_LISTENER_NAME;
	}

	// Run this if job is about to be executed
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {

		String jobName = context.getJobDetail().getKey().toString();
		ThreadContext.put("jobName", jobName);
		ThreadContext.put("jobStartTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		LOGGER.debug("[Job: {} is going to start..]", jobName);
	}

	// No idea when will run this?
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		LOGGER.debug("jobExecutionVetoed");
	}

	// Run this after job has been executed
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		String jobName = context.getJobDetail().getKey().toString();
		Map<String, Object> inputData = new HashMap<>();
        
		if(jobException != null) {
			LOGGER.debug("jobException is {}", jobException);
			if(!"".equals(jobException.getMessage())) {
            	LOGGER.error("Exception thrown by: {} Exception: {}", jobName, jobException.getMessage());
            }
        }
		LOGGER.debug("[Job: {} is end..]", jobName);
	}
}
