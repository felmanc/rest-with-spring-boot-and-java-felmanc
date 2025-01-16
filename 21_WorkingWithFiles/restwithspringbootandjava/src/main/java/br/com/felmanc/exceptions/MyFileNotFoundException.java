package br.com.felmanc.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.felmanc.repositories.PersonRepository;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyFileNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@Autowired
	PersonRepository repository;
	
	public MyFileNotFoundException(String ex) {
		super(ex);
	}

}
