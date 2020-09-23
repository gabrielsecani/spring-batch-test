package com.fiap.stockbatch;

import com.fiap.stockbatch.models.Galinha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;

@SpringBootApplication
@EnableBatchProcessing
public class StockChunkApplication {

	Logger logger = LoggerFactory.getLogger(StockChunkApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(StockChunkApplication.class, args);
	}

	@Bean
	public FlatFileItemReader<Galinha> fileReader(@Value("${file.chunk}") Resource resource) {
		return new FlatFileItemReaderBuilder<Galinha>()
				.name("read file")
				.resource(resource)
				.targetType(Galinha.class)
				.delimited().delimiter(";").names("nome", "cpf")
				.build();
	}

	@Bean
	public ItemProcessor<Galinha, Galinha> processor() {
		return (pessoa) -> {
			pessoa.setNome(pessoa.getNome().toUpperCase());
			pessoa.setCpf(pessoa.getCpf().replaceAll("\\.", "").replace("-", ""));
			return pessoa;
		};
	}
	@Bean
	public JdbcBatchItemWriter<Galinha> databaseWriter(DataSource datasource) {
		return new JdbcBatchItemWriterBuilder<Galinha>()
				.dataSource(datasource)
				.sql("insert into TB_PESSOA (NOME, CPF) values (:nome, :cpf)")
				.beanMapped()
				.build();
	}

	@Bean
	public Step step(StepBuilderFactory stepBuilderFactory,
					 ItemReader<Galinha> itemReader,
					 ItemProcessor<Galinha, Galinha> itemProcessor,
					 ItemWriter<Galinha> itemWriter){
		return stepBuilderFactory
				.get("Step chunk file -> jdbc")
				.<Galinha, Galinha>chunk(2)
				.reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.build();
	}

	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory,
			Step step){
		return jobBuilderFactory.get("Delete file Job")
				.start(step)
				.build();
	}
}
