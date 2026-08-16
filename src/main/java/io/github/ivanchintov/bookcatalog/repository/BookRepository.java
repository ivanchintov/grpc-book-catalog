package io.github.ivanchintov.bookcatalog.repository;

import io.github.ivanchintov.bookcatalog.proto.Book;

import java.util.Optional;

public interface BookRepository {

    Optional<Book> findById(long id);

    Book save(Book book);
}
