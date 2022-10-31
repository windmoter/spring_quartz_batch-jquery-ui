package com.spring.web.listener;

import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spring.web.common.BatchLog; 
 

@Component
public class BatchJobExecutionListener implements JobExecutionListener {
	private final Logger LOGGER = LoggerFactory.getLogger(BatchJobExecutionListener.class);

	private final BatchLog batchLog; 

	@Autowired
	//private SendSlack slack;

	public BatchJobExecutionListener(BatchLog batchLog) {
		this.batchLog = batchLog;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		LOGGER.info("JobExecutionListener - @beforeJob: This instance: {}", this);
		Map<String, Object> inputData = new HashMap<>();

		try {
			Calendar calendar = new GregorianCalendar();
			Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
			String clsYmd = sdf.format(timestamp);
			String jobName = jobExecution.getJobInstance().getJobName();
			LOGGER.info("JobExecutionListener - @beforeJob: {}", jobName);
		//	if(!jobName.equals(CstLmsVO.CST_LMS_JOB)){ //CstLmsJob인경우 history INSERT 안되도록 
			inputData.put("clsYyyyMmDd", clsYmd);
			inputData.put("batchId", jobName);

			String batchHistoryId = batchLog.init(inputData);
			jobExecution.getExecutionContext().put("batchHistoryId", batchHistoryId);
		//	}
		} catch(Exception e) {
			LOGGER.error("JobExecutionListener Error: {}", e);
		}
	}
	 
	@Override
	public void afterJob(JobExecution jobExecution) {
		LOGGER.info("After Job!: {}", jobExecution.getExecutionContext().get("batchHistoryId"));
		String jobName = jobExecution.getJobInstance().getJobName();
		String batchHistoryId = (String)jobExecution.getExecutionContext().remove("batchHistoryId");
	//	if(!jobName.equals(CstLmsVO.CST_LMS_JOB)){
			if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
				batchLog.set(batchHistoryId, "successFlag", "Y");
				
				if (jobExecution.getExecutionContext().containsKey("batchDataCount")) {
					batchLog.set(batchHistoryId, "batchDataCount", jobExecution.getExecutionContext().getString("batchDataCount"));
				}
				if (jobExecution.getExecutionContext().containsKey("batchResultContent")) {
					batchLog.set(batchHistoryId, "batchResultContent", jobExecution.getExecutionContext().getString("batchResultContent"));
				}
				
				LOGGER.info("BatchStatus.COMPLETED - After Job!: {}", jobExecution.getStatus());
	
			} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
	        	LOGGER.info("BatchStatus.FAILED - After Job!: {}", jobExecution.getStatus());
	        	List<Throwable> exceptions = jobExecution.getAllFailureExceptions();
	        	String slackMessage = "";

	        	if(exceptions != null && !exceptions.isEmpty()) {
	        		for(Throwable exception : exceptions) {
	        			
	        			/**
	        			 * Job ID : customerDuplicateCheckingJob 일때 Batch Count > 0 일때
	        			 * 일부러 Exception 처리하여 Batch Log 쌓도록 하기 위한 로직
	        			 */
	        			if(exception.getMessage().contains("DUPLE")) {
	        				String msg[] = exception.getMessage().split(",");
							batchLog.set(batchHistoryId, "batchResultContent",  msg[0]);
							batchLog.set(batchHistoryId, "batchDataCount",  msg[1]);
	        			} else {
	        				String detailErr = "";
	        				if (jobExecution.getExecutionContext().containsKey("batchResultContent")) {
	        					batchLog.set(batchHistoryId, "batchResultContent", jobExecution.getExecutionContext().getString("batchResultContent"));
	        					detailErr = jobExecution.getExecutionContext().getString("batchResultContent");
	        				}
	        				if(!detailErr.equals("")){
	        					batchLog.set(batchHistoryId, "batchResultContent", "Error!" + exception.getMessage() + "\ndetailErr [ " + detailErr + " ]");
	        				}else{
	        					batchLog.set(batchHistoryId, "batchResultContent", "Error!" + exception.getMessage());
	        				}
							
							LOGGER.info("BatchStatus.FAILED - After Job Exception: {}", exception.getMessage());
	        			}

	        			slackMessage += exception.getMessage() + "\n";
	        		}
	        	}
				batchLog.set(batchHistoryId, "successFlag", "N");

				// Exception 발생시 slack에 알림전송
				String profileEnv = "profileEnv";
				if("PROD".equals(profileEnv)) {
					/*
					int slackResCode = slack.sendMessage(jobName, batchHistoryId, slackMessage);
					if(slackResCode == HttpURLConnection.HTTP_OK) {
						LOGGER.info("BatchStatus.FAILED - SendSlack Success >> jobName [{}], batchHistoryId [{}]", jobName, batchHistoryId);
					} else {
						LOGGER.info("BatchStatus.FAILED - SendSlack Exception: {}", slackResCode);
					}
					*/
				}
			}
			batchLog.print(batchHistoryId);
			LOGGER.info("JobExecutionListener - @afterJob: {}", jobExecution.getStatus());
		}
 	//}
}
