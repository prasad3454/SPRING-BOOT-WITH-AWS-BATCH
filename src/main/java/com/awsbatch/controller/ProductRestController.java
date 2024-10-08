package com.awsbatch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired 
	private Job job;
	
	@GetMapping("/customer")
	public void loadCsvToDb() throws Exception {
		JobParameters jobParams = new JobParametersBuilder().addLong("Start-at",System.currentTimeMillis()).toJobParameters();
		jobLauncher.run(job,jobParams);
		System.out.println("Successfuly Inserted.");
	}
}