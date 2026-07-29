package io.github.ivanchintov.bookcatalog.integration;

import io.github.ivanchintov.bookcatalog.client.BookCatalogClient;
import io.github.ivanchintov.bookcatalog.proto.Book;
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

    @AfterAll
    public static void tearDown() {
        CLIENT.shutdown();
        SERVER.stop();
    }
}
