package com.spring.web.service;
  

import java.util.List;
import java.util.Map;

import com.spring.web.vo.BatchJob;
import com.spring.web.vo.Schedule;

public interface ScheduleService {
    List<Schedule> getSchedules(Map<String, Object> ids);
    int putSchedule(Schedule schedule);
    int modifySchedule(Schedule schedule);
    int deleteSchedule(String batchWorkNo);
    int putJob(BatchJob batchJob);
    BatchJob getJob(String batchJobNo);
    Map<String, Object> getStartTime(String stepName);
}
