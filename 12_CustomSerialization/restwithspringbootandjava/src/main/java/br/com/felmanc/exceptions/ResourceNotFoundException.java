package br.com.felmanc.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.felmanc.repositories.PersonRepository;

//@ResponseStatus:
//  Indica que será retornado um código de erro
//   Deve ser o mesmo erro tratado em handleBadRequestExceptions
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@Autowired
	PersonRepository repository;
	
	public ResourceNotFoundException(String ex) {
		super(ex);
	}

}
