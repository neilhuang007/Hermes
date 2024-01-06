package dev.hermes.server.handlers;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.hermes.utils.client.file.FileUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HtmlHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String filePath = "src/main/assets/hermes/ui/index.html";
        byte[] responseContentByte = Files.readAllBytes(Paths.get(filePath));

        httpExchange.getResponseHeaders().add("Content-Type:", "text/html;charset=utf-8");

        httpExchange.sendResponseHeaders(200, responseContentByte.length);

        OutputStream out = httpExchange.getResponseBody();
        out.write(responseContentByte);
        out.flush();
        out.close();
    }
}
