package br.com.felmanc.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.felmanc.controller.PersonController;
import br.com.felmanc.data.vo.v1.PersonVO;
import br.com.felmanc.exceptions.RequiredObjectIsNullException;
import br.com.felmanc.exceptions.ResourceNotFoundException;
import br.com.felmanc.mapper.DozerMapper;
import br.com.felmanc.model.Person;
import br.com.felmanc.repositories.PersonRepository;

// @Service: Objeto que será injetado em Runtime na aplicação
@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName());

//	@Autowired
	PersonRepository repository;
	
	// No lugar do @Autowired pode injetar dependencia pelo construtor: 
	public PersonServices(PersonRepository repository) {
		this.repository = repository;
	}

	public List<PersonVO> findAll() {
		logger.info("Find all people!");

		var persons = DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
		
		persons
			.stream()
			.forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		
		return persons;
	}

	public PersonVO findById(Long id) {
		logger.info("Find one person! id " + id);

		// Entidade: é o que o JPA/Hibernate sabe trabalhar
		// Vai ao banco de dados e busca o objeto por id
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + id));
		
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		
		return vo;
	}

	// Create: Recebe um PersonVO
	public PersonVO create(PersonVO personVO) {

		if(personVO == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one person!");

		// Converte um PersonVO para uma entidade do tipo Person
		var entity = DozerMapper.parseObject(personVO, Person.class);
		// Salva a entidade no banco de dados, pega o resultado e converte para um Person VO
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());

		return vo;
	}

	public PersonVO update(PersonVO personVO) {

		if(personVO == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one person!");

		// PersonVO
		var entity = repository.findById(personVO.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + personVO.getKey()));

		entity.setFirstName(personVO.getFirstName());
		entity.setLastName(personVO.getLastName());
		entity.setAddress(personVO.getAddress());
		entity.setGender(personVO.getGender());

		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());

		return vo;
	}

	public void delete(Long id) {
		logger.info("Deleting one person! " + id);

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + id));

		repository.delete(entity);
	}
}
