package br.com.felmanc.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.felmanc.exceptions.ResourceNotFoundException;
import br.com.felmanc.mapper.DozerMapper;
import br.com.felmanc.mapper.custom.PersonMapper;
import br.com.felmanc.model.Person;
import br.com.felmanc.repositories.PersonRepository;
import br.com.felmanc.vo.v1.PersonVO;
import br.com.felmanc.vo.v2.PersonVOV2;

// @Service: Objeto que será injetado em Runtime na aplicação
@Service
public class PersonServices {

	private Logger logger = Logger.getLogger(PersonServices.class.getName());

	@Autowired
	PersonRepository repository;
	
	@Autowired
	PersonMapper mapper;

	public List<PersonVO> findAll() {
		logger.info("Find all people!");

		return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
	}

	public PersonVO findById(Long id) {
		logger.info("Find one person! id " + id);

		// Entidade: é o que o JPA/Hibernate sabe trabalhar
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + id));
		
		DozerMapper.parseObject(entity, PersonVO.class);
		
		return null;
	}

	// Create: Recebe um PersonVO
	public PersonVO create(PersonVO personVO) {
		logger.info("Creating one person!");

		// Converte um PersonVO para uma entidade do tipo Person
		var entity = DozerMapper.parseObject(personVO, Person.class);
		// Salva a entidade no banco de dados, pega o resultado e converte para um Person VO
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		
		return vo;
	}

	// CreateV2: Recebe um PersonVO
	public PersonVOV2 createV2(PersonVOV2 personVO) {
		logger.info("Creating one person with V2!");
		
		// Converte um PersonVO para uma entidade do tipo Person
		var entity = mapper.convertVOToEntity(personVO);
		// Salva a entidade no banco de dados, pega o resultado e converte para um Person VO
		var vo = mapper.convertEntityToVO(repository.save(entity));
		
		return vo;
	}
	
	public PersonVO update(PersonVO personVO) {
		logger.info("Creating one person!");

		// PersonVO
		var entity = repository.findById(personVO.getId())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + personVO.getId()));

		entity.setFirstName(personVO.getFirstName());
		entity.setLastName(personVO.getLastName());
		entity.setAddress(personVO.getAddress());
		entity.setGender(personVO.getGender());

		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		
		return vo;
	}

	public void delete(Long id) {
		logger.info("Deleting one person! " + id);

		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + id));

		repository.delete(entity);
	}
}
