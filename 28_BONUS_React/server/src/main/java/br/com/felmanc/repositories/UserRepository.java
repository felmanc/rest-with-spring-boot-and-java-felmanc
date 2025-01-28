package br.com.felmanc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.felmanc.model.User;

//É a classe que acessa o Banco de dados

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	//@Query: JPQL: Query efetuada no objeto, não direto no banco
	// findByUserName não é necessário desenvolver, mas pode ser personalizado
	@Query("SELECT u FROM User u WHERE u.userName =:userName")
	User findByUserName(@Param("userName") String userName);
	
}
