package br.com.felmanc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;

import br.com.felmanc.model.Person;

//@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {}
