package com.share.customerdata.config;

import com.share.customerdata.dao.CustomerRepo;
import com.share.customerdata.model.Customer;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;

import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class BatchConfig {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public FlatFileItemReader<Customer> customerReader() {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("src/main/resources/Book2.csv"));
        reader.setLinesToSkip(1);
        reader.setName("customer-csv-reader");
        reader.setLineMapper(lineMapper());

        return reader;
    }
    
    private LineMapper<Customer> lineMapper() {
        final DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        final DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);
        tokenizer.setNames("id","name","city","contact");

        final BeanWrapperFieldSetMapper<Customer> fieldWrapper = new BeanWrapperFieldSetMapper<>();
        fieldWrapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldWrapper);

        return lineMapper;
    }

    @Bean
    public CustomProcessor customerProcessor() {
        return new CustomProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> customerWriter() {
        final RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepo);
        writer.setMethodName("save");

        return writer;
    }

    public Step step() {

        return new StepBuilder("Step-1", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(customerReader())
                .processor(customerProcessor())
                .writer(customerWriter())
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("customer-import-job", jobRepository).start(step()).build();
    }
}
