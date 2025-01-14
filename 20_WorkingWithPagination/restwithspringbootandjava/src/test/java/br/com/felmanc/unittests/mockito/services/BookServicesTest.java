package br.com.felmanc.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.felmanc.data.vo.v1.BookVO;
import br.com.felmanc.exceptions.RequiredObjectIsNullException;
import br.com.felmanc.model.Book;
import br.com.felmanc.repositories.BookRepository;
import br.com.felmanc.services.BookServices;
import br.com.felmanc.unittests.mapper.mocks.MockBook;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {
	
	MockBook input;
	
	@InjectMocks
	private BookServices service;
	
	@Mock
	BookRepository repository;
	
	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}
/*
	@Test
	void testFindAll() {
		List<Book> list = input.mockEntityList();
		
		when(repository.findAll()).thenReturn(list);
		
		var book = service.findAll();
		
		assertNotNull(book);
		assertEquals(14, book.size());
		
		var bookOne = book.get(1);
		
		assertNotNull(bookOne);
		assertNotNull(bookOne.getKey());
		assertNotNull(bookOne.getLinks());

		assertTrue(bookOne.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		
		assertEquals("Some Author1", bookOne.getAuthor());
		assertNotNull(bookOne.getLaunchDate());
		assertEquals(5.5D, bookOne.getPrice());
		assertEquals("Title's Book1", bookOne.getTitle());

		var bookFive = book.get(5);
		
		assertNotNull(bookFive);
		assertNotNull(bookFive.getKey());
		assertNotNull(bookFive.getLinks());

		assertTrue(bookFive.toString().contains("links: [</api/book/v1/5>;rel=\"self\"]"));
		
		assertEquals("Some Author5", bookFive.getAuthor());
		assertNotNull(bookFive.getLaunchDate());
		assertEquals(5.5D, bookFive.getPrice());
		assertEquals("Title's Book5", bookFive.getTitle());
		
		var bookEight = book.get(8);
		
		assertNotNull(bookEight);
		assertNotNull(bookEight.getKey());
		assertNotNull(bookEight.getLinks());

		assertTrue(bookEight.toString().contains("links: [</api/book/v1/8>;rel=\"self\"]"));
		
		assertEquals("Some Author8", bookEight.getAuthor());
		assertNotNull(bookEight.getLaunchDate());
		assertEquals(5.5D, bookEight.getPrice());
		assertEquals("Title's Book8", bookEight.getTitle());
	}
*/
	@Test
	void testFindById() {
		
		Book entity = input.mockEntity(2);
		
		//entity.setId(2L);
		
		when(repository.findById(2L)).thenReturn(Optional.of(entity));
		
		var result = service.findById(2L);
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		
		assertTrue(result.toString().contains("links: [</api/book/v1/2>;rel=\"self\"]"));
		
		assertEquals("Some Author2", result.getAuthor());
		assertNotNull(result.getLaunchDate());
		assertEquals(5.5D, result.getPrice());
		assertEquals("Title's Book2", result.getTitle());
	}
	
	@Test
	void testCreate() {
		
		Book persisted = input.mockEntity(2);
		//persisted.setId(2L);

		BookVO vo = input.mockVO(2); 
		//vo.setKey(2L);
		
		when(repository.save(any(Book.class))).thenReturn(persisted);
		
		var result = service.create(vo);
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		
		assertTrue(result.toString().contains("links: [</api/book/v1/2>;rel=\"self\"]"));
		
		assertEquals("Some Author2", result.getAuthor());
		assertNotNull(result.getLaunchDate());
		assertEquals(5.5D, result.getPrice());
		assertEquals("Title's Book2", result.getTitle());
	}

	@Test
	void testCreateWithNullPerson() {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void testUpdate() {
		
		Book entity = input.mockEntity(2);
		//entity.setId(2);
		
		Book persisted = input.mockEntity(2);
		//persisted.setId(2);

		BookVO vo = input.mockVO(2); 
		//vo.setId(2L);
		
		when(repository.findById(2L)).thenReturn(Optional.of(entity));
		when(repository.save(any(Book.class))).thenReturn(persisted);
		
		var result = service.update(vo);
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		
		assertTrue(result.toString().contains("links: [</api/book/v1/2>;rel=\"self\"]"));
		
		assertEquals("Some Author2", result.getAuthor());
		assertNotNull(result.getLaunchDate());
		assertEquals(5.5D, result.getPrice());
		assertEquals("Title's Book2", result.getTitle());
	}
	
	@Test
	void testUpdateWithNullPerson() {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void testDelete() {
		
		Book entity = input.mockEntity(1);
		
		entity.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		
		service.delete(1L);
	}
}
