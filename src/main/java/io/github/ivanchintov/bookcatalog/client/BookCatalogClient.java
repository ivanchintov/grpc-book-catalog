package io.github.ivanchintov.bookcatalog.client;

import io.github.ivanchintov.bookcatalog.proto.Book;
import io.github.ivanchintov.bookcatalog.proto.BookCatalogGrpc;
import io.github.ivanchintov.bookcatalog.proto.GetBookRequest;
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

    public void shutDown() {
        channel.shutdown();
    }
}
