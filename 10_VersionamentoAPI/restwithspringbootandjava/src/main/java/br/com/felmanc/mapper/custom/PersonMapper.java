package br.com.felmanc.mapper.custom;

import java.util.Date;

import org.springframework.stereotype.Service;

import br.com.felmanc.model.Person;
import br.com.felmanc.vo.v2.PersonVOV2;

@Service
public class PersonMapper {
	
	public PersonVOV2 convertEntityToVO(Person person) {
		PersonVOV2 vo = new PersonVOV2();
		
		vo.setAddress(person.getAddress());
		vo.setBirthday(new Date());
		vo.setFirstName(person.getFirstName());
		vo.setGender(person.getGender());
		vo.setId(person.getId());
		vo.setLastName(person.getLastName());
		
		return vo;
	}

	public Person convertVOToEntity(PersonVOV2 person) {
		Person entity = new Person();
		
		entity.setAddress(person.getAddress());
		//vo.setBirthday(new Date());
		entity.setFirstName(person.getFirstName());
		entity.setGender(person.getGender());
		entity.setId(person.getId());
		entity.setLastName(person.getLastName());
		
		return entity;
	}
}
