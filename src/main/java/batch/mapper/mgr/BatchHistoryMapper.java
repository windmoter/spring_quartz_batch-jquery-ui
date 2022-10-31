package batch.mapper.mgr;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface BatchHistoryMapper {
    List<Map<String, Object>> retrieveBatchHistory(Map<String, Object> inputData);
    Map<String, Object> getBatchHistoryId();
    void updateBatchHistory(Map<String, Object> input);
    void createBatchHistory(Map<String, Object> input);
}
