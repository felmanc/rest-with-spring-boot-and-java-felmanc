package br.com.felmanc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.felmanc.model.Person;
import br.com.felmanc.services.PersonServices;

//@RestController: retorna um objeto e
//  o objeto e os dados do objeto são escritos diretamente na resposta HTTP, como JSON ou XML
@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	private PersonServices service;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Person> findAll() {
		return service.findAll();
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Person findById(@PathVariable(/*value = "id"*/) Long id)/* throws Exception */ {
		return service.findById(id);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Person create(@RequestBody Person person)/* throws Exception */ {
		return service.create(person);
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Person update(@RequestBody Person person)/* throws Exception */ {
		return service.update(person);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
