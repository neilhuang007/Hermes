package dev.hermes.server.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.hermes.Hermes;
import dev.hermes.module.Module;
import dev.hermes.utils.url.URLUtil;
import dev.hermes.value.Value;
import dev.hermes.value.impl.BoolValue;
import dev.hermes.value.impl.ListValue;
import dev.hermes.value.impl.NumberValue;
import dev.hermes.value.impl.StringValue;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ModuleSettingsHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String moduleName = URLUtil.getValues(httpExchange)[0];

        JsonObject jsonObject = new JsonObject();
        boolean isFound = false;

        for (Module module : Hermes.moduleManager.getModules()) {
            if (module.getName().equals(moduleName)) {
                JsonArray moduleJsonArray = new JsonArray();
                isFound = true;
                for (Value<?> setting : module.getValues()) {
                    JsonObject moduleSet = new JsonObject();
                    if (setting instanceof StringValue) {
                        moduleSet.addProperty("name", setting.getName());
                        moduleSet.addProperty("type", "input");
                        moduleSet.addProperty("value", ((StringValue) setting).getValue());
                    } else if (setting instanceof NumberValue) {
                        moduleSet.addProperty("name", setting.getName());
                        moduleSet.addProperty("type", "slider");
                        moduleSet.addProperty("min", ((NumberValue) setting).getMinimum().doubleValue());
                        moduleSet.addProperty("max", ((NumberValue) setting).getMaximum().doubleValue());
                        moduleSet.addProperty("step", 1);
                        moduleSet.addProperty("value", ((NumberValue) setting).getValue().doubleValue());
                        moduleSet.addProperty("suffix", ((NumberValue) setting).getSuffix());
                    } else if (setting instanceof ListValue) {
                        moduleSet.addProperty("name", setting.getName());
                        moduleSet.addProperty("type", "selection");
                        JsonArray values = new JsonArray();
                        values.addAll(((ListValue) setting).getValues());
                        moduleSet.addProperty("value", URLUtil.encode(((ListValue) setting).getValue()));
                    } else if (setting instanceof BoolValue) {
                        moduleSet.addProperty("name", setting.getName());
                        moduleSet.addProperty("type", "checkbox");
                        moduleSet.addProperty("value", ((BoolValue) setting).getValue());
                    }
                    moduleJsonArray.add(moduleSet);
                }
                jsonObject.add("result", moduleJsonArray);
            }
        }

        jsonObject.addProperty("success", isFound);
        if (!isFound) jsonObject.addProperty("reason", "Can't find module");

        byte[] response = jsonObject.toString().getBytes(StandardCharsets.UTF_8);

        httpExchange.sendResponseHeaders(200, response.length);

        OutputStream out = httpExchange.getResponseBody();
        out.write(response);
        out.flush();
        out.close();
    }

}