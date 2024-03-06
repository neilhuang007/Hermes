package dev.hermes.server.handlers.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.hermes.Hermes;
import dev.hermes.module.Module;
import dev.hermes.utils.url.URLUtil;
import dev.hermes.module.value.Value;
import dev.hermes.module.value.impl.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SetModuleSettingsHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String[] param = URLUtil.getValues(httpExchange);
        String moduleName = param[0];
        String name = param[1];
        String value = param[2];
        String options = param[3];

        Module module = Hermes.moduleManager.get(moduleName);
        JsonObject JsonObject = new JsonObject();

        for (Value<?> setting : module.getAllValues()) {
            if (setting.getName().equals(name)) {
                if (setting instanceof BooleanValue) {
                    ((BooleanValue) setting).setValue(value.equals("true"));
                    JsonObject.addProperty("result", value.equals("true"));
                } else if (setting instanceof NumberValue) {
                    ((NumberValue) setting).setValue(Double.valueOf(value));
                    JsonObject.addProperty("result", Double.valueOf(value));
                } else if (setting instanceof StringValue) {
                    ((StringValue) setting).setValue(value);
                    JsonObject.addProperty("result", value);
                } else if (setting instanceof ModeValue) {
                    JsonArray values = new JsonArray();
                    values.addAll(((ModeValue) setting).getAllSubValuesAsJson());
                    int currentIndex = ((ModeValue) setting).getModes().indexOf(setting.getValue());
                    for(int i=0;i < values.size();i++){
                        if(values.get(i).getAsString().equals(URLUtil.decode(value))){
                            currentIndex = i;
                        }
                    }
                    ((ModeValue) setting).setValue(((ModeValue) setting).getModes().get(currentIndex));
                    JsonObject.addProperty("result", URLUtil.encode(value));
                } else if (setting instanceof BoundsNumberValue) {
                    if (options.equals("min")) {
                        ((BoundsNumberValue) setting).setValue(Float.parseFloat(URLUtil.decode(value)));
                    }
                    if (options.equals("max")) {
                        ((BoundsNumberValue) setting).setSecondValue(Float.parseFloat(URLUtil.decode(value)));
                    }
                    JsonObject.addProperty("result", URLUtil.encode(value));
                } else if (setting instanceof ListValue<?>) {
                    JsonArray values = new JsonArray();
                    values.addAll(((ListValue) setting).getSubValuesAsJson());
                    int currentIndex = ((ListValue) setting).getModes().indexOf(setting.getValue());
                    for(int i=0;i < values.size();i++){
                        if(values.get(i).getAsString().equals(URLUtil.decode(value))){
                            currentIndex = i;
                        }
                    }
                    Object Svalue = ((ListValue<?>) setting).getModes().get(currentIndex);
                    setting.setValueAsObject(Svalue);
                    JsonObject.addProperty("result", URLUtil.encode(value));
                }
            }
        }

        JsonObject.addProperty("success", true);

        byte[] response = JsonObject.toString().getBytes(StandardCharsets.UTF_8);

        // Set CORS headers
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");  // Allow requests from any origin
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");

        httpExchange.sendResponseHeaders(200, response.length);

        OutputStream out = httpExchange.getResponseBody();
        out.write(response);
        out.flush();
        out.close();
    }

}