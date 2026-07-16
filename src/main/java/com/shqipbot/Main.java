package com.shqipbot;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    🇦🇱 S H Q I P B O T 🇦🇱                  ║");
        System.out.println("║              Boti Ekonomik Shqiptar për Discord            ║");
        System.out.println("║                   Botërori 2026 - Live                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // 🔥 Lexo token-in nga variablat e mjedisit (Railway) ose System properties (NetBeans)
        String botToken = System.getenv("DISCORD_TOKEN");
        
        // 🔥 Nëse nuk u gjet në mjedis, provo System property (për NetBeans)
        if (botToken == null || botToken.isEmpty()) {
            botToken = System.getProperty("DISCORD_TOKEN");
        }
        
        if (botToken == null || botToken.isEmpty()) {
            System.err.println("❌ Gabim: DISCORD_TOKEN nuk u gjet!");
            System.err.println();
            System.err.println("💡 Për NetBeans: Run → Set Project Configuration → VM Options:");
            System.err.println("   -DDISCORD_TOKEN=tokeni_yt");
            System.err.println("   -DFOOTBALL_API_KEY=");
            System.err.println();
            System.err.println("💡 Për Railway: Shto variablat:");
            System.err.println("   DISCORD_TOKEN=tokeni_yt");
            System.err.println("   FOOTBALL_API_KEY=2c737cdebf61441c9fff2d");
            return;
        }
        
        System.out.println("✅ Token-i u lexua nga variabli i mjedisit!");
        System.out.println("🚀 Duke nisur ShqipBot-in...");
        
        try {
            ShqipBot bot = new ShqipBot(botToken);
            System.out.println("✅ ShqipBot është gati dhe po punon 24/7!");
            System.out.println("🌍 Botërori 2026 - Ndeshjet live nga API-Football");
            System.out.println("💡 Komandat: 'work, 'bal, 'help, 'worldcup, 'match");
            
            // 🔥 KJO E MBAN BOT-IN GJALLË
            while (true) {
                try {
                    Thread.sleep(60000);
                    System.out.println("💓 ShqipBot është gjallë...");
                } catch (InterruptedException e) {
                    System.out.println("⚠️ Thread u ndërpre, por bot-i vazhdon...");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Gabim: " + e.getMessage());
            e.printStackTrace();
        }
    }
}