package com.spring.web.controller;

 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.web.common.BatchLog;
import com.spring.web.listener.BatchJobListener;
import com.spring.web.schedule.BatchJobExecutor;
import com.spring.web.service.ScheduleService;
import com.spring.web.vo.BatchJob;
import com.spring.web.vo.Schedule;
 

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class MainController {
    private final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private final String JOB_GROUP = "JOB";
    private final String TRIGGER_GROUP = "TRIGGER";

    private final ScheduleService scheduleService;
    private final SchedulerFactoryBean schedulerFactory;
    private final BatchJobListener batchJobListener;
    private final JobRegistry jobRegistry;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final BatchLog batchLog;

    public MainController(ScheduleService scheduleService, SchedulerFactoryBean schedulerFactory, BatchJobListener batchJobListener, JobRegistry jobRegistry, JobExplorer jobExplorer, JobOperator jobOperator, BatchLog batchLog) {
        this.scheduleService = scheduleService;
        this.schedulerFactory = schedulerFactory;
        this.batchJobListener = batchJobListener;
        this.jobRegistry = jobRegistry;
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
        this.batchLog = batchLog;
    }

    @PostConstruct
    public void init() {
        Scheduler scheduler = schedulerFactory.getScheduler();
        LOGGER.debug("Initialize Schedules: {}", scheduler);
        try {
            scheduler.start();
            scheduler.getListenerManager().addJobListener(batchJobListener);
        } catch (SchedulerException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
        // Schedule Setting Initialize
        List<Schedule> schedules = scheduleService.getSchedules(null);
        
        insertSchedules(schedules);
    }

    public List<String> insertSchedules(List<Schedule> schedules) {
        List<String> scheduleIds = new ArrayList<>();
        Scheduler scheduler = schedulerFactory.getScheduler();
        LOGGER.debug("Insert Schedules Start...");
        
        for(Schedule schedule : schedules) {
        	LOGGER.info("=====insertSchedules schedule : " + schedule.getDescription());
            try {
                Map<String, String> parameters = Stream.of(schedule.getPgmParam().split(","))
                        .map(String::trim).map(e -> e.split("=", 2))
                        .collect(Collectors.toMap(e -> e[0], e -> e.length > 1 ? e[1] : ""));
                LOGGER.info("{} => parameters: {}", schedule.getBatchWorkNo(), parameters);
                String cronExpression = schedule.getCronExpression();
                JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

                if(parameters != null) {
                    parameters.forEach((k, v) -> {
                        if(k != null && k.length() > 0)
                            jobParametersBuilder.addString(k, v);
                    });
                }
                jobParametersBuilder.addString("EXEC_PRD_TP", schedule.getExecPrdTp());

                JobParameters jobParameters = jobParametersBuilder.toJobParameters();
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("jobParameters", jobParameters);
                String jobId = schedule.getBatchWorkNo();
                JobKey jobKey = new JobKey(jobId, JOB_GROUP);
                JobDetail jobDetail = JobBuilder.newJob(BatchJobExecutor.class)
                        .withIdentity(jobKey)
                        .usingJobData("job", jobId)
                        .usingJobData(jobDataMap)
                        .storeDurably()
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(schedule.getBatchScheNo(), TRIGGER_GROUP)
                        .startNow()
                        .forJob(jobDetail)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .build();

                LOGGER.debug("Schedule: {}", scheduler);
                if(scheduler.checkExists(jobDetail.getKey())) {
                    List<CronTrigger> triggers = (List<CronTrigger>) scheduler.getTriggersOfJob(jobKey);
                    triggers.stream().anyMatch(tri -> cronExpression.equals(tri.getCronExpression()));
                    if(!triggers.stream().anyMatch(tri -> cronExpression.equals(tri.getCronExpression()))) {
                        scheduler.scheduleJob(trigger);
                        scheduleIds.add(schedule.getBatchScheNo());
                    } else {
                        LOGGER.error("Someone has already scheduled such job/trigger. {} : {}", jobDetail.getKey(), trigger.getKey());
                    }
                } else {
                    scheduler.scheduleJob(jobDetail, trigger);
                    scheduleIds.add(schedule.getBatchScheNo());
                }
            } catch (Exception e) {
                LOGGER.error("Insert Schedule Error: {}", e);
            }
        }
        LOGGER.info("=====insertSchedules scheduleIds : " + scheduleIds);
        LOGGER.debug("Insert Schedules End..");
        return scheduleIds;
    }

    // Main 화면
    @RequestMapping(value = "/schedules", method = RequestMethod.GET)
    public String jobList(Model model) {
        Map<String, String> jobs = new TreeMap<>();
        jobRegistry.getJobNames().forEach(e -> jobs.put(e, e));

        model.addAttribute("jobs", jobs);
        model.addAttribute("schedules", scheduleService.getSchedules(null));
        model.addAttribute("runnings", batchLog.getRunningJob());

        return "schedule";
    }

    @RequestMapping(value = "/job/run/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String run(@PathVariable("id") String id,
                      @RequestBody(required = false) Map<String, Object> body) {
        LOGGER.info("POST, id: {}, body: {}", id, body);

        if(isJobRunning(id)) {
            return "{\"STATUS\":\"Same job is already running.\"}";
        }

        Map<String, Object> map = body;
        try {
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
            if(map != null) {
                Map<String, Object> param = (Map<String, Object>) map.get("jobParameters");
                if(param != null && param.size() > 0) {
                    param.forEach((k, v) -> jobParametersBuilder.addString(k, String.valueOf(v)));
                }
            }

            JobParameters jobParameters = jobParametersBuilder.toJobParameters();
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("jobParameters", jobParameters);
            JobKey jobKey = new JobKey(id, JOB_GROUP);
            JobDetail jobDetail = JobBuilder.newJob(BatchJobExecutor.class)
                    .withIdentity(jobKey)
                    .usingJobData("job", id)
                    .usingJobData(jobDataMap)
                    .storeDurably()
                    .build();

            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.addJob(jobDetail, true);
            scheduler.triggerJob(jobKey, jobDataMap);
        } catch(Exception e) {
            LOGGER.error("Job List Error: {}", e);
        }
        return "{\"STATUS\":\"OK\"}";
    }

    @RequestMapping(value = "/job/stop/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String stop(@PathVariable("id") String id) {
        LOGGER.info("POST, id: {}", id);

        if(!isJobRunning(id)) {
            return "{\"STATUS\":\"Job is not running now.\"}";
        }
        StopJob(id);

        return "{\"STATUS\":\"OK\"}";
    }

    @RequestMapping(value = "/job/modify", method = RequestMethod.POST)
    @ResponseBody
    public String modify(@RequestParam Map<String, String> body) {
        LOGGER.info("modifyJob: {}", body);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Schedule schedule = objectMapper.convertValue(body, Schedule.class);
            // BTOCSITE-7159 start
            String descriptionStr = ""; 
            if(body.get("description")!=null){
            	if(!body.get("description").equals("")){
            		descriptionStr = body.get("description");
            	}
            }
            LOGGER.info("======= modify body.get(description) : ", body.get("description"));
            schedule.setDescription(descriptionStr.replace("\n", "<br>"));
            LOGGER.info("======= modify descriptionStr : ", descriptionStr);
            LOGGER.info("======= modify schedule.getDescription : ", schedule.getDescription());
            // BTOCSITE-7159 end
            
            if(schedule.getDayOfWeek() == null) {
                schedule.setDayOfWeek("NNNNNNN");
            }
            

            // Error occurs when ID is null
            schedule.setId("tester");
/*
            CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
            CronDescriptor cronDescriptor = CronDescriptor.instance(Locale.US);
            CronParser cronParser = new CronParser(cronDefinition);
            String description = cronDescriptor.describe(cronParser.parse(schedule.getCronExpression()));
            schedule.setRmk(description);
*/
            scheduleService.modifySchedule(schedule);

            Scheduler scheduler = schedulerFactory.getScheduler();
            Trigger oldTrigger = scheduler.getTrigger(TriggerKey.triggerKey(schedule.getBatchScheNo(), TRIGGER_GROUP));

            TriggerBuilder triggerBuilder = oldTrigger.getTriggerBuilder();
            Trigger newTrigger = triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(schedule.getCronExpression())).build();

            scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
        } catch (SchedulerException e) {
            return "{\"STATUS\":\"Failed to modify job schedule.\"}";
        }
        return "{\"STATUS\":\"OK\"}";
    }

    @RequestMapping(value = "/schedule/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteSchedule(@RequestParam Map<String, String> body) {
        LOGGER.info("deleteSchedule: {}", body);
        scheduleService.deleteSchedule(body.get("batchWorkNo"));

        List<String> deleteList = new ArrayList<>();
        deleteList.add(body.get("batchWorkNo"));

        if(!deleteList.isEmpty()) {
            deleteSchedules(deleteList);
        }
        return "{\"STATUS\":\"OK\"}";
    }

    @RequestMapping(value = "/schedule/save", method = RequestMethod.POST)
    @ResponseBody
    public String saveSchedule(@RequestParam Map<String, String> body) {
        LOGGER.info("saveSchedule: {}", body);

        ObjectMapper objectMapper = new ObjectMapper();
        Schedule schedule = objectMapper.convertValue(body, Schedule.class);
     // BTOCSITE-7159 start
        String descriptionStr = ""; 
        if(body.get("description")!=null){
        	if(!body.get("description").equals("")){
        		descriptionStr = body.get("description");
        	}
        }
        LOGGER.info("======= modify body.get(description) : ", body.get("description"));
        schedule.setDescription(descriptionStr.replace("\n", "<br>"));
        LOGGER.info("======= modify descriptionStr : ", descriptionStr);
        LOGGER.info("======= modify schedule.getDescription : ", schedule.getDescription());
        // BTOCSITE-7159 end
        BatchJob batchJob = new BatchJob();
        batchJob.setBatchWorkNo(schedule.getBatchWorkNo());
        batchJob.setBatchWorkNm(schedule.getBatchWorkNo());
        batchJob.setId("tester");

        if(scheduleService.getJob(batchJob.getBatchWorkNo()) == null) {
            scheduleService.putJob(batchJob);
        }

        if(schedule.getDayOfWeek() == null) {
            schedule.setDayOfWeek("NNNNNNN");
        }

        schedule.setId("tester");
        schedule.setBatchScheNo("");
/*
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        CronDescriptor cronDescriptor = CronDescriptor.instance(Locale.US);
        CronParser cronParser = new CronParser(cronDefinition);
        String description = cronDescriptor.describe(cronParser.parse(schedule.getCronExpression()));
        schedule.setRmk(description);
*/
        scheduleService.putSchedule(schedule);

        List<String> insertList = new ArrayList<>();
        insertList.add(schedule.getBatchScheNo());
        Map<String, Object> param = new HashMap<>();
        param.put("ids", insertList);
        List<Schedule> schedules = scheduleService.getSchedules(param);

        if(!schedules.isEmpty()) {
            insertSchedules(schedules);
        }
        return "{\"STATUS\":\"OK\"}";
    }

    @RequestMapping(value = "/runningjobs", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity runningJobs() {
        return new ResponseEntity(batchLog.getRunningJob(), HttpStatus.OK);
    }

    public boolean isJobRunning(String id) {
        Scheduler scheduler = schedulerFactory.getScheduler();
        List<JobExecutionContext> currentJobs;
        try {
            currentJobs = scheduler.getCurrentlyExecutingJobs();
            // if(!NullUtil.isNone(id) && currentJobs.size() > 0) {
            //     return true;
            // }
            for(JobExecutionContext jobExecutionContext : currentJobs) {
                String jobName = jobExecutionContext.getJobDetail().getKey().getName();
                String groupName = jobExecutionContext.getJobDetail().getKey().getName();
                LOGGER.debug("JobName: {}, JobGroup: {}", jobName, groupName);
                if(id.equalsIgnoreCase(jobName)) {
                    return true;
                }
            }
        } catch(SchedulerException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    public void StopJob(String jobName) {
        try {
            for(JobExecution jobExecution : jobExplorer.findRunningJobExecutions(jobName)) {
                LOGGER.info(String.valueOf(jobExecution.getId()));

                BatchStatus status = jobExecution.getStatus();
                if(status == BatchStatus.STARTED || status == BatchStatus.STARTING) {
                    jobOperator.stop(jobExecution.getId());
                }
                batchLog.removeHistoryMapForStopJob(jobName);
            }
        } catch (JobExecutionNotRunningException | NoSuchJobExecutionException e) {
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private List<String> deleteSchedules(List<String> param) {
        List<String> scheduleIds = new ArrayList<>();
        Scheduler scheduler = schedulerFactory.getScheduler();
        try {
            for(String groupName : scheduler.getJobGroupNames()) {
                for(JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    List<CronTrigger> triggers = (List<CronTrigger>) scheduler.getTriggersOfJob(jobKey);
                    triggers.forEach(
                            trigger -> param.stream()
                                .filter(s -> trigger.getKey().toString().indexOf(s) > 0)
                                .forEach(scheduleId -> {
                                    try {
                                        scheduler.pauseTrigger(trigger.getKey());
                                        scheduler.unscheduleJob(trigger.getKey());
                                        scheduleIds.add(scheduleId);
                                        LOGGER.debug("Schedule deleted matched key: {}, Expression: {}", trigger.getKey(), trigger.getCronExpression());
                                    } catch (SchedulerException e) {
                                        LOGGER.error("Delete Schedule Error: {}", e);
                                    }
                                })
                    );
                }
            }
        } catch (SchedulerException e) {
            LOGGER.error("Delete Schedule Error: {}", e);
        }
        return scheduleIds;
    }
}
