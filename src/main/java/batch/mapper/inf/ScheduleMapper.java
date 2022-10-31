package batch.mapper.inf;
 
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.web.vo.BatchJob;
import com.spring.web.vo.Schedule;

@Mapper
public interface ScheduleMapper {
    public List<Schedule> getSchedules(Map<String, Object> ids);
    public int putSchedule(Schedule schedule);
    public int modifySchedule(Schedule schedule);
    public int deleteSchedule(String batchWorkNo);
    public int putJob(BatchJob batchjob);
    public BatchJob getJob(String batchJobNo);
    public Map<String, Object> getStartTime(String stepName);
}
