package br.com.felmanc.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.felmanc.controllers.BookController;
import br.com.felmanc.data.vo.v1.BookVO;
import br.com.felmanc.exceptions.RequiredObjectIsNullException;
import br.com.felmanc.exceptions.ResourceNotFoundException;
import br.com.felmanc.mapper.DozerMapper;
import br.com.felmanc.model.Book;
import br.com.felmanc.repositories.BookRepository;


@Service
public class BookServices {
	private Logger logger = Logger.getLogger(BookServices.class.getName());

	//@Autowired
	BookRepository repository;

	//@Autowired
	PagedResourcesAssembler<BookVO> assembler;
	
	public BookServices(BookRepository repository, PagedResourcesAssembler<BookVO> assembler) {
		this.repository = repository;
		this.assembler = assembler;
	}
	
	public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {
		logger.info("Find all books!");
		
		var booksPage = repository.findAll(pageable);
		
		var bookVOsPage = booksPage.map(p -> DozerMapper.parseObject(p, BookVO.class));
		
		bookVOsPage.map(
				p -> p.add(linkTo(methodOn(BookController.class)
						.findById(p.getKey())).withSelfRel()));
		
		Link link = linkTo(methodOn(BookController.class)
				.findAll(pageable.getPageNumber(),
						pageable.getPageSize(),
						"asc")).withSelfRel();
		return assembler.toModel(bookVOsPage, link);
	}

	public BookVO findById(Long id) {
		logger.info("Find one book! id " + id);
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + id));
		
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		
		return vo;
	}

	public BookVO create(BookVO bookVO) {
		
		if(bookVO == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one book!");
		
		var entity = DozerMapper.parseObject(bookVO, Book.class);
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());

		return vo;
	}
	
	public BookVO update(BookVO bookVO) {
		
		if(bookVO == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one book!");

		// BookVO
		var entity = repository.findById(bookVO.getKey())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + bookVO.getKey()));
				
		entity.setAuthor(bookVO.getAuthor());
		entity.setLaunchDate(bookVO.getLaunchDate());
		entity.setPrice(bookVO.getPrice());
		entity.setPrice(bookVO.getPrice());
		entity.setTitle(bookVO.getTitle());

		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());

		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one book! " + id);
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID: " + id));
		
		repository.delete(entity);
	}
	
}
