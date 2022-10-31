package com.spring.web.history;

import java.util.List;
import java.util.Map;

public interface BatchHistoryService {
    public List<Map<String, Object>> retrieveBatchHistoryList(Map<String, Object> input);
    public void cudBatchHistory(List<Map<String, Object>> inputData);
    public Map<String, Object> getBatchHistoryId();
}
