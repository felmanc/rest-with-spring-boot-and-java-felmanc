package br.com.felmanc.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.felmanc.repositories.PersonRepository;

//@ResponseStatus:
//  Indica que será retornado um código de erro
//   Deve ser o mesmo erro tratado em handleBadRequestExceptions
@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidJwtAuthenticationException extends AuthenticationException {
	private static final long serialVersionUID = 1L;

	@Autowired
	PersonRepository repository;
	
	public InvalidJwtAuthenticationException(String ex) {
		super(ex);
	}

}
