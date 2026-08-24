package io.github.ivanchintov.bookcatalog.service;

import io.github.ivanchintov.bookcatalog.proto.Book;
import io.github.ivanchintov.bookcatalog.proto.GetBookRequest;
import io.github.ivanchintov.bookcatalog.repository.BookRepository;
import io.github.ivanchintov.bookcatalog.validation.AddBookValidator;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetBookServiceTest {
    private BookRepository repository;
    private BookCatalogService service;
    private StreamObserver<Book> responseObserver;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        repository = mock(BookRepository.class);
        AddBookValidator validator = mock(AddBookValidator.class);
        responseObserver = mock(StreamObserver.class);

        service = new BookCatalogService(repository, validator);
    }

    @Test
    public void shouldReturnBookWhenBookExists() {
        long id = 33L;
        Book existingBook = Book.newBuilder().build();
        when(repository.findById(id)).thenReturn(Optional.of(existingBook));

        GetBookRequest request = GetBookRequest.newBuilder().setId(id).build();
        service.getBook(request, responseObserver);

        verify(repository).findById(id);
        verify(responseObserver).onNext(existingBook);
        verify(responseObserver).onCompleted();
    }

    @Test
    public void shouldReturnNotFoundWhenBookDoesNotExist() {
        long nonExistingId = 5299268L;
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        GetBookRequest request = GetBookRequest.newBuilder().setId(nonExistingId).build();
        service.getBook(request, responseObserver);

        ArgumentCaptor<StatusRuntimeException> exceptionCaptor =
                ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(responseObserver).onError(exceptionCaptor.capture());
        verify(repository).findById(nonExistingId);
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();

        StatusRuntimeException exception = exceptionCaptor.getValue();
        assertThat(exception.getStatus().getCode())
                .isEqualTo(Status.Code.NOT_FOUND);
        assertThat(exception.getStatus().getDescription())
                .isEqualTo("Book with ID: " + nonExistingId + " was not found.");
    }
}
