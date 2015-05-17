package hello.files;

import java.io.File;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.apache.commons.logging.Log;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.apache.commons.logging.LogFactory;

import hello.files.tasks.*;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;

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
        ItemWriter<File> writer = new FileItemWriter(dirOut); 
        return stepBuilderFactory.get("directoryScaffoldStep-" + counter)
                .<File, File> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


}



