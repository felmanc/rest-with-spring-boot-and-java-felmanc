package br.com.felmanc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.felmanc.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{}
