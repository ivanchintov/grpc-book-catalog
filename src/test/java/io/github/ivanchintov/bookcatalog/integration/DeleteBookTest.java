package io.github.ivanchintov.bookcatalog.integration;

import io.github.ivanchintov.bookcatalog.proto.AddBookRequest;
import io.github.ivanchintov.bookcatalog.proto.Book;
import io.github.ivanchintov.bookcatalog.proto.Genre;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteBookTest extends BaseTest {

    @Test
    public void shouldDeleteExistingBook() {
        AddBookRequest addBookRequest = AddBookRequest.newBuilder()
                .setTitle("The Guns of August")
                .setAuthor("Barbara W. Tuchman")
                .setIsbn("978-1-4361-7732-0")
                .setPublicationYear(1962)
                .setGenre(Genre.HISTORY)
                .build();

        Book book = client.addBook(addBookRequest);
        long id = book.getId();
        client.deleteBook(id);

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.getBook(id));

        assertThat(exception.getStatus().getCode())
                .isEqualTo(Status.Code.NOT_FOUND);
        assertThat(exception.getStatus().getDescription())
                .isEqualTo("Book with ID: " + id + " was not found.");
    }

    @Test
    public void shouldReturnNotFoundWhenDeletingNonExistingBook() {
        long nonExistingId = 2832432;

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.deleteBook(nonExistingId));

        assertThat(exception.getStatus().getCode())
                .isEqualTo(Status.Code.NOT_FOUND);
        assertThat(exception.getStatus().getDescription())
                .isEqualTo("Book with ID: " + nonExistingId + " was not found.");
    }
}
