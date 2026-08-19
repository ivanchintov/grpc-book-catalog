package io.github.ivanchintov.bookcatalog.integration;

import io.github.ivanchintov.bookcatalog.proto.AddBookRequest;
import io.github.ivanchintov.bookcatalog.proto.Genre;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddBookInvalidFieldsTest extends BaseTest {

    private static final int MAX_TITLE_LENGTH = 500;
    private static final int MAX_AUTHOR_LENGTH = 300;

    @Test
    public void shouldRejectTitleExceedingMaximumLength() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle(textOfLength(MAX_TITLE_LENGTH + 1))
                .setAuthor("Isaac Asimov")
                .setIsbn("978-0-553-29338-8")
                .setPublicationYear(1988)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.addBook(addBookRequest));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription()).isEqualTo("Title cannot exceed 500 characters.");
    }

    @Test
    public void shouldRejectAuthorExceedingMaximumLength() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("Foundation")
                .setAuthor(textOfLength(MAX_AUTHOR_LENGTH + 1))
                .setIsbn("978-0-553-29338-8")
                .setPublicationYear(1988)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.addBook(addBookRequest));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription()).isEqualTo("Author cannot exceed 300 characters.");
    }

    @Test
    public void shouldRejectInvalidIsbn() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("Foundation")
                .setAuthor("Isaac Asimov")
                .setIsbn("978-0-55")
                .setPublicationYear(1989)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.addBook(addBookRequest));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription())
                .isEqualTo("ISBN is not valid.");
    }

    @Test
    public void shouldRejectFutureYear() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("Foundation")
                .setAuthor("Isaac Asimov")
                .setIsbn("978-0-553-29338-8")
                .setPublicationYear(3033)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.addBook(addBookRequest));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription())
                .isEqualTo("Publication year must be a positive year and cannot be in the future.");
    }

    private static String textOfLength(int length) {
        return "a".repeat(length);
    }


}
