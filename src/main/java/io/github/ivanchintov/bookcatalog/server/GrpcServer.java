package io.github.ivanchintov.bookcatalog.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {

    private final int port;
    private final Server server;

    public GrpcServer(int port, BindableService... services) {
        this.port = port;
        ServerBuilder<?> builder = ServerBuilder.forPort(port);

        for (BindableService service : services) {
            builder.addService(service);
        }

        this.server = builder.build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("gRPC server started on port " + port);
    }

    public void stop() {
        server.shutdown();
    }

    // Blocks until the server is shut down.
    public void blockUntilShutdown() throws InterruptedException {
        server.awaitTermination();
    }

    @Override
    public String toString() {
        return "GrpcServer{port=" + port + "}";
    }
}
