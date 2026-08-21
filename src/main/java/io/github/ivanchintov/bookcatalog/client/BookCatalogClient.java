package io.github.ivanchintov.bookcatalog.client;

import com.google.protobuf.Empty;
import io.github.ivanchintov.bookcatalog.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BookCatalogClient {

    private final ManagedChannel channel;
    private final BookCatalogGrpc.BookCatalogBlockingStub stub;

    public BookCatalogClient(String host, int port) {
        this.channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext() // If this were production, we'd configure TLS certificates instead.
                .build();

        this.stub = BookCatalogGrpc.newBlockingStub(channel);
    }

    public Book getBook(long id) {
        GetBookRequest request = GetBookRequest.newBuilder()
                .setId(id)
                .build();

        return stub.getBook(request);
    }

    public Book addBook(AddBookRequest request) {
        return stub.addBook(request);
    }

    public Empty deleteBook(long id) {
        DeleteBookRequest request = DeleteBookRequest.newBuilder()
                .setId(id)
                .build();
        return stub.deleteBook(request);
    }

    public void shutdown() {
        channel.shutdown();
    }
}
