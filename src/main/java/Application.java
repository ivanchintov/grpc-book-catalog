import io.github.ivanchintov.bookcatalog.repository.BookRepository;
import io.github.ivanchintov.bookcatalog.repository.InMemoryBookRepository;
import io.github.ivanchintov.bookcatalog.server.GrpcServer;
import io.github.ivanchintov.bookcatalog.service.BookCatalogService;

public class Application {

    static void main(String[] args) throws Exception {
        BookRepository repository = new InMemoryBookRepository();

        BookCatalogService service = new BookCatalogService(repository);

        GrpcServer server = new GrpcServer(9090, service);

        server.start();
        server.blockUntilShutdown();
    }
}
