package com.spring.batch.job.service;
 
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Service("SampleJobService")
public class SampleJobServiceImpl implements SampleJobService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleJobServiceImpl.class);
    @Autowired
    private SqlSession sqlSession; 
    
    public SampleJobServiceImpl( ) { 
    }
    @Override
    public String getMessage() {
        HashMap<String, String> input = new HashMap<String, String>();
        input.put("code", "AIA");
        
        List<HashMap<String, String>> outputs 
            = sqlSession.selectList("batch.job.sampleDB.mapper.dbCheck.dbCheck", input);
        
        return outputs.toString();
    } 
}
