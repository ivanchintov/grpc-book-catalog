package io.github.ivanchintov.bookcatalog.service;

import io.github.ivanchintov.bookcatalog.proto.AddBookRequest;
import io.github.ivanchintov.bookcatalog.proto.Book;
import io.github.ivanchintov.bookcatalog.proto.Genre;
import io.github.ivanchintov.bookcatalog.repository.BookRepository;
import io.github.ivanchintov.bookcatalog.validation.AddBookValidator;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class AddBookServiceTest {

    private BookRepository repository;
    private AddBookValidator validator;
    private BookCatalogService service;
    private StreamObserver<Book> responseObserver;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        repository = mock(BookRepository.class);
        validator = mock(AddBookValidator.class);
        responseObserver = mock(StreamObserver.class);

        service = new BookCatalogService(repository, validator);
    }

    @Test
    public void shouldSaveBookWhenRequestIsValid() {
        String isbn = "0670813028";
        when(validator.validateIsbn(isbn)).thenReturn(isbn);
        when(repository.findByIsbn(isbn)).thenReturn(Optional.empty());

        Book savedBook = Book.newBuilder()
                .setId(1L)
                .setTitle("It")
                .setAuthor("Stephen King")
                .setIsbn(isbn)
                .setPublicationYear(1986)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();
        when(repository.save(any(Book.class))).thenReturn(savedBook);

        AddBookRequest request = AddBookRequest.newBuilder()
                .setTitle("It")
                .setAuthor("Stephen King")
                .setIsbn(isbn)
                .setPublicationYear(1986)
                .setGenre(Genre.SCIENCE_FICTION)
                .build();

        service.addBook(request, responseObserver);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(repository).save(bookCaptor.capture());
        verify(responseObserver).onNext(savedBook);
        verify(responseObserver).onCompleted();

        Book bookToSave = bookCaptor.getValue();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(bookToSave.getTitle()).isEqualTo(request.getTitle());
        softly.assertThat(bookToSave.getAuthor()).isEqualTo(request.getAuthor());
        softly.assertThat(bookToSave.getIsbn()).isEqualTo(request.getIsbn());
        softly.assertThat(bookToSave.getPublicationYear()).isEqualTo(request.getPublicationYear());
        softly.assertThat(bookToSave.getGenre()).isEqualTo(request.getGenre());
        softly.assertAll();
    }

    @Test
    public void shouldNotSaveBookWhenValidationFails() {
        doThrow(
                Status.INVALID_ARGUMENT
                        .withDescription("Title cannot be empty.")
                        .asRuntimeException())
                .when(validator).validateTitle("");

        AddBookRequest request = AddBookRequest.newBuilder()
                .setTitle("")
                .build();

        service.addBook(request, responseObserver);

        verify(responseObserver).onError(any(StatusRuntimeException.class));
        verify(repository, never()).save(any(Book.class));
    }

    @Test
    public void shouldRejectBookWhenIsbnAlreadyExists() {
        String isbn = "9780747532699";
        when(validator.validateIsbn(isbn)).thenReturn(isbn);

        Book existingBook = Book.newBuilder()
                .build();
        when(repository.findByIsbn(isbn))
                .thenReturn(Optional.of(existingBook));

        AddBookRequest request = AddBookRequest.newBuilder()
                .setIsbn(isbn)
                .build();

        service.addBook(request, responseObserver);

        ArgumentCaptor<StatusRuntimeException> exceptionCaptor =
                ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(responseObserver).onError(exceptionCaptor.capture());
        verify(repository, never()).save(any());

        StatusRuntimeException exception = exceptionCaptor.getValue();
        assertThat(exception.getStatus().getCode())
                .isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(exception.getStatus().getDescription())
                .isEqualTo("A book with ISBN: " + isbn + " already exists.");
    }
}
