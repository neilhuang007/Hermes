package dev.hermes.module.impl.render.clickgui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleHttpServer {

    private HttpServer server;

    public void startServer(String basePath, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        File baseDir = new File(basePath);

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String filePath = baseDir.getAbsolutePath() + exchange.getRequestURI().getPath();
                System.out.println("Request: " + filePath);
                if ("/".equals(exchange.getRequestURI().getPath())) {
                    filePath += "index.html"; // Default to index.html if root is requested
                }
                File file = new File(filePath);
                if (file.exists() && !file.isDirectory()) {
                    exchange.sendResponseHeaders(200, file.length());
                    try (OutputStream os = exchange.getResponseBody(); FileInputStream fs = new FileInputStream(file)) {
                        final byte[] buffer = new byte[0x10000];
                        int count;
                        while ((count = fs.read(buffer)) >= 0) {
                            os.write(buffer, 0, count);
                        }
                    }
                } else {
                    String response = "404 (Not Found)\n";
                    exchange.sendResponseHeaders(404, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            }
        });

        server.start();
        System.out.println("Server started at http://localhost:" + port);
    }

    public void stopServer() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server stopped.");
        }
    }
}
