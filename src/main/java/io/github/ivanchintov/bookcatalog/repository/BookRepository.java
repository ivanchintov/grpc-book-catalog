package io.github.ivanchintov.bookcatalog.repository;

import io.github.ivanchintov.bookcatalog.proto.Book;

import java.util.Iterator;
import java.util.Optional;

public interface BookRepository {

    Optional<Book> findById(long id);

    Optional<Book> findByIsbn(String isbn);

    Iterator<Book> findAll();

    Book save(Book book);

    boolean deleteById(long id);
}
