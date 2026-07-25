package io.github.ivanchintov.bookcatalog.repository;

import io.github.ivanchintov.bookcatalog.proto.Book;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryBookRepository implements BookRepository{

    private final Map<Long, Book> books = new HashMap<>();

    public InMemoryBookRepository() {
        initializeBooks();
    }


    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(books.get(id));
    }

    private void initializeBooks() {
        Book dune = Book.newBuilder()
                .setId(1L)
                .setTitle("Dune")
                .setAuthor("Frank Herbert")
                .build();

        Book theWayOfKings = Book.newBuilder()
                .setId(2L)
                .setTitle("The Way of Kings")
                .setAuthor("Brandon Sanderson")
                .build();

        books.put(dune.getId(), dune);
        books.put(dune.getId(), theWayOfKings);
    }
}
