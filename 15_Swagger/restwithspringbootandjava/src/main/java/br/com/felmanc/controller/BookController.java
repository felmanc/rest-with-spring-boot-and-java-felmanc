package br.com.felmanc.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.felmanc.data.vo.v1.BookVO;
import br.com.felmanc.services.BookServices;


@RestController
@RequestMapping("/api/book/v1")
public class BookController {

	private BookServices service;

	public BookController(BookServices service) {
		this.service = service;
	}
	
	@GetMapping
	public List<BookVO> findAll() {
		return service.findAll();
	}
	
	@GetMapping(value = "/{id}")
	public BookVO findById(@PathVariable Long id) {
		return service.findById(id);
	}
	
	@PostMapping
	public BookVO create(@RequestBody BookVO vo) {
		return service.create(vo);
	}
	
	@PutMapping
	public BookVO update(@RequestBody BookVO vo) {
		return service.update(vo);
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}
}
