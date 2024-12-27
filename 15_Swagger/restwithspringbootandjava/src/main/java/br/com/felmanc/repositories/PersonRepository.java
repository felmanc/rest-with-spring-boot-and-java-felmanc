package br.com.felmanc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;

import br.com.felmanc.model.Person;

//É a classe que acessa o Banco de dados

//@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {}
