package hello;

import hello.files.DirectoryScaffold;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
