package io.github.ivanchintov.bookcatalog.integration;

import io.github.ivanchintov.bookcatalog.proto.AddBookRequest;
import io.github.ivanchintov.bookcatalog.proto.Genre;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddBookRequiredFieldsTest extends BaseTest {

    @Test
    public void shouldRejectBlankTitle() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("")
                .setAuthor("Isaac Asimov")
                .setIsbn("978-0-553-29338-8")
                .setPublicationYear(1988)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.addBook(addBookRequest));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription()).isEqualTo("Title cannot be empty.");
    }

    @Test
    public void shouldRejectBlankAuthor() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("Foundation")
                .setAuthor("")
                .setIsbn("978-0-553-29338-8")
                .setPublicationYear(1988)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.addBook(addBookRequest));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription()).isEqualTo("Author cannot be empty.");

    }

    @Test
    public void shouldRejectBlancIsbn() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("Foundation")
                .setAuthor("Isaac Asimov")
                .setIsbn("")
                .setPublicationYear(1988)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.addBook(addBookRequest));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription()).isEqualTo("ISBN cannot be empty.");
    }

    @Test
    public void shouldRejectMissingPublicationYear() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("Foundation")
                .setAuthor("Isaac Asimov")
                .setIsbn("978-0-553-29338-8")
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.addBook(addBookRequest));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription()).isEqualTo("Publication year cannot be empty.");
    }

    @Test
    public void shouldRejectMissingGenre() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("Foundation")
                .setAuthor("Isaac Asimov")
                .setIsbn("978-0-553-29338-8")
                .setPublicationYear(1988)
                .build();

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.addBook(addBookRequest));

        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(exception.getStatus().getDescription()).isEqualTo("Genre cannot be empty.");
    }
}
