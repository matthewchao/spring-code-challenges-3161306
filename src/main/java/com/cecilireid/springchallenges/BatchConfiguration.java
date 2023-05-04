package com.cecilireid.springchallenges;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    public CateringJobRepository repository;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<CateringJob> reader() {
        String[] names = new String[]{"id", "customerName", "phoneNumber", "email", "menu", "noOfGuests", "status"};
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_COMMA);
        tokenizer.setNames(names);

        DefaultLineMapper<CateringJob> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new CateringJobMapper());

        FlatFileItemReader<CateringJob> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new FileSystemResource("src/main/resources/upload.csv"));
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public RepositoryItemWriter<CateringJob> writer() {
        RepositoryItemWriter<CateringJob> writer = new RepositoryItemWriter<>();
        writer.setRepository(this.repository);
        return writer;
    }

    @Bean
    public Job uploadCateringJob(@Autowired Step step) {
        return jobBuilderFactory.get("uploadCateringJob")
                .start(step)
                .build();
    }

    @Bean
    public Step step(@Autowired ItemWriter<CateringJob> writer, @Autowired ItemReader<CateringJob> reader) {
        return stepBuilderFactory.get("step")
                .<CateringJob, CateringJob>chunk(10)
                .reader(reader)
                .writer(writer)
                .build();

    }
}
