package br.com.felmanc.controller;

import java.util.List;

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

import br.com.felmanc.data.vo.v1.PersonVO;
import br.com.felmanc.services.PersonServices;


//@RestController: retorna um objeto e
//  o objeto e os dados do objeto são escritos diretamente na resposta HTTP, como JSON ou XML
@RestController
@RequestMapping("/api/person/v1")
public class PersonController {

	//@Autowired
	private PersonServices service;
	
	// Injeção de dependência sem @Autowired
	public PersonController(PersonServices service) {
		this.service = service;
	}

	@GetMapping(
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
	public List<PersonVO> findAll() {
		return service.findAll();
	}

	@GetMapping(value = "/{id}",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
	public PersonVO findById(@PathVariable/*(value = "id")*/ Long id)/* throws Exception */ {
		return service.findById(id);
	}

	@PostMapping(
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
	public PersonVO create(@RequestBody PersonVO person)/* throws Exception */ {
		return service.create(person);
	}

	@PutMapping(
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_YAML_VALUE})
	public PersonVO update(@RequestBody PersonVO person)/* throws Exception */ {
		return service.update(person);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable/*(value = "id")*/ Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
