package com.span.bulkuploadpipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BulkuploadpipelineApplication {

	public static void main(String[] args) {
		SpringApplication.run(BulkuploadpipelineApplication.class, args);
	}

}
