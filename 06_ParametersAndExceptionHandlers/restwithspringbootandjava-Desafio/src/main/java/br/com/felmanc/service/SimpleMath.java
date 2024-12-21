package br.com.felmanc.service;

import br.com.felmanc.exceptions.UnsupportedMathOperationException;

public class SimpleMath {

	public static Double addition(Double num1, Double num2) {
		return num1 + num2;
	}
	
	public static Double subtraction(Double num1, Double num2) {
		return num1 - num2;
	}
	
	public static Double multiplication(Double num1, Double num2) {
		return num1 * num2;
	}
	
	public static Double division(Double num1, Double num2) {
		if(num2 == 0) {
			throw new UnsupportedMathOperationException("Unsupported operation: Division by zero is not allowed");
		}
		return num1 / num2;

	}
	
	public static Double average(Double num1, Double num2) {
		return (num1 + num2) / 2;
	}
	
	public static Double squareroot(Double num) {
		if(num < 0) {
			throw new UnsupportedMathOperationException("Unsupported operation: Square of negative number is not allowed");
		}
			
		return Math.sqrt(num);
	}
	
}
