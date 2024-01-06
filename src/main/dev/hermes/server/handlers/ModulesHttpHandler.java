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


        for (Module module : Hermes.moduleManager.getModuleList()) {
            if (module.getCategory().name().equals(category)) {
                JsonObject moduleJson = new JsonObject();
                moduleJson.addProperty("name", module.getModuleName());
                moduleJson.addProperty("description", module.getDescription());


                // Add more properties as needed

                response.add(module.getModuleName(), moduleJson);
            }
        }


        httpExchange.sendResponseHeaders(200, response.getAsByte());

        OutputStream out = httpExchange.getResponseBody();
        out.write(response.getAsByte());
        out.flush();
        out.close();
    }
}
