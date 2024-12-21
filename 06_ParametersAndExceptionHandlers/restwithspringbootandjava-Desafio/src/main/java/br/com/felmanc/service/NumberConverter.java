package br.com.felmanc.service;

import br.com.felmanc.exceptions.UnsupportedMathOperationException;

public class NumberConverter {

	public static Double convertToDouble(String strNumber) throws Exception {
		if(!isNumeric(strNumber)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}		
		
		if(strNumber == null) return 0D;
		// BR 10,25 / US 10.25
		String number = strNumber.replaceAll(",", ".");
		if(isNumeric(number)) return Double.parseDouble(number);
		return 0D;
	}

	public static boolean isNumeric(String strNumber) {
		if(strNumber == null) return false;
		String number = strNumber.replaceAll(",", ".");
				
		return number.matches("[-+]?[0-9]*\\.?[0-9]+");
	}

}
