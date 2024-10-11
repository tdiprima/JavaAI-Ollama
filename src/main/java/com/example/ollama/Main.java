package com.example.ollama;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

// java -cp target/ollama-web-interface-1.0.jar com.example.ollama.Main
public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setWar("target/ollama-web-interface-1.0.jar");

        server.setHandler(context);

        server.start();
        server.join();
    }
}
