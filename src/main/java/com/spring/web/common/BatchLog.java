package com.spring.web.common;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.spring.web.history.BatchHistoryService;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class BatchLog {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchLog.class);

    private final BatchHistoryService batchHistoryService;

    private int batchDataCount = 0;
    private Map<String, Map<String, Object>> historyMap;

    public BatchLog(BatchHistoryService batchHistoryService) {
        this.batchHistoryService = batchHistoryService;
        historyMap = new HashMap<>();
    }

    public String init(Map<String, Object> inputData) {
        batchDataCount = 0;
        Map<String, Object> batchDataMap = new HashMap<>();

        batchDataMap.put("batchUserId", "system");
        batchDataMap.put("programTypeCode", "01");
        batchDataMap.put("bizSctnCode", "AZ");
        batchDataMap.put("batchId", inputData.get("batchId"));
        batchDataMap.put("clsYyyyMmDd", inputData.get("clsYyyyMmDd"));
        batchDataMap.put("batchDesc", inputData.get("batchId"));
        batchDataMap.put("successFlag", "N");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
        String currentTime = sdf.format(new Date());
        batchDataMap.put("batchStartDate", currentTime);
        batchDataMap.put("batchEndDate", "99991231235959");

        // 채번
        String batchHistoryId = getBatchHistoryId();  // 1
        batchDataMap.put("batchHistoryId", batchHistoryId);

        List<Map<String, Object>> data = new ArrayList<>();
        data.add(batchDataMap);
        batchHistoryService.cudBatchHistory(data);
        historyMap.put(batchHistoryId, batchDataMap);

        return batchHistoryId;
    }

    public void set(String batchHistoryId, String col, String val) {
        Map<String, Object> batchDataMap = historyMap.get(batchHistoryId);
        batchDataMap.put(col, val);
    }

    public String get(String batchHistoryId, String col) {
        Map<String, Object> batchDataMap = historyMap.get(batchHistoryId);
        return String.valueOf(batchDataMap.get(col));
    }

    public Map<String, String> getRunningJob() {
        Map<String, String> map = new HashMap<>();
        historyMap.forEach((id, val) -> {
            map.put(String.valueOf(val.get("batchId")), String.valueOf(val.get("batchId")));
        });
        return map;
    }

    public void print(String batchHistoryId) {
        try {
            batchDataCount++;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
            String currentTime = sdf.format(new Date());

            Map<String, Object> batchDataMap = historyMap.get(batchHistoryId);
            batchDataMap.put("batchEndDate", currentTime);

            LOGGER.debug("batchHistoryId     = {}",  batchHistoryId);
            LOGGER.debug("batchUserId        = {}",  batchDataMap.get("batchUserId"));
            LOGGER.debug("programTypeCode    = {}",  batchDataMap.get("programTypeCode"));
            LOGGER.debug("bizSctnCode        = {}",  batchDataMap.get("bizSctnCode"));
            LOGGER.debug("batchId            = {}",  batchDataMap.get("batchJobId"));
            LOGGER.debug("clsYyyyMmDd        = {}",  batchDataMap.get("clsYyyyMmDd"));
            LOGGER.debug("batchDesc          = {}",  batchDataMap.get("batchDesc"));
            LOGGER.debug("batchStartDate     = {}",  batchDataMap.get("batchStartDate"));
            LOGGER.debug("batchEndDate       = {}",  batchDataMap.get("batchEndDate"));
            LOGGER.debug("successFlag        = {}",  batchDataMap.get("successFlag"));
            LOGGER.debug("batchDataCount     = {}",  this.batchDataCount);
            LOGGER.debug("batchResultContent = {}",  batchDataMap.get("batchResultContent"));
            LOGGER.debug("sessionInfoContent = {}",  batchDataMap.get("sessionInfoContent"));
            LOGGER.debug("dbErrorCode        = {}",  batchDataMap.get("dbErrorCode"));
            LOGGER.debug("dbErrorDesc        = {}",  batchDataMap.get("dbErrorDesc"));

            List<Map<String, Object>> data = new ArrayList<>();
            data.add(batchDataMap);
            batchHistoryService.cudBatchHistory(data);
            historyMap.remove(batchHistoryId);
        } catch(Exception e) {
            LOGGER.error(this.getClass().getName() + ".print() => " + e.getMessage());
        }
    }

    public String getBatchHistoryId() {
        Map<String, Object> input = batchHistoryService.getBatchHistoryId();
        return String.valueOf(input.get("batchHistoryId"));
    }

    public void removeHistoryMapForStopJob(String batchHistoryId) {
        historyMap.remove(batchHistoryId);
    }

    public int getBatchDataCount() {
        return batchDataCount;
    }

    public void setBatchDataCount(int batchDataCount) {
        this.batchDataCount = batchDataCount;
    }
}
