package com.spring.web.controller;

 
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spring.batch.job.service.SampleJobService;
import com.spring.quartz.QuartzService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	@Resource(name = "SampleJobService")
	private  SampleJobService sampleJobService; //DB 접속 
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/main.do", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date); 
		model.addAttribute("serverTime", formattedDate + sampleJobService.getMessage() );
		System.out.println("hello world");
		
		return "home";
	}
	
	@Autowired
	QuartzService quartzService;
	
	//gracefully shut down test
	@RequestMapping(value = "/shutdown.do", method = RequestMethod.GET)
	public void shutdown(Model model) throws SchedulerException, InterruptedException {
	   quartzService.shutdown();
	}
	
}
