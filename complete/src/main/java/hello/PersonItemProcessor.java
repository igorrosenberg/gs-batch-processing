package hello;

import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private final int tag ; 

    public PersonItemProcessor(int index){
      super();
      tag = index;
    }
    @Override
    public Person process(final Person person) throws Exception {
        final String firstName = person.getFirstName().toUpperCase();
        final String lastName = person.getLastName().toUpperCase();

        final Person transformedPerson = new Person(firstName, lastName +' ' + '(' +tag+')');

        System.out.println("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }

}
