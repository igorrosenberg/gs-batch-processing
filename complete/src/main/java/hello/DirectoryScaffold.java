package hello;

import javax.sql.DataSource;
import java.util.*;
import java.io.*;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;

public class DirectoryScaffold {

    private static Log logger = LogFactory.getLog("DirectoryScaffold");
     
    public Job moveFilesinDirectoryJob(
      JobBuilderFactory jobs, 
//      File dirIn, File dirOut
        String dirInPath, String dirOutPath
         ) {
       int configurationData = 1 ; 
       logger.info("preparing Job to move from "+ dirInPath + " to " + dirOutPath);
       File dirIn = new File(dirInPath); 
       File dirOut = new File(dirOutPath); 
       Step step1 = step1(dirIn);
//       Step step2 = step2(dirIn,dirOut);
        return jobs.get("importUserJob" + configurationData)
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                //.flow(step2)
                .end()
                .build();
    }    

    @Autowired
    StepBuilderFactory stepBuilderFactory;
    /*
    protected Step step2(
            File dirIn, File dirOut
            ) {
            }
*/
    protected Step step1(
            File dirIn
            ) {
        System.out.println("step1 - read files from " +  dirIn);
        ItemReader<File> reader = new FilesInDirectoryItemReader(dirIn);
        ItemProcessor<File, List<String>> processor = new FileItemProcessor(); 
        ItemWriter<List<String>> writer = writer();
        return stepBuilderFactory.get("step1")
                .<File, List<String>> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    protected ItemWriter<List<String>> writer() {
       return new ItemWriter<List<String>>(){
          public void write(java.util.List<? extends List<String>> items){
            System.out.println("Writer is receiving "+items.size()+" items");
            for (List<String> ls: items) {
              for (String s: ls) {
                  System.out.println("    Writer received " + s);
              }
            }
          
          }
       };
    }


}



