package io.github.ivanchintov.bookcatalog.integration;

import io.github.ivanchintov.bookcatalog.proto.Book;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetBookTest extends BaseTest {

    private static final long DUNE_ID = 1L;
    private static final long UNKNOWN_BOOK_ID = 333L;


    @Test
    public void shouldReturnBookWhenBookExists() {
        Book book = client.getBook(DUNE_ID);

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
                () -> client.getBook(UNKNOWN_BOOK_ID));

        assertThat(exception.getStatus().getCode())
                .isEqualTo(Status.Code.NOT_FOUND);
        assertThat(exception.getStatus().getDescription())
                .isEqualTo("Book with ID: 333 was not found.");
    }
}
