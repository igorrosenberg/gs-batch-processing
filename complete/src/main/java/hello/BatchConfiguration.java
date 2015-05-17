package hello;

import hello.files.DirectoryScaffold;

import javax.sql.DataSource;
import java.util.*;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public Job moveFilesinDirectory(JobBuilderFactory jobs, DirectoryScaffold directoryScaffold) {
      return directoryScaffold.moveFilesinDirectoryJob(jobs, "/tmp/in", "/tmp/out");
         }

    // @Bean
    public Job moveFilesinDirectory2(JobBuilderFactory jobs, DirectoryScaffold directoryScaffold) {
      return directoryScaffold.moveFilesinDirectoryJob(jobs, "/tmp/out", "/tmp/in");
         }

    /**
     * Could probably be more elegant
     */
    @Bean
    public DirectoryScaffold getDirectoryScaffold(){
        return new DirectoryScaffold(); 
      }       

}
