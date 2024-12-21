package br.com.felmanc.service;

import org.springframework.stereotype.Service;

import br.com.felmanc.enums.OperationEnum.Operation;
import br.com.felmanc.exceptions.UnsupportedMathOperationException;

@Service
public class MathOperation {
	//private static AtomicLong counter = new AtomicLong(); // para Mockar um id
	
	public Double performOperation(
			String numberOne, String numberTwo, Operation operation)
			throws Exception {
		
		Double num1 = NumberConverter.convertToDouble(numberOne);
		Double num2 = NumberConverter.convertToDouble(numberTwo);
		
		switch(operation) {
		case ADDITION:
			return SimpleMath.addition(num1, num2);
		case SUBTRACT:
			return SimpleMath.subtraction(num1, num2);
		case MULTIPLICATION:
			return SimpleMath.multiplication(num1, num2);
		case DIVISION:
			return SimpleMath.division(num1, num2);
		case AVERAGE:
			return SimpleMath.average(num1, num2);
			
		default:
			throw new UnsupportedMathOperationException("Unsupported operation");
		}
	}

	public Double performOperation(
			String number, Operation operation)
			throws Exception {
		
		Double num1 = NumberConverter.convertToDouble(number);
		
		switch (operation) {
		case SQUAREROOT:
			return SimpleMath.squareroot(num1);			
		default:
			throw new UnsupportedMathOperationException("Unsupported operation");
		}
	}

}
