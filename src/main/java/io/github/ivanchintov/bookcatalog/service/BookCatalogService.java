package io.github.ivanchintov.bookcatalog.service;

import io.github.ivanchintov.bookcatalog.proto.AddBookRequest;
import io.github.ivanchintov.bookcatalog.proto.Book;
import io.github.ivanchintov.bookcatalog.proto.BookCatalogGrpc;
import io.github.ivanchintov.bookcatalog.proto.GetBookRequest;
import io.github.ivanchintov.bookcatalog.repository.BookRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Optional;

public class BookCatalogService extends BookCatalogGrpc.BookCatalogImplBase {

    private final BookRepository bookRepository;

    public BookCatalogService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
        Book book = Book.newBuilder()
                .setTitle(request.getTitle())
                .setAuthor(request.getAuthor())
                .setIsbn(request.getIsbn())
                .setPublicationYear(request.getPublicationYear())
                .setGenre(request.getGenre())
                .build();

        Book savedBook = bookRepository.save(book);

        responseObserver.onNext(savedBook);
        responseObserver.onCompleted();
    }
}
