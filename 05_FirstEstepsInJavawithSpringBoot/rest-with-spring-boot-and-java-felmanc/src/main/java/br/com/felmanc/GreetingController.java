package br.com.felmanc;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// localhost:8080/greeting?name=Felipe
// o Localhost:8080 acessa a página raiz do Spring Boot
// dentro da página raiz, busca por controllers: @RestController
// depois procura por /greeting: @RequestMapping("/greeting")
//	@RequestMapping: mapeia uma requisição para um método
// @RequestParam: é um tipo de parâmetro opcional (query param/ query string)


//@Controller:
// Cria uma map do model e encontrar um view equivalente
//@ResponseBody:

//@RestController: retorna um objeto e o objeto e os dados do objeto são escritos diretamente na resposta HTTP, como JSON ou XML
@RestController
public class GreetingController {
	private static final String template = "Hello, %s!";
	private static AtomicLong counter = new AtomicLong(); // para Mockar um id
	
	@RequestMapping("/greeting")
	public Greeting greeting(
			@RequestParam(value = "name", defaultValue = "World")
			String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
}
