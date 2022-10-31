package com.spring.web.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spring.web.vo.BatchJob;
import com.spring.web.vo.Schedule;

import batch.mapper.inf.ScheduleMapper;

@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;

    public ScheduleServiceImpl(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public List<Schedule> getSchedules(Map<String, Object> ids) {
        return scheduleMapper.getSchedules(ids);
    }

    @Override
    public int putSchedule(Schedule schedule){
        return scheduleMapper.putSchedule(schedule);
    }

    @Override
    public int deleteSchedule(String batchWorkNo){
        return scheduleMapper.deleteSchedule(batchWorkNo);
    }

    @Override
    public int putJob(BatchJob batchJob){
        return scheduleMapper.putJob(batchJob);
    }

    @Override
    public BatchJob getJob(String batchJobNo) {
        return scheduleMapper.getJob(batchJobNo);
    }

    @Override
    public int modifySchedule(Schedule schedule) {
        return scheduleMapper.modifySchedule(schedule);
    }

	@Override
	public Map<String, Object> getStartTime(String stepName) {
		return scheduleMapper.getStartTime(stepName);
	}
}
