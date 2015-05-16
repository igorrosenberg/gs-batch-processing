package hello;

import javax.sql.DataSource;
import java.util.*;;

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

/*
* This List<Job> is not properly recognized as many @Beans.
*/
    @Bean
    public List<Job> importUserJobs(
      JobBuilderFactory jobs,
       Scaffold scaffold
         ) {
         List<Job> list = new LinkedList<Job>();
         for (int index=2 ; index <= 3 ; index++ ) {
           Job job =  scaffold.importUserJob( jobs, index);
            list.add(job);
         System.out.println("Nice list " + index);
         }
         return list;
         }

    @Bean
    public Job importUserJob2(JobBuilderFactory jobs, Scaffold scaffold) {
      return scaffold.importUserJob( jobs, 2);
         }

    @Bean
    public Job importUserJob3(JobBuilderFactory jobs, Scaffold scaffold) {
      return scaffold.importUserJob( jobs, 3);
         }

    /**
     * Could probably be more elegant
     */
    @Bean
    public Scaffold getScaffold(){
        return new Scaffold(); 
      }

        

}
