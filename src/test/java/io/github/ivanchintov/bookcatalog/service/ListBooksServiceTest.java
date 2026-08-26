package io.github.ivanchintov.bookcatalog.service;

import com.google.protobuf.Empty;
import io.github.ivanchintov.bookcatalog.proto.Book;
import io.github.ivanchintov.bookcatalog.repository.BookRepository;
import io.github.ivanchintov.bookcatalog.validation.AddBookValidator;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListBooksServiceTest {

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
    public void shouldReturnExistingBooks() {
        Book bookOne = Book.newBuilder().build();
        Book bookTwo = Book.newBuilder().build();
        Iterator<Book> books = List.of(bookOne, bookTwo).iterator();
        when(repository.findAll()).thenReturn(books);

        service.listBooks(Empty.getDefaultInstance(), responseObserver);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);

        verify(repository).findAll();
        verify(responseObserver, times(2)).onNext(bookCaptor.capture());
        verify(responseObserver).onCompleted();

        assertThat(bookCaptor.getAllValues())
                .containsExactlyInAnyOrder(bookOne, bookTwo);
    }

    @Test
    public void shouldReturnEmptyStreamWhenNoBooksExist() {
        when(repository.findAll()).thenReturn(Collections.emptyIterator());

        service.listBooks(Empty.getDefaultInstance(), responseObserver);

        verify(repository).findAll();
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver).onCompleted();
    }
}
