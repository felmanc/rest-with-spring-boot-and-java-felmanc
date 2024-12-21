package br.com.felmanc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.felmanc.enums.OperationEnum.Operation;
import br.com.felmanc.service.MathOperation;

//@RestController: retorna um objeto e
//	o objeto e os dados do objeto são escritos diretamente na resposta HTTP, como JSON ou XML
@RestController
public class MathController {
	
	private final MathOperation mathOperation;
	
	//@Autowired
	public MathController(MathOperation mathService) {
		this.mathOperation = mathService;
	}
	
	//@RequestMapping(value = "/sum/{numberOne}/{numberTwo}",method=RequestMethod.GET)
	@GetMapping(value = "/sum/{numberOne}/{numberTwo}")
	public Double sum(
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception{
		return mathOperation.performOperation(numberOne, numberTwo, Operation.ADDITION);
	}

	@GetMapping(value = "/sub/{numberOne}/{numberTwo}")
	public Double sub(
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception{
		return mathOperation.performOperation(numberOne, numberTwo, Operation.SUBTRACT);
	}

	@GetMapping(value = "/mul/{numberOne}/{numberTwo}")
	public Double mul(
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception {
		return mathOperation.performOperation(numberOne, numberTwo, Operation.MULTIPLICATION);
	}

	@GetMapping(value = "/div/{numberOne}/{numberTwo}")
	public Double div(
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception {
		return mathOperation.performOperation(numberOne, numberTwo, Operation.DIVISION);
	}

	@GetMapping(value = "/avg/{numberOne}/{numberTwo}")
	public Double avg(
			@PathVariable(value = "numberOne") String numberOne,
			@PathVariable(value = "numberTwo") String numberTwo
			) throws Exception {
		return mathOperation.performOperation(numberOne, numberTwo, Operation.AVERAGE);
	}

	@GetMapping(value = "/sqr/{number}")
	public Double sqr(
			@PathVariable(value = "number") String number
			) throws Exception {
		return mathOperation.performOperation(number, Operation.SQUAREROOT);
	}
}
