package com.shqipbot;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    🇦🇱 S H Q I P B O T 🇦🇱                  ║");
        System.out.println("║              Boti Ekonomik Shqiptar për Discord            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        String botToken = System.getenv("DISCORD_TOKEN");
        
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("❌ Gabim: DISCORD_TOKEN nuk u gjet!");
            System.err.println("Shto variablin DISCORD_TOKEN në Railway.");
            return;
        }
        
        System.out.println("✅ Token-i u lexua nga variabli i mjedisit!");
        System.out.println("🚀 Duke nisur ShqipBot-in...");
        
        try {
            ShqipBot bot = new ShqipBot(botToken);
            System.out.println("✅ ShqipBot është gati dhe po punon 24/7!");
            
            // 🔥 Në vend të while loop, JDA e mban lidhjen WebSocket gjallë
            // Thjesht bllokoje thread-in kryesor
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }
        } catch (Exception e) {
            System.err.println("❌ Gabim fatal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}