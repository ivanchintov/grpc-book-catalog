package io.github.ivanchintov.bookcatalog.integration;

import io.github.ivanchintov.bookcatalog.client.BookCatalogClient;
import io.github.ivanchintov.bookcatalog.repository.BookRepository;
import io.github.ivanchintov.bookcatalog.repository.InMemoryBookRepository;
import io.github.ivanchintov.bookcatalog.server.GrpcServer;
import io.github.ivanchintov.bookcatalog.service.BookCatalogService;
import io.github.ivanchintov.bookcatalog.validation.AddBookValidator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    protected BookCatalogClient client;
    private GrpcServer server;

    @BeforeAll
    public void setUp() throws IOException {
        BookRepository repository = new InMemoryBookRepository();
        AddBookValidator validator = new AddBookValidator();
        BookCatalogService service = new BookCatalogService(repository, validator);

        int port = 9090;
        server = new GrpcServer(port, service);
        server.start();

        client = new BookCatalogClient("localhost", port);
    }

    @AfterAll
    public void tearDown() {
        client.shutdown();
        server.stop();
    }

}
