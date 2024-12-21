package br.com.felmanc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//@ResponseStatus:
//	Indica que será retornado um código de erro
// 	Deve ser o mesmo erro tratado em handleBadRequestExceptions
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedMathOperationException extends RuntimeException {

	
	public UnsupportedMathOperationException(String ex) {
		super(ex);
	}

	private static final long serialVersionUID = 1L;
}
