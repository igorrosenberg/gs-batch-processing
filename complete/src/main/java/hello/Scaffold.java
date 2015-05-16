package hello;

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

public class Scaffold {

    public Job importUserJob(
      JobBuilderFactory jobs, int configurationData
         ) {
        System.out.println("JOB "  + configurationData);
        Step step = step(configurationData);
        return jobs.get("importUserJob" + configurationData)
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .build();
    }    

    protected ItemReader<Person> configuredReader(String fileName) {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
        reader.setResource(new ClassPathResource(fileName));
        reader.setLineMapper(new DefaultLineMapper<Person>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "firstName", "lastName" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
            }});
        }});
        return reader;
    }

    protected ItemProcessor<Person, Person> processor(int configurationData) {
        return new PersonItemProcessor(configurationData);
    }

    protected ItemWriter<Person> writer(final int configurationData) {
       return new ItemWriter<Person>(){
          public void write(java.util.List<? extends Person> items){
            System.out.println("Writer "+configurationData+" is receiving "+items.size()+" items");
            for (Person p: items) {
                System.out.println("    Writer received " + p);
            }
          
          }
       };
    }

    @Autowired
    StepBuilderFactory stepBuilderFactory;
    
    protected Step step(
            int configurationData
            ) {
        System.out.println("STEP-" +  configurationData);
        ItemReader<Person> reader = configuredReader("sample-data-"+configurationData+".csv");
        ItemProcessor<Person, Person> processor = processor(configurationData); 
        ItemWriter<Person> writer = writer(configurationData);
        return stepBuilderFactory.get("step" + configurationData)
                .<Person, Person> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}



