package br.com.felmanc.services;

import java.util.List;
import java.util.logging.Logger;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.felmanc.data.vo.v1.BookVO;
import br.com.felmanc.exceptions.RequiredObjectIsNullException;
import br.com.felmanc.exceptions.ResourceNotFoundException;
import br.com.felmanc.mapper.DozerMapper;
import br.com.felmanc.model.Book;
import br.com.felmanc.repositories.BookRepository;

//TODO: Implementar o HATEOAS!
@Service
public class BookServices {
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	
	BookRepository repository;

	public BookServices(BookRepository repository) {
		this.repository = repository;
	}
	
	
	public List<BookVO> findAll() {
		logger.info("Find all books!");
		
		var books = DozerMapper.parseListObjects(repository.findAll(), BookVO.class);
		
		return books;
	}
	
	public BookVO findById(Long id) {
		logger.info("Find one book! id " + id);
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + id));
		
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		
		return vo;
	}
	
	public BookVO create(BookVO bookVO) {
		
		if(bookVO == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one book!");
		
		var entity = DozerMapper.parseObject(bookVO, Book.class);
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		
		return vo;
	}
	
	public BookVO update(BookVO bookVO) {
		
		if(bookVO == null) throw new RequiredObjectIsNullException();
		
		var entity = repository.findById(bookVO.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + bookVO.getKey()));
				
		entity.setAuthor(bookVO.getAuthor());
		entity.setLaunchDate(bookVO.getLaunchDate());
		entity.setPrice(bookVO.getPrice());
		entity.setPrice(bookVO.getPrice());
		entity.setTitle(bookVO.getTitle());

		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one book! " + id);
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + id));
		
		repository.delete(entity);
	}
	
}
