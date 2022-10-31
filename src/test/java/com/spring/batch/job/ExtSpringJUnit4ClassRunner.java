package com.spring.batch.job;
 
import org.junit.runners.model.InitializationError;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiTemplate;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
 
public class ExtSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {
 
    public ExtSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        // TODO Auto-generated constructor stub
        try {
            bindJndi();
        } catch (Exception e) {
            
        }
    }
    
    private void bindJndi() throws Exception {
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.activate();
        
        JndiTemplate jt = new JndiTemplate();
        
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://~:3306/~?useSSL=false&amp;serverTimezone=UTC");
        ds.setUsername("~");
        ds.setPassword("~");
        
        jt.bind("java:/jdbc/testJNDI", ds);
    }
}