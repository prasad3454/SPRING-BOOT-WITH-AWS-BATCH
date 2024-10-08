package com.awsbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.awsbatch.model.Product;
import com.awsbatch.repository.ProductRepository;

import lombok.AllArgsConstructor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class CsvBatchConfig {
	
	private JobBuilderFactory jobBuilderFactory;
	private StepBuilderFactory stepBuilderFactory;
	private ProductRepository productRepository;
	
	@Bean
	public FlatFileItemReader<Product> customerReader() {
		FlatFileItemReader<Product> fileItemReader = new FlatFileItemReader<>();
		fileItemReader.setResource(new FileSystemResource("src/main/resources/projects.csv")); 
		fileItemReader.setName("csv-reader");
		fileItemReader.setLinesToSkip(1);
		fileItemReader.setLineMapper(lineMapper());
		return fileItemReader;
	}

	private LineMapper<Product> lineMapper() {
		DefaultLineMapper<Product> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setDelimiter(",");
		delimitedLineTokenizer.setStrict(false);
		delimitedLineTokenizer.setNames("productId","title","description","price","discount");
		
		BeanWrapperFieldSetMapper<Product> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(Product.class);
		
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
		
		return defaultLineMapper;
	}
	
	@Bean
	public CustomItemProcessor customItemProcessor() {
		return new CustomItemProcessor();
	}
	
	@Bean
	public RepositoryItemWriter<Product> customerWriter() {
		
		RepositoryItemWriter<Product> writer = new RepositoryItemWriter<Product>();
		writer.setRepository(productRepository);
		writer.setMethodName("save");
		
		return writer;
	}
	
	@Bean
	public Step step() {
		return stepBuilderFactory.get("step-1").<Product, Product>chunk(10)
				.reader(customerReader())
				.processor(customItemProcessor())
				.writer(customerWriter())
				.taskExecutor(taskExecutor())
				.build();
	}
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("customers-import")
				.flow(step())
				.end()
				.build();
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(10);
		return taskExecutor;
	}
}
