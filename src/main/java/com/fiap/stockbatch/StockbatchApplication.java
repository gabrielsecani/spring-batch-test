//package com.fiap.stockbatch;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//
//import java.io.File;
//import java.nio.file.Paths;
//
//@SpringBootApplication
//@EnableBatchProcessing
//public class StockbatchApplication {
//
//	Logger logger = LoggerFactory.getLogger(StockbatchApplication.class);
//
//	public static void main(String[] args) {
//		SpringApplication.run(StockbatchApplication.class, args);
//	}
//
//	@Bean
//	public Tasklet tasklet(@Value("${file.path}") String filePath){
//		return ((contribution, chunkContext) -> {
//			File file = Paths.get(filePath).toFile();
//			if(file.delete()){
//				logger.warn("File deleted");
//			}else{
//				logger.error("Could not delete file");
//			}
//			return RepeatStatus.FINISHED;
//		});
//
//	}
//
//	@Bean
//	public Step step(StepBuilderFactory stepBuilderFactory,
//			  Tasklet tasklet){
//		return stepBuilderFactory
//				.get("Delete file step")
//				.allowStartIfComplete(true)
//				.tasklet(tasklet)
//				.build();
//	}
//
//	@Bean
//	public Job job(JobBuilderFactory jobBuilderFactory,
//			Step step){
//		return jobBuilderFactory.get("Delete file Job")
//				.start(step)
//				.build();
//	}
//}
