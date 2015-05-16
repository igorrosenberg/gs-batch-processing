package hello;

import javax.sql.DataSource;
import java.util.*;
import java.io.*;
import java.nio.file.*;

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
        String dirInPath, String dirOutPath
         ) {
       logger.info("preparing Job to move from "+ dirInPath + " to " + dirOutPath);
       File dirIn = new File(dirInPath); 
       File dirOut = new File(dirOutPath); 
       Step step1 = step1(dirIn,dirOut);
        return jobs.get("moveFilesinDirectoryJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }    

    @Autowired
    StepBuilderFactory stepBuilderFactory;
    
    protected Step step1(
            File dirIn, File dirOut
            ) {
        System.out.println("step1 - read files from " +  dirIn);
        ItemReader<File> reader = new FilesInDirectoryItemReader(dirIn);
        ItemProcessor<File, File> processor = new FileItemProcessor(); 
        ItemWriter<File> writer = writer(dirOut);
        return stepBuilderFactory.get("step1")
                .<File, File> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    protected ItemWriter<File> writer(final File dirOut) {
       return new ItemWriter<File>() {
          public void write(List<? extends File> files) throws IOException {
            System.out.println("Writer is receiving " + files.size());
            for (File file : files) {
              Path dest = new File (dirOut, file.getName()).toPath();
              Path destinationPath = Files.move(file.toPath(), dest);
              System.out.println("    Moved file to " + destinationPath);
            }
          }
       };
    }


}



