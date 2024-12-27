package br.com.felmanc.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.felmanc.repositories.PersonRepository;

//@ResponseStatus:
//  Indica que será retornado um código de erro
//   Deve ser o mesmo erro tratado em handleBadRequestExceptions
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredObjectIsNullException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@Autowired
	PersonRepository repository;
	
	public RequiredObjectIsNullException() {
		super("It is not allowed to persist a null object!");
	}
	
	public RequiredObjectIsNullException(String ex) {
		super(ex);
	}

}
