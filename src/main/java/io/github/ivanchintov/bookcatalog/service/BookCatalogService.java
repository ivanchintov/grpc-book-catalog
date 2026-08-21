package io.github.ivanchintov.bookcatalog.service;

import com.google.protobuf.Empty;
import io.github.ivanchintov.bookcatalog.proto.*;
import io.github.ivanchintov.bookcatalog.repository.BookRepository;
import io.github.ivanchintov.bookcatalog.validation.AddBookValidator;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Optional;

public class BookCatalogService extends BookCatalogGrpc.BookCatalogImplBase {

    private final BookRepository bookRepository;
    private final AddBookValidator addBookValidator;

    public BookCatalogService(BookRepository bookRepository, AddBookValidator addBookValidator) {
        this.bookRepository = bookRepository;
        this.addBookValidator = addBookValidator;
    }

    @Override
    public void getBook(GetBookRequest request, StreamObserver<Book> responseObserver) {
        long bookId = request.getId();

        Optional<Book> book = bookRepository.findById(bookId);

        if (book.isPresent()) {
            responseObserver.onNext(book.get());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Book with ID: " + bookId + " was not found.")
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void addBook(AddBookRequest request, StreamObserver<Book> responseObserver) {
        String isbn;

        try {
            isbn = validateAddBookRequest(request);
        } catch (StatusRuntimeException exception) {
            responseObserver.onError(exception);
            return;
        }

        if (bookRepository.findByIsbn(isbn).isPresent()) {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription("A book with ISBN: " + isbn + " already exists.")
                            .asRuntimeException()
            );

            return;
        }

        Book book = Book.newBuilder()
                .setTitle(request.getTitle())
                .setAuthor(request.getAuthor())
                .setIsbn(isbn)
                .setPublicationYear(request.getPublicationYear())
                .setGenre(request.getGenre())
                .build();

        Book savedBook = bookRepository.save(book);

        responseObserver.onNext(savedBook);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBook(DeleteBookRequest request, StreamObserver<Empty> responseObserver) {
        long id = request.getId();
        boolean deleted = bookRepository.deleteById(id);

        if (!deleted) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Book with ID: " + id + " was not found.")
                            .asRuntimeException());
            return;
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    private String validateAddBookRequest(AddBookRequest request) {
        addBookValidator.validateTitle(request.getTitle());
        addBookValidator.validateAuthor(request.getAuthor());
        String isbn = addBookValidator.validateIsbn(request.getIsbn());
        addBookValidator.validatePublicationYear(request.getPublicationYear());
        addBookValidator.validateGenre(request.getGenre());
        return isbn;
    }
}
