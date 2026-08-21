package io.github.ivanchintov.bookcatalog.repository;

import io.github.ivanchintov.bookcatalog.proto.Book;
import io.github.ivanchintov.bookcatalog.proto.Genre;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryBookRepository implements BookRepository {

    private final Map<Long, Book> books = new HashMap<>();

    public InMemoryBookRepository() {
        initializeBooks();
    }

    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(books.get(id));
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return books.values()
                .stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();
    }

    @Override
    public Book save(Book book) {
        long id = getNextId();
        Book savedBook = book.toBuilder()
                .setId(id)
                .build();
        books.put(id, savedBook);
        return savedBook;
    }

    @Override
    public boolean deleteById(long id) {
        return books.remove(id) != null;
    }

    private void initializeBooks() {
        Book dune = Book.newBuilder()
                .setId(1L)
                .setTitle("Dune")
                .setAuthor("Frank Herbert")
                .setIsbn("978-0-240-80772-0")
                .setPublicationYear(1965)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        Book theWayOfKings = Book.newBuilder()
                .setId(2L)
                .setTitle("The Way of Kings")
                .setAuthor("Brandon Sanderson")
                .setIsbn("978-0-7653-2635-5")
                .setPublicationYear(2010)
                .setGenre(Genre.FANTASY)
                .build();

        books.put(dune.getId(), dune);
        books.put(theWayOfKings.getId(), theWayOfKings);
    }

    private long getNextId() {
        return books
                .keySet()
                .stream()
                .max(Long::compare)
                .map(id -> id + 1)
                .orElse(1L);
    }
}
