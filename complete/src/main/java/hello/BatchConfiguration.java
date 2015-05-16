package hello;

import javax.sql.DataSource;

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

    protected ItemProcessor<Person, Person> processor(int index) {
        return new PersonItemProcessor(index);
    }

    protected ItemWriter<Person> writer(final int index) {
       return new ItemWriter<Person>(){
          public void write(java.util.List<? extends Person> items){
            System.out.println("Writer "+index+" is receiving "+items.size()+" items");
            for (Person p: items) {
                System.out.println("    Writer received " + p);
            }
          
          }
       };
    }

    @Bean
    public ItemWriter<Person> writer(DataSource dataSource) {
        System.out.println ("Configuring writer with " + dataSource);
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }

    // tag::jobstep[]
    @Bean
    public Job importUserJob1(
      JobBuilderFactory jobs,
      ItemWriter<Person> writer
         ) {
        System.out.println("JOB 1");
        Step s1 = step(1, writer);
        return jobs.get("importUserJob1")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public Job importUserJob2(
      JobBuilderFactory jobs
         ) {
        System.out.println("JOB 2");
        ItemWriter<Person> writer = writer(2);
        Step s1 = step(2, writer);
        return jobs.get("importUserJob2")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
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

    @Autowired
    DataSource dataSource;

    @Autowired
    StepBuilderFactory stepBuilderFactory;
    
    protected Step step(
            int index,
          ItemWriter<Person> writer
            ) {
        System.out.println("STEP-" +  index);
        ItemReader<Person> reader = configuredReader("sample-data-"+index+".csv");
        ItemProcessor<Person, Person> processor = processor(index); 
//        ItemWriter<Person> writer = writer(dataSource) ;
        return stepBuilderFactory.get("step" + index)
                .<Person, Person> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
