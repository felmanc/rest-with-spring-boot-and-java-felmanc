package br.com.felmanc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import br.com.felmanc.model.Person;

//É a classe que acessa o Banco de dados

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
	
	// @Modifying: Para garantir os princípios ACID (Atomicidade, conscistência, isolação e durabilidade)
	//		Necessário por efetuar alterações no Banco de Dados e não é o Spring Data que está manipulando
	@Modifying
	@Query("UPDATE Person p SET p.enabled = false WHERE p.id =:id")
	void disablePerson(@Param("id") Long id);
}
