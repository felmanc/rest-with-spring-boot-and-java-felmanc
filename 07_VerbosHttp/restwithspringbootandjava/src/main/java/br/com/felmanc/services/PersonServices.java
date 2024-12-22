package br.com.felmanc.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import br.com.felmanc.model.Person;

// @Service: Objeto que será injetado em Runtime na aplicação
@Service
public class PersonServices {

  private final AtomicLong counter = new AtomicLong();
  private Logger logger = Logger.getLogger(PersonServices.class.getName());

  public List<Person> findAll() {
    logger.info("Find all people!");
    
    List<Person> persons = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      Person person = mockPerson(i);
      persons.add(person);
    }
    return persons;
  }

  public Person findById(String id) {
    logger.info("Find one person! id " + id);
    
    Person person = new Person();
    
    person.setId(counter.incrementAndGet());
    person.setFirstName("Felipe");
    person.setLastName("Schaden");
    person.setAddress("Ribeirão Preto");
    person.setGender("Male");
    
    return person;
  }
  
  public Person create(Person person) {
    logger.info("Creating one person!");
    
    return person;
  }


  public Person update(Person person) {
    logger.info("Creating one person!");
    
    return person;
  }
  
  public void delete(String id) {
    logger.info("Deleting one person!");
  }
  
  private Person mockPerson(int i) {
    
    Person person = new Person();
    person.setId(counter.incrementAndGet());
    person.setFirstName("Person Name " + i);
    person.setLastName("Last Name " + i);
    person.setAddress("Some address in Brasil " + i);
    person.setGender("Male");
    
    return person;
  }

}
