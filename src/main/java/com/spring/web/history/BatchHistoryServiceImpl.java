package com.spring.web.history;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import batch.mapper.mgr.BatchHistoryMapper;

import java.util.List;
import java.util.Map;

@Service("batchHistoryService")
public class BatchHistoryServiceImpl implements BatchHistoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchHistoryServiceImpl.class);

    private final BatchHistoryMapper batchHistoryMapper;

    public BatchHistoryServiceImpl(BatchHistoryMapper batchHistoryMapper) {
        this.batchHistoryMapper = batchHistoryMapper;
    }

    @Override
    public List<Map<String, Object>> retrieveBatchHistoryList(Map<String, Object> inputData) {
        return batchHistoryMapper.retrieveBatchHistory(inputData);
    }

    @Override
    public void cudBatchHistory(List<Map<String, Object>> inputData) {
        try {
            for(Map<String, Object> input : inputData) {
                input.put("queryBatchHistoryId", String.valueOf(input.get("batchHistoryId")));
                List<Map<String, Object>> batchHistory = batchHistoryMapper.retrieveBatchHistory(input);

                if(batchHistory != null && batchHistory.size() > 0) {
                    batchHistoryMapper.updateBatchHistory(input);
                } else {
                    input.put("batchHistoryId", String.valueOf(input.get("batchHistoryId")));
                    batchHistoryMapper.createBatchHistory(input);
                }
            }
        } catch(Exception e) {
            LOGGER.error(this.getClass().getName() + ".cudBatchHistory() => " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getBatchHistoryId() {
        try {
            return batchHistoryMapper.getBatchHistoryId();
        } catch(Exception e) {
            LOGGER.error(this.getClass().getName() + ".getBatchHistoryId() => " + e.getMessage());
            throw e;
        }
    }
}
