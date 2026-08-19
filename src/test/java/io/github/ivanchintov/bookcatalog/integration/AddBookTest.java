package io.github.ivanchintov.bookcatalog.integration;

import io.github.ivanchintov.bookcatalog.proto.AddBookRequest;
import io.github.ivanchintov.bookcatalog.proto.Book;
import io.github.ivanchintov.bookcatalog.proto.Genre;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddBookTest extends BaseTest {

    @Test
    public void shouldSaveBookSuccessfully() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("Foundation")
                .setAuthor("Isaac Asimov")
                .setIsbn("9780553293357")
                .setPublicationYear(1951)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        Book createdBook = client.addBook(addBookRequest);

        Book persistedBook = client.getBook(createdBook.getId());
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(persistedBook.getTitle()).isEqualTo(addBookRequest.getTitle());
        softly.assertThat(persistedBook.getAuthor()).isEqualTo(addBookRequest.getAuthor());
        softly.assertThat(persistedBook.getIsbn()).isEqualTo(addBookRequest.getIsbn());
        softly.assertThat(persistedBook.getPublicationYear()).isEqualTo(addBookRequest.getPublicationYear());
        softly.assertThat(persistedBook.getGenre()).isEqualTo(addBookRequest.getGenre());
        softly.assertAll();
    }

    @Test
    public void shouldPersistMultipleBooksIndependently() {
        AddBookRequest firstRequest = AddBookRequest.newBuilder()
                .setTitle("The Lord of The Rings")
                .setAuthor("J. R. R. Tolkien")
                .setIsbn("978-0-261-10320-7")
                .setPublicationYear(1969)
                .setGenre(Genre.FANTASY)
                .build();

        AddBookRequest secondRequest = AddBookRequest.newBuilder()
                .setTitle("Skiing the Balkans")
                .setAuthor("Dimitar Dimitrov")
                .setIsbn("9786199080900")
                .setPublicationYear(2017)
                .setGenre(Genre.SPORTS)
                .build();

        Book firstBook = client.addBook(firstRequest);
        Book secondBook = client.addBook(secondRequest);

        Book persistedFirstBook = client.getBook(firstBook.getId());
        Book persistedSecondBook = client.getBook(secondBook.getId());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(firstBook.getId()).isNotEqualTo(secondBook.getId());
        softly.assertThat(persistedFirstBook.getTitle()).isEqualTo(firstRequest.getTitle());
        softly.assertThat(persistedSecondBook.getTitle()).isEqualTo(secondRequest.getTitle());
        softly.assertAll();
    }

    @Test
    public void shouldRejectDuplicatedIsbn() {
        AddBookRequest firstRequest = AddBookRequest.newBuilder()
                .setTitle("Harry Potter and the Philosopher's Stone")
                .setAuthor("J. K. Rowling")
                .setIsbn("978-0-7475-3269-9")
                .setPublicationYear(1997)
                .setGenre(Genre.FANTASY)
                .build();

        AddBookRequest secondRequest = AddBookRequest.newBuilder()
                .setTitle("Harry Potter and the Philosopher's Stone")
                .setAuthor("J. K. Rowling")
                .setIsbn("978-0-7475-3269-9")
                .setPublicationYear(1997)
                .setGenre(Genre.FANTASY)
                .build();

        client.addBook(firstRequest);
        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.addBook(secondRequest));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(exception.getStatus().getDescription())
                .isEqualTo("A book with ISBN: 9780747532699 already exists.");
    }
}
