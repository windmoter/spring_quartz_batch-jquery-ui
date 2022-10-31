drop table if exists batch_schedule;  
drop table if exists batch_job;  

CREATE TABLE batch_schedule (
  BATCH_SCHE_NO varchar(40) COLLATE utf8mb4_bin NOT NULL,
  BATCH_WORK_NO varchar(40) COLLATE utf8mb4_bin NOT NULL,
  BATCH_JOB_PARAM varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  EXEC_PRD_TP varchar(20) COLLATE utf8mb4_bin DEFAULT NULL,
  EXEC_YY varchar(4) COLLATE utf8mb4_bin DEFAULT NULL,
  EXEC_MM varchar(2) COLLATE utf8mb4_bin DEFAULT NULL,
  EXEC_DD varchar(2) COLLATE utf8mb4_bin DEFAULT NULL,
  EXEC_HI varchar(2) COLLATE utf8mb4_bin DEFAULT NULL,
  EXEC_MI varchar(2) COLLATE utf8mb4_bin DEFAULT NULL,
  EXEC_SS varchar(2) COLLATE utf8mb4_bin DEFAULT NULL,
  RMK varchar(1500) COLLATE utf8mb4_bin DEFAULT NULL,
  IN_DT datetime NOT NULL,
  IN_ID varchar(30) COLLATE utf8mb4_bin NOT NULL,
  UP_DT datetime NOT NULL,
  UP_ID varchar(30) COLLATE utf8mb4_bin NOT NULL,
  CRON_EXPRESSION varchar(200) COLLATE utf8mb4_bin NOT NULL,
  DAY_OF_WEEK varchar(30) COLLATE utf8mb4_bin DEFAULT NULL,
  DESCRIPTION text COLLATE utf8mb4_bin DEFAULT NULL COMMENT '내용',
  PRIMARY KEY (BATCH_SCHE_NO)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;



CREATE TABLE batch_job (
  BATCH_WORK_NO varchar(40) COLLATE utf8mb4_bin NOT NULL,
  BATCH_WORK_NM varchar(800) COLLATE utf8mb4_bin DEFAULT NULL,
  BATCH_PGM_PATH varchar(300) COLLATE utf8mb4_bin DEFAULT NULL,
  BATCH_PARAM varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
  USE_YN char(1) COLLATE utf8mb4_bin NOT NULL,
  RMK varchar(1500) COLLATE utf8mb4_bin DEFAULT NULL,
  IN_DT datetime NOT NULL,
  IN_ID varchar(30) COLLATE utf8mb4_bin NOT NULL,
  UP_DT datetime NOT NULL,
  UP_ID varchar(30) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (BATCH_WORK_NO)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


-- lgecokr_mgr_publ.mkt_batch_h definition

CREATE TABLE mkt_batch_h (
  akey_id int(11) NOT NULL AUTO_INCREMENT COMMENT '대체키',
  batch_history_id varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '배치히스토리ID',
  batch_user_id varchar(20) COLLATE utf8mb4_bin NOT NULL COMMENT '배치수행사용자ID',
  program_type_code varchar(10) COLLATE utf8mb4_bin NOT NULL COMMENT '프로그램타입',
  biz_sctn_code varchar(10) COLLATE utf8mb4_bin NOT NULL COMMENT 'BIZ SCTN CODE',
  batch_id varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '배치ID',
  cls_yyyymmdd varchar(8) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'CLS YYYYMMDD',
  batch_desc varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '배치설명',
  batch_start_date datetime NOT NULL COMMENT '배치시작일시',
  batch_end_date datetime DEFAULT NULL COMMENT '배치종료일시',
  success_flag varchar(1) COLLATE utf8mb4_bin NOT NULL DEFAULT 'N' COMMENT '성공여부',
  batch_data_count bigint(20) DEFAULT NULL COMMENT 'BATCH DATA COUNT',
  batch_result_content text COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'BATCH RESULT CONTENT',
  session_info_content varchar(400) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'SESSION INFO CONTENT',
  db_error_code varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'DB에러코드',
  db_error_desc varchar(400) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'DB에러설명',
  domn_sctn_code varchar(10) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'DOMN SCTN CODE',
  PRIMARY KEY (akey_id),
  KEY mkt_batch_h_x02 (batch_start_date,batch_desc) USING BTREE,
  KEY mkt_batch_h_x01 (batch_history_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1977376 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='배치수행 히스토리';
