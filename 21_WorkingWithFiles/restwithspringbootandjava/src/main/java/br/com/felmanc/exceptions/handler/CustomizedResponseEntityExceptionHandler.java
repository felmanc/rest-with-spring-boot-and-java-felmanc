package br.com.felmanc.exceptions.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.felmanc.exceptions.ExceptionResponse;
import br.com.felmanc.exceptions.InvalidJwtAuthenticationException;
import br.com.felmanc.exceptions.RequiredObjectIsNullException;
import br.com.felmanc.exceptions.ResourceNotFoundException;

// @ControllerAdvice: Tramento global
//  Para concentrar tratamento dos controllers
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  //Exception.class: exceção mais genérica do Java
  @ExceptionHandler(Exception.class)
  public final ResponseEntity<ExceptionResponse> handleAllExceptions(
        Exception ex, WebRequest request){

      ExceptionResponse exceptionResponse = new ExceptionResponse(
          new Date(),
          ex.getMessage(),
          request.getDescription(false));
      return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(
      Exception ex, WebRequest request){
    
    ExceptionResponse exceptionResponse = new ExceptionResponse(
        new Date(),
        ex.getMessage(),
        request.getDescription(false));
    return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
  }
  
  @ExceptionHandler(RequiredObjectIsNullException.class)
  public final ResponseEntity<ExceptionResponse> handleBadRequestExceptions(
		  Exception ex, WebRequest request){
	  
	  ExceptionResponse exceptionResponse = new ExceptionResponse(
			  new Date(),
			  ex.getMessage(),
			  request.getDescription(false));
	  return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(InvalidJwtAuthenticationException.class)
  public final ResponseEntity<ExceptionResponse> handleInvalidJwtAuthenticationExceptions(
		  Exception ex, WebRequest request){
	  
	  ExceptionResponse exceptionResponse = new ExceptionResponse(
			  new Date(),
			  ex.getMessage(),
			  request.getDescription(false));
	  return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
  }
}
