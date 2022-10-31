 
package com.spring.web.vo;

public class Schedule {
    private String batchScheNo;
    private String batchWorkNo;
    private String cronExpression;
    private String batchJobParam;
    private String execPrdTp;
    private String execYy;
    private String execMm;
    private String execDd;
    private String execHi;
    private String execMi;
    private String execSs;
    private String dayOfWeek;
    private String rmk;
    private String pgmPath;
    private String pgmParam;
    private String id;
    private String upDt;
    private String description; // BTOCSITE-7195

    public String getBatchScheNo() {
        return batchScheNo;
    }

    public void setBatchScheNo(String batchScheNo) {
        this.batchScheNo = batchScheNo;
    }

    public String getBatchWorkNo() {
        return batchWorkNo;
    }

    public void setBatchWorkNo(String batchWorkNo) {
        this.batchWorkNo = batchWorkNo;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getBatchJobParam() {
        return batchJobParam;
    }

    public void setBatchJobParam(String batchJobParam) {
        this.batchJobParam = batchJobParam;
    }

    public String getExecPrdTp() {
        return execPrdTp;
    }

    public void setExecPrdTp(String execPrdTp) {
        this.execPrdTp = execPrdTp;
    }

    public String getExecYy() {
        return execYy;
    }

    public void setExecYy(String execYy) {
        this.execYy = execYy;
    }

    public String getExecMm() {
        return execMm;
    }

    public void setExecMm(String execMm) {
        this.execMm = execMm;
    }

    public String getExecDd() {
        return execDd;
    }

    public void setExecDd(String execDd) {
        this.execDd = execDd;
    }

    public String getExecHi() {
        return execHi;
    }

    public void setExecHi(String execHi) {
        this.execHi = execHi;
    }

    public String getExecMi() {
        return execMi;
    }

    public void setExecMi(String execMi) {
        this.execMi = execMi;
    }

    public String getExecSs() {
        return execSs;
    }

    public void setExecSs(String execSs) {
        this.execSs = execSs;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    public String getPgmPath() {
        return pgmPath;
    }

    public void setPgmPath(String pgmPath) {
        this.pgmPath = pgmPath;
    }

    public String getPgmParam() {
        return pgmParam;
    }

    public void setPgmParam(String pgmParam) {
        this.pgmParam = pgmParam;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpDt() {
        return upDt;
    }

    public void setUpDt(String upDt) {
        this.upDt = upDt;
    }
    // BTOCSITE-7195 start
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	// BTOCSITE-7195 end
}
