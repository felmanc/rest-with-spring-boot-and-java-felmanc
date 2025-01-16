package br.com.felmanc.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.felmanc.repositories.PersonRepository;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@Autowired
	PersonRepository repository;
	
	public FileStorageException(String ex) {
		super(ex);
	}

	public FileStorageException(String ex, Throwable cause) {
		super(ex, cause);
	}
	
}
