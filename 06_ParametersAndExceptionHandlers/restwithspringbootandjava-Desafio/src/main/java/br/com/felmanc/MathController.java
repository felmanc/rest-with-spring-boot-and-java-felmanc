package br.com.felmanc;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.felmanc.exceptions.UnsupportedMathOperationException;

//@RestController: retorna um objeto e
//	o objeto e os dados do objeto são escritos diretamente na resposta HTTP, como JSON ou XML
@RestController
public class MathController {
	private static AtomicLong counter = new AtomicLong(); // para Mockar um id

	//@RequestMapping(value = "/sum/{numberOne}/{numberTwo}",method=RequestMethod.GET)
	@GetMapping(value = "/sum/{numberOne}/{numberTwo}")
	public Double sum(
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception{
		return performOperation(numberOne, numberTwo, Operation.ADDITION);
	}

	@GetMapping(value = "/sub/{numberOne}/{numberTwo}")
	public Double sub(
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception{
		return performOperation(numberOne, numberTwo, Operation.SUBTRACT);
	}

	@GetMapping(value = "/mul/{numberOne}/{numberTwo}")
	public Double mul(
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception {
		return performOperation(numberOne, numberTwo, Operation.MULTIPLICATION);
	}

	@GetMapping(value = "/div/{numberOne}/{numberTwo}")
	public Double div(
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception {
		return performOperation(numberOne, numberTwo, Operation.DIVISION);
	}

	@GetMapping(value = "/avg/{numberOne}/{numberTwo}")
	public Double avg(
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception {
		return performOperation(numberOne, numberTwo, Operation.AVERAGE);
	}

	@GetMapping(value = "/sqr/{number}")
	public Double sqr(
			@PathVariable(value = "number") String number
			) throws Exception {
		return performOperation(number, Operation.SQUAREROOT);
	}

	private Double performOperation(
			String numberOne, String numberTwo, Operation operation)
			throws Exception {
		
		Double num1 = convertToDouble(numberOne);
		Double num2 = convertToDouble(numberTwo);
		
		switch(operation) {
		case ADDITION:
			return num1 + num2;
		case SUBTRACT:
			return num1 - num2;
		case MULTIPLICATION:
			return num1 * num2;
		case DIVISION:
			if(num2 == 0) {
				throw new UnsupportedMathOperationException("Unsupported operation: Division by zero is not allowed");
			}
			return num1 / num2;
		case AVERAGE:
			return (num1 + num2) / 2;
			
		default:
			throw new UnsupportedMathOperationException("Unsupported operation");
		}
	}

	private Double performOperation(
			String number, Operation operation)
			throws Exception {
		
		Double num1 = convertToDouble(number);
		
		switch (operation) {
		case SQUAREROOT:
			if(num1 < 0) {
				throw new UnsupportedMathOperationException("Unsupported operation: Square of negative number is not allowed");
			}
				
			return Math.sqrt(num1);
		default:
			throw new UnsupportedMathOperationException("Unsupported operation");
		}
	}
	
	private Double convertToDouble(String strNumber) throws Exception {
		if(!isNumeric(strNumber)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}		
		
		if(strNumber == null) return 0D;
		// BR 10,25 / US 10.25
		String number = strNumber.replaceAll(",", ".");
		if(isNumeric(number)) return Double.parseDouble(number);
		return 0D;
	}

	private boolean isNumeric(String strNumber) {
		if(strNumber == null) return false;
		String number = strNumber.replaceAll(",", ".");
				
		return number.matches("[-+]?[0-9]*\\.?[0-9]+");
	}
	
	private enum Operation {
		ADDITION, SUBTRACT, MULTIPLICATION, DIVISION, AVERAGE, SQUAREROOT
	}
}
