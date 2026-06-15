package com.shqipbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.util.Random;

public class ShqipBot extends ListenerAdapter {
    
    private Database db;
    private Random random;
    private JDA jda;
    private String lastCommand = "";
    private long lastCommandTime = 0;
    
    private final String ADMIN_ID = "123456789012345678"; // Zëvendëso me ID-në tënde!
    
    public ShqipBot(String token) throws Exception {
        this.db = new Database();
        this.random = new Random();
        
        System.out.println("🔌 Duke u lidhur me Discord-in...");
        
        this.jda = JDABuilder.createLight(token, 
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("'work për lekë 🇦🇱"))
                .addEventListeners(this)
                .build();
        
        jda.awaitReady();
        System.out.println("✅ ShqipBot u lidh! Ping: " + jda.getGatewayPing() + " ms");
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        String message = event.getMessage().getContentRaw();
        if (!message.startsWith("'")) return;
        
        long now = System.currentTimeMillis();
        if (message.equals(lastCommand) && (now - lastCommandTime) < 1000) {
            return;
        }
        lastCommand = message;
        lastCommandTime = now;
        
        String userId = event.getAuthor().getId();
        String username = event.getAuthor().getName();
        String command = message.toLowerCase();
        
        System.out.println("📨 " + username + ": " + command);
        
        db.krijoPerdorues(userId, username);
        
        if (command.equals("'work")) {
            String[][] punet = {
                {"pastrues në Tiranë", "Fshive rrugët te Blloku", "50"},
                {"ndërtimtar te Aneks", "Vure tulla por shefi mashtroi", "120"},
                {"shitës në pazar", "Shite rroba kineze", "80"},
                {"kamarier në Durrës", "Servire turistët", "70"},
                {"taksi i fshehtë", "Çove njerëz pa targë", "90"}
            };
            int index = random.nextInt(punet.length);
            int fitimi = Integer.parseInt(punet[index][2]);
            db.shtoPara(userId, fitimi);
            event.getChannel().sendMessage("🇦🇱 **" + username + "** punoi si " + 
                    punet[index][0] + ".\n" + punet[index][1] + "\n💰 +" + fitimi + " lekë").queue();
        }
        else if (command.equals("'slut")) {
            String[][] skenaret = {
                {"Tregove vallëzim te Pink Floyd", "200"},
                {"Ofrove masazh por ishte inspektor", "-100"},
                {"Këndove në dasmë me fustan të shkurtër", "350"},
                {"Të kapën me klient te Liqeni", "-250"}
            };
            int index = random.nextInt(skenaret.length);
            int shuma = Integer.parseInt(skenaret[index][1]);
            db.shtoPara(userId, shuma);
            String shenja = (shuma > 0) ? "+" : "";
            event.getChannel().sendMessage("🍑 **" + username + "**: " + 
                    skenaret[index][0] + "\n💰 " + shenja + shuma + " lekë").queue();
        }
        else if (command.equals("'crime")) {
            String[][] krime = {
                {"Shite CD te Rruga e Kavajës", "500"},
                {"U përpoqe të korruptosh policin", "-300"},
                {"Kontrabandove cigare nga Mali i Zi", "1000"},
                {"Të kapën me armë pa leje te Farka", "-500"}
            };
            int index = random.nextInt(krime.length);
            int shuma = Integer.parseInt(krime[index][1]);
            db.shtoPara(userId, shuma);
            String shenja = (shuma > 0) ? "+" : "";
            event.getChannel().sendMessage("😈 **" + username + "**: " + 
                    krime[index][0] + "\n💰 " + shenja + shuma + " lekë").queue();
        }
        else if (command.matches("^'rob @\\w+$")) {
            String targetName = command.split("@")[1];
            User target = null;
            try {
                for (var member : event.getGuild().getMembers()) {
                    if (member.getUser().getName().equalsIgnoreCase(targetName)) {
                        target = member.getUser();
                        break;
                    }
                }
            } catch (Exception e) {
                event.getChannel().sendMessage("❌ Nuk mund të gjej përdoruesin.").queue();
                return;
            }
            if (target == null || target.getId().equals(userId)) {
                event.getChannel().sendMessage("❌ Nuk mund të grabisësh veten.").queue();
                return;
            }
            int shuma = random.nextInt(300) + 50;
            boolean suksesi = random.nextDouble() > 0.4;
            int balanceViktima = db.merrBalance(target.getId());
            if (suksesi && balanceViktima >= shuma) {
                db.zbritPara(target.getId(), shuma);
                db.shtoPara(userId, shuma);
                event.getChannel().sendMessage("🔫 **" + username + "** grabisi @" + 
                        targetName + " dhe mori " + shuma + " lekë! 💰").queue();
            } else {
                db.zbritPara(userId, 200);
                event.getChannel().sendMessage("🚔 Grabistja dështoi! **" + username + 
                        "** u kap dhe pagoi 200 lekë gjobë.").queue();
            }
        }
        else if (command.equals("'bal")) {
            int balance = db.merrBalance(userId);
            event.getChannel().sendMessage("💰 **" + username + "** ka " + balance + 
                    " lekë në xhep.\n📡 Ping: " + jda.getGatewayPing() + " ms").queue();
        }
        else if (command.equals("'dep")) {
            long depozitaFundit = db.merrDepozitaFundit(userId);
            long tani = System.currentTimeMillis();
            long diferenca = tani - depozitaFundit;
            if (diferenca < 86400000) {
                long oreMbetur = (86400000 - diferenca) / 3600000;
                event.getChannel().sendMessage("⏳ Ke marrë bonusin sot. Provo pas " + oreMbetur + " orësh!").queue();
            } else {
                db.shtoPara(userId, 100);
                db.updateDepozita(userId, tani);
                event.getChannel().sendMessage("🏦 Depozitove 100 lekë bonus ditor! 💰").queue();
            }
        }
        else if (command.equals("'with")) {
            db.zbritPara(userId, 100);
            int balance = db.merrBalance(userId);
            event.getChannel().sendMessage("💸 Tërhoqe 50 lekë por taksohesh 50 lekë gjobë.\n" +
                    "Gjendja e re: **" + balance + "** lekë").queue();
        }
        else if (command.startsWith("/add_money") && userId.equals(ADMIN_ID)) {
            String[] parts = command.split(" ");
            if (parts.length == 3 && parts[1].startsWith("@")) {
                String targetUser = parts[1].substring(1);
                try {
                    int amount = Integer.parseInt(parts[2]);
                    event.getChannel().sendMessage("✅ I shtove @" + targetUser + " " + amount + " lekë.").queue();
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage("❌ Shuma duhet të jetë numër!").queue();
                }
            }
        }
        else if (command.equals("'help")) {
            event.getChannel().sendMessage(
                "🇦🇱 **SHQIPBOT - Boti Ekonomik Shqiptar**\n\n" +
                "`'work` - Puno në Shqipëri (50-120 lekë)\n" +
                "`'slut` - Shërbime të dyshimta (-300 deri +500)\n" +
                "`'crime` - Punë kriminale (-700 deri +1200)\n" +
                "`'rob @user` - Grabiste dikë (50-350 lekë)\n" +
                "`'bal` - Shiko sa lekë ke\n" +
                "`'dep` - Bonus ditor 100 lekë\n" +
                "`'with` - Tërhiq para (humb 100 lekë)\n\n" +
                "💓 Ping: " + jda.getGatewayPing() + " ms\n" +
                "📢 Bërë me 🇦🇱 për Shqipërinë"
            ).queue();
        }
        else if (command.equals("'start")) {
            event.getChannel().sendMessage(
                "🇦🇱 **MIRË SE VJEN NË SHQIPBOT!**\n\n" +
                "Boti ekonomik 100% në gjuhën shqipe 🇦🇱\n\n" +
                "Provo: `'work`, `'bal`, `'help`\n" +
                "💰 Fillo me 100 lekë në xhep!"
            ).queue();
        }
    }
}