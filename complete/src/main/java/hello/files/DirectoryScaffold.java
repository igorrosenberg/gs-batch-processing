package hello.files;

import hello.files.tasks.*;

import javax.sql.DataSource;
import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.springframework.batch.core.*;
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
     
    static int counter = 0; 
    
    public Job moveFilesinDirectoryJob(
      JobBuilderFactory jobs, 
        String dirInPath, String dirOutPath
         ) {
       logger.info("preparing Job to move from "+ dirInPath + " to " + dirOutPath);
       File dirIn = new File(dirInPath); 
       File dirOut = new File(dirOutPath); 
       Step step = step(dirIn,dirOut);
       JobParametersValidator jobParametersValidator = validator(dirIn, dirOut);
        return jobs.get("moveFilesinDirectoryJob-" + (counter++))
                .incrementer(new RunIdIncrementer())
                .validator(jobParametersValidator)
                .flow(step)
                .end()
                .build();
    }    
    
    protected JobParametersValidator validator(final File dirIn, final File dirOut) {
        return new JobParametersValidator() {
          public void validate(JobParameters parameters) throws JobParametersInvalidException {
            Map<String, JobParameter> params = parameters.getParameters();
            for (Map.Entry<String, JobParameter> entry : params.entrySet()) {
                System.out.println("  Job param: " + entry.getKey() + " >> " + entry.getValue());
            }
            System.out.println("  Job param dirIn: "  + dirIn);
            System.out.println("  Job param dirOut: " + dirOut);
            if (!dirIn.exists())
              throw new JobParametersInvalidException("In directory "+dirIn+" not found");
            if (!dirIn.isDirectory())
              throw new JobParametersInvalidException("In directory "+dirIn+" is not a directory");
            if (!dirOut.exists())
              throw new JobParametersInvalidException("Out directory "+dirOut+" not found");
            if (!dirOut.isDirectory())
              throw new JobParametersInvalidException("Out directory "+dirOut+" is not a directory");

          }
        };
    }
    

    @Autowired
    StepBuilderFactory stepBuilderFactory;
    
    protected Step step(
            File dirIn, File dirOut
            ) {
        System.out.println("step - read files from " +  dirIn + ", process, then move to " + dirOut);
        ItemReader<File> reader = new FilesInDirectoryItemReader(dirIn);
        ItemProcessor<File, File> processor = new FileItemProcessor(); 
        ItemProcessor<File, File> processor2 = new FileItemProcessor(); 
        ItemWriter<File> writer = new FileItemWriter(dirOut); 
        return stepBuilderFactory.get("directoryScaffoldStep-" + counter)
                .<File, File> chunk(10)
                .reader(reader)
                .processor(processor)
                .processor(processor2)
                .writer(writer)
                .build();
    }


}



