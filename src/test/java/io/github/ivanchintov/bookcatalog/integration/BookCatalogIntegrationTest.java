package io.github.ivanchintov.bookcatalog.integration;

import io.github.ivanchintov.bookcatalog.client.BookCatalogClient;
import io.github.ivanchintov.bookcatalog.proto.AddBookRequest;
import io.github.ivanchintov.bookcatalog.proto.Book;
import io.github.ivanchintov.bookcatalog.proto.Genre;
import io.github.ivanchintov.bookcatalog.repository.BookRepository;
import io.github.ivanchintov.bookcatalog.repository.InMemoryBookRepository;
import io.github.ivanchintov.bookcatalog.server.GrpcServer;
import io.github.ivanchintov.bookcatalog.service.BookCatalogService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookCatalogIntegrationTest {

    private static final int PORT = 9090;

    private static final BookRepository REPOSITORY = new InMemoryBookRepository();
    private static final BookCatalogService SERVICE = new BookCatalogService(REPOSITORY);
    private static final GrpcServer SERVER = new GrpcServer(PORT, SERVICE);
    private static final BookCatalogClient CLIENT = new BookCatalogClient("localhost", PORT);

    private static final long DUNE_ID = 1L;
    private static final long UNKNOWN_BOOK_ID = 333L;

    @BeforeAll
    public static void setUp() throws IOException {
        SERVER.start();
    }

    @AfterAll
    public static void tearDown() {
        CLIENT.shutdown();
        SERVER.stop();
    }

    @Test
    public void shouldReturnBookWhenBookExists() {
        Book book = CLIENT.getBook(DUNE_ID);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(book.getId()).isEqualTo(DUNE_ID);
        softly.assertThat(book.getTitle()).isEqualTo("Dune");
        softly.assertThat(book.getAuthor()).isEqualTo("Frank Herbert");
        softly.assertAll();
    }

    @Test
    public void shouldReturnNotFoundWhenBookDoesNotExist() {
        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> CLIENT.getBook(UNKNOWN_BOOK_ID));

        assertThat(exception.getStatus().getCode())
                .isEqualTo(Status.Code.NOT_FOUND);
        assertThat(exception.getStatus().getDescription())
                .isEqualTo("Book with ID: 333 was not found.");
    }

    @Test
    public void shouldSaveBookSuccessfully() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("Foundation")
                .setAuthor("Isaac Asimov")
                .setIsbn("9780553293357")
                .setPublicationYear(1951)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        Book createdBook = CLIENT.addBook(addBookRequest);

        Book persistedBook = CLIENT.getBook(createdBook.getId());
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
                .setIsbn("978-6199080900")
                .setPublicationYear(2017)
                .setGenre(Genre.SPORTS)
                .build();

        Book firstBook = CLIENT.addBook(firstRequest);
        Book secondBook = CLIENT.addBook(secondRequest);

        Book persistedFirstBook = CLIENT.getBook(firstBook.getId());
        Book persistedSecondBook = CLIENT.getBook(secondBook.getId());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(firstBook.getId()).isNotEqualTo(secondBook.getId());
        softly.assertThat(persistedFirstBook.getTitle()).isEqualTo(firstRequest.getTitle());
        softly.assertThat(persistedSecondBook.getTitle()).isEqualTo(secondRequest.getTitle());
        softly.assertAll();
    }
}
