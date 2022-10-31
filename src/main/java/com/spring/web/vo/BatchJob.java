package com.spring.web.vo;

public class BatchJob {
    private String batchWorkNo;
    private String batchWorkNm;
    private String batchParam;
    private String rmk;
    private String useYn;
    private String id;

    public String getBatchWorkNo() {
        return batchWorkNo;
    }

    public void setBatchWorkNo(String batchWorkNo) {
        this.batchWorkNo = batchWorkNo;
    }

    public String getBatchWorkNm() {
        return batchWorkNm;
    }

    public void setBatchWorkNm(String batchWorkNm) {
        this.batchWorkNm = batchWorkNm;
    }

    public String getBatchParam() {
        return batchParam;
    }

    public void setBatchParam(String batchParam) {
        this.batchParam = batchParam;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
