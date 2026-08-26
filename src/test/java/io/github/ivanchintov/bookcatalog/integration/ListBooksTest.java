package io.github.ivanchintov.bookcatalog.integration;

import io.github.ivanchintov.bookcatalog.proto.Book;
import io.github.ivanchintov.bookcatalog.proto.Genre;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class ListBooksTest extends BaseTest {

    @Test
    public void shouldReturnExistingBooks() {
        Book expectedDune = Book.newBuilder()
                .setId(1L)
                .setTitle("Dune")
                .setAuthor("Frank Herbert")
                .setIsbn("978-0-240-80772-0")
                .setPublicationYear(1965)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        Book expectedTheWayOfKings = Book.newBuilder()
                .setId(2L)
                .setTitle("The Way of Kings")
                .setAuthor("Brandon Sanderson")
                .setIsbn("978-0-7653-2635-5")
                .setPublicationYear(2010)
                .setGenre(Genre.FANTASY)
                .build();

        Iterator<Book> books = client.listBooks();

        assertThat(books)
                .toIterable()
                .containsExactlyInAnyOrder(expectedDune, expectedTheWayOfKings);
    }
}
