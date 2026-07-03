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
            
            // 🔥 KJO E MBAN BOT-IN GJALLË PA CRASH 🔥
            while (true) {
                try {
                    Thread.sleep(60000);
                    System.out.println("💓 ShqipBot është gjallë...");
                } catch (InterruptedException e) {
                    System.out.println("⚠️ Thread u ndërpre, por bot-i vazhdon...");
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Gabim fatal: " + e.getMessage());
            e.printStackTrace();
            // Mos dil nga programi - prit dhe rinis
            while (true) {
                try {
                    Thread.sleep(5000);
                    System.err.println("🔄 Duke u përpjekur të rinis...");
                    ShqipBot bot = new ShqipBot(System.getenv("DISCORD_TOKEN"));
                    System.out.println("✅ Bot-i u rinis me sukses!");
                } catch (Exception ex) {
                    System.err.println("❌ Rinisja dështoi: " + ex.getMessage());
                }
            }
        }
    }
}