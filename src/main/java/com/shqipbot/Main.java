package com.shqipbot;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.OutputStream;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    🇦🇱 S H Q I P B O T 🇦🇱                  ║");
        System.out.println("║              Boti Ekonomik Shqiptar për Discord            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // 🔥 Server HTTP për healthcheck (i duhet Cloud Run)
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", exchange -> {
                String response = "OK";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });
            server.setExecutor(null);
            server.start();
            System.out.println("✅ Healthcheck server started on port 8080");
        } catch (Exception e) {
            System.out.println("⚠️ Healthcheck server not started: " + e.getMessage());
        }
        
        // 🔥 Lexo token-in
        String botToken = System.getenv("DISCORD_TOKEN");
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("❌ Gabim: DISCORD_TOKEN nuk u gjet!");
            System.err.println("Shto variablin DISCORD_TOKEN në Google Cloud Run.");
            return;
        }
        
        System.out.println("✅ Token-i u lexua!");
        System.out.println("🚀 Duke nisur ShqipBot-in...");
        
        try {
            ShqipBot bot = new ShqipBot(botToken);
            System.out.println("✅ ShqipBot është gati dhe po punon 24/7!");
            
            // 🔥 E mban bot-in gjallë
            while (true) {
                Thread.sleep(60000);
                System.out.println("💓 ShqipBot është gjallë...");
            }
        } catch (Exception e) {
            System.err.println("❌ Gabim: " + e.getMessage());
            e.printStackTrace();
        }
    }
}