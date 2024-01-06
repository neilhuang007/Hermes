package dev.hermes.server;


import com.sun.net.httpserver.HttpServer;
import dev.hermes.server.handlers.*;
import dev.hermes.utils.client.log.LogUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HermesServer {

    private static HttpServer server;

    public static void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 1342), 0);
        server.createContext("/", new HtmlHttpHandler());
        server.createContext("/api/modulesList", new ModulesHttpHandler());
//        server.createContext("/api/setStatus", new StatusHttpHandler());
//        server.createContext("/api/modulesList", new ModulesHttpHandler());
//        server.createContext("/api/updateModulesInfo", new ModuleInfoHttpHandler());
//        server.createContext("/api/categoriesList", new CategoriesHttpHandler());
//        server.createContext("/api/getModuleSetting", new SettingsHttpHandler());
//        server.createContext("/api/setModuleSettingValue", new SetSettingHttpHandler());
//        server.createContext("/api/setBind", new BindHttpHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));

        server.start();
        LogUtil.printLog("Server started on port 8080");
    }

    public static void stop() {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        start();
    }


}
