package io.github.ivanchintov.bookcatalog.service;

import com.google.protobuf.Empty;
import io.github.ivanchintov.bookcatalog.proto.DeleteBookRequest;
import io.github.ivanchintov.bookcatalog.repository.BookRepository;
import io.github.ivanchintov.bookcatalog.validation.AddBookValidator;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class DeleteBookServiceTest {

    private BookRepository repository;
    private BookCatalogService service;
    private StreamObserver<Empty> responseObserver;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        repository = mock(BookRepository.class);
        AddBookValidator validator = mock(AddBookValidator.class);
        responseObserver = mock(StreamObserver.class);

        service = new BookCatalogService(repository, validator);
    }

    @Test
    public void shouldDeleteExistingBook() {
        long id = 1L;
        when(repository.deleteById(id)).thenReturn(true);
        DeleteBookRequest request = DeleteBookRequest.newBuilder()
                .setId(id)
                .build();

        service.deleteBook(request, responseObserver);

        verify(repository).deleteById(id);
        verify(responseObserver).onNext(Empty.getDefaultInstance());
        verify(responseObserver).onCompleted();
    }

    @Test
    public void shouldReturnNotFoundWhenDeletingNonExistingBook() {
        long id = 1L;
        when(repository.deleteById(id)).thenReturn(false);
        DeleteBookRequest request = DeleteBookRequest.newBuilder()
                .setId(id)
                .build();

        service.deleteBook(request, responseObserver);

        ArgumentCaptor<StatusRuntimeException> exceptionCaptor =
                ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(responseObserver).onError(exceptionCaptor.capture());
        verify(repository).deleteById(id);

        StatusRuntimeException exception = exceptionCaptor.getValue();
        assertThat(exception.getStatus().getCode())
                .isEqualTo(Status.Code.NOT_FOUND);
        assertThat(exception.getStatus().getDescription())
                .isEqualTo("Book with ID: " + id + " was not found.");
    }
}
