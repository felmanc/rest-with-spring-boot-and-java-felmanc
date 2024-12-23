package br.com.felmanc.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.felmanc.exceptions.ResourceNotFoundException;
import br.com.felmanc.model.Person;
import br.com.felmanc.repositories.PersonRepository;

// @Service: Objeto que será injetado em Runtime na aplicação
@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName());

	@Autowired
	PersonRepository repository;
	
	public List<Person> findAll() {
		logger.info("Find all people!");

		return repository.findAll();
	}

	public Person findById(Long id) {
		logger.info("Find one person! id " + id);

		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + id));
	}

	public Person create(Person person) {
		logger.info("Creating one person!");

		return repository.save(person);
	}

	public Person update(Person person) {
		logger.info("Creating one person!");

		//Person
		var entity = repository.findById(person.getId())
		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + person.getId()));
		
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		return repository.save(person);
	}

	public void delete(Long id) {
		logger.info("Deleting one person! " + id);
		
		var entity = repository.findById(id)
		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + id));

		repository.delete(entity);
	}
}
