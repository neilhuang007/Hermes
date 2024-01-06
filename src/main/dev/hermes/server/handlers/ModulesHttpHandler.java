package dev.hermes.server.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.hermes.Hermes;
import dev.hermes.module.Module;
import dev.hermes.utils.url.URLUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ModulesHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String category = URLUtil.getValues(httpExchange)[0];

        JsonObject response = new JsonObject();

        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:63342");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");


        for (Module module : Hermes.moduleManager.getModuleList()) {
            if (module.getCategory().name().equals(category)) {
                JsonObject moduleJson = new JsonObject();
                moduleJson.addProperty("name", module.getModuleName());
                moduleJson.addProperty("description", module.getDescription());

                // Add more properties as needed

                response.add(module.getModuleName(), moduleJson);
            }
        }

        byte[] res = response.toString().getBytes(StandardCharsets.UTF_8);

        System.out.println(res);


        httpExchange.sendResponseHeaders(200, res.length);

        OutputStream out = httpExchange.getResponseBody();
        out.write(res);
        out.flush();
        out.close();
    }
}
