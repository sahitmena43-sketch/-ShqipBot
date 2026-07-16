package com.shqipbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.awt.Color;
import java.time.Instant;
import java.util.*;

public class ShqipBot extends ListenerAdapter {
    
    private Database db;
    private Random random;
    private JDA jda;
    private String lastCommand = "";
    private long lastCommandTime = 0;
    
    private final String ADMIN_ID = "781121784526929950";
    
    // ==================== 20 PUNË ====================
    private final String[][] punet = {
        {"Shitës Euro në Rrugë", "Shite Euro false por turistët i deshën", "1500"},
        {"Kontrabandist TVSH", "Fshehe TVSH-në në tavan por ra dhe u zbulove", "2000"},
        {"Mësues i Gjuhës Shqipe", "Mësove 'qe' dhe 'ka' por nxënësit thanë 'ka qe'", "1000"},
        {"Menaxher Nëntoke", "Menaxhove biznesin nga bodrumi por policia erdhi", "1800"},
        {"Falsifikues Diplomash", "Bëre diplomë për kafshën por e mori seriozisht", "2500"},
        {"Përkthyes në Gjyq", "Përktheve nga shqip në shqip por gjyqtari u hutua", "1200"},
        {"Shitës Kravatash", "Shite kravata me ngjyra të çmendura por të gjithë i blenë", "900"},
        {"Konsulent Politik", "Këshillove politikanin por ai bëri të kundërtën", "3000"},
        {"Gazetar Sporti", "Shkruajti lajm të rremë por u besuan", "1300"},
        {"Agjent Imobiliash", "Shite banesë në Tiranë por ishte në Mars", "2500"},
        {"Rregullues Shorti", "Rregullove shortin e Kupës por u zbulove", "4000"},
        {"Kameraman Dasmash", "Xhirove dasmën por ngatërrove nusen me kunatën", "1100"},
        {"Shitës Teli", "Shite tel të ndryshkur si të ri", "700"},
        {"Noter i Rrugës", "Noterove dokumente pa vizë por i deshën", "1600"},
        {"Eksportues Vaji", "Eksportove vaj ulliri por ishte vaj makine", "2200"},
        {"Trajner Futbolli", "Trajnovat skuadrën por ata luanin basketboll", "1900"},
        {"Shitës Uji", "Shite ujë të paster por ishte nga lumi", "800"},
        {"Menaxher Kulture", "Organizove koncert por këngëtari ishte i dehur", "1400"},
        {"Krijues Sloganesh", "Krijove slogan por e përdori kundërshtari", "1700"},
        {"Punonjës i BSH", "Punoje në BSH por i ngatërrove statistikat", "2100"}
    };
    
    // ==================== SLUT ====================
    private final String[][] slutSukses = {
        {"Tregove vallëzim te Pink Floyd", "2000"},
        {"Këndove në dasmë me fustan të shkurtër", "3500"},
        {"Bëre live në TikTok me veshje provokuese", "5000"},
        {"Bëre foto për OnlyFans në Kala", "4500"},
        {"Kërcen në 'Puls' në mes të natës", "4000"},
        {"Ofrove shërbim për shefin e plazhit", "3000"},
        {"Shite trupin në 'New York' për 24 orë", "2500"},
        {"Bëre striptiz në bachelorette party", "3800"},
        {"Fotove veten me Berishën", "4200"},
        {"Shfaqve trupin në makinë te Liqeni", "1500"}
    };
    
    private final String[][] slutDeshtim = {
        {"Ofrove masazh por klienti ishte inspektor", "-1000"},
        {"Të kapën me klient në makinë te Liqeni", "-2500"},
        {"Shefës i ofrove 'shërbim special' për të mbajtur punën", "-3000"},
        {"Ofrove taksi me 'shërbim shtesë' por ishte polic", "-1500"},
        {"Bëre foto për OnlyFans por ishte policia", "-2000"},
        {"Tregove trupin në plazh dhe të arrestuan", "-1800"},
        {"Këndove në dasmë por ishte e fejuara", "-2200"},
        {"Ofrove shërbim në hotel por ishte nusja", "-2800"},
        {"Bëre striptiz në familje", "-3500"},
        {"Shite trupin por ishte vetëm një shaka", "-1300"}
    };
    
    // ==================== CRIME ====================
    private final String[][] crimeSukses = {
        {"Shite CD të paluajtshme te 'Rruga e Kavajës'", "5000"},
        {"Kontrabandove cigare nga Mali i Zi", "10000"},
        {"Shite narkotikë te shkolla 'Fan Noli'", "8000"},
        {"Vodhe bakër nga Kantieri", "6000"},
        {"Falsifikove Euro në shtëpi", "12000"},
        {"Shite armë për bandën e Durrësit", "9000"},
        {"U fute ilegalisht në Greqi", "4000"},
        {"Shite patentë të rreme", "7500"},
        {"Vodhe makinë në Tiranë", "6500"},
        {"Grabitëse dyqani në Vlorë", "5500"}
    };
    
    private final String[][] crimeDeshtim = {
        {"U përpoqe të korruptosh policin", "-3000"},
        {"Të kapën me armë pa leje te 'Farka'", "-5000"},
        {"Grabitëse banke por ra alarmi", "-7000"},
        {"Shite narkotikë por ishte polic në civil", "-4000"},
        {"Kontrabandove duhan dhe të kapën në kufi", "-6000"},
        {"U përpoqe të vjedhësh bankë por u mbyll", "-3500"},
        {"Shite armë por ishin kuti për fëmijë", "-2500"},
        {"Falsifikove dokumenta dhe të zbuluan", "-4500"},
        {"U fute ilegalisht dhe të kthyen mbrapsht", "-2000"},
        {"Vodhe makinë por ishte e policisë", "-5500"}
    };
    
    // ==================== KONSTRUKTORI ====================
    public ShqipBot(String token) throws Exception {
        this.db = new Database();
        this.random = new Random();
        
        System.out.println("🔌 Duke u lidhur me Discord-in...");
        
        this.jda = JDABuilder.createLight(token, 
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("'work për lekë 🇦🇱 | ShqipBot"))
                .addEventListeners(this)
                .build();
        
        jda.awaitReady();
        System.out.println("✅ ShqipBot u lidh! Ping: " + jda.getGatewayPing() + " ms");
    }
    
    // ==================== METODA KRYESORE ====================
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        String msgContent = event.getMessage().getContentRaw();
        if (!msgContent.startsWith("'")) return;
        
        long now = System.currentTimeMillis();
        if (msgContent.equals(lastCommand) && (now - lastCommandTime) < 1000) {
            return;
        }
        lastCommand = msgContent;
        lastCommandTime = now;
        
        String userId = event.getAuthor().getId();
        String userName = event.getAuthor().getName();
        String command = msgContent.toLowerCase();
        
        System.out.println("📨 " + userName + ": " + command);
        
        db.krijoPerdorues(userId, userName);
        
        // ==================== 'work ====================
        if (command.equals("'work")) {
            int index = random.nextInt(punet.length);
            int fitimi = Integer.parseInt(punet[index][2]);
            db.shtoPara(userId, fitimi);
            db.updateWeeklyEarnings(userId, fitimi);
            db.updateMonthlyEarnings(userId, fitimi);
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("🇦🇱 PUNË ALLA SHQIPTARE 🇦🇱")
                .setDescription("**" + event.getAuthor().getName() + "** punoi si **" + punet[index][0] + "**")
                .addField("📝 Përshkrimi", punet[index][1], false)
                .addField("💰 Fitimi", "+" + fitimi + " lekë", true)
                .setFooter("ShqipBot © 2026", null)
                .setTimestamp(Instant.now());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'slut ====================
        else if (command.equals("'slut")) {
            boolean sukses = random.nextBoolean();
            String[] result;
            String status;
            Color color;
            
            if (sukses) {
                result = slutSukses[random.nextInt(slutSukses.length)];
                status = "✅ SUKSES";
                color = Color.GREEN;
            } else {
                result = slutDeshtim[random.nextInt(slutDeshtim.length)];
                status = "❌ DËSHTIM";
                color = Color.RED;
            }
            
            int shuma = Integer.parseInt(result[1]);
            db.shtoPara(userId, shuma);
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(color)
                .setTitle("🍑 SHËRBIME TË DYSHIMTA 🍑")
                .setDescription("**" + event.getAuthor().getName() + "** " + result[0])
                .addField("📊 Statusi", status, true)
                .addField("💰 Rezultati", (shuma > 0 ? "+" : "") + shuma + " lekë", true)
                .setFooter("ShqipBot © 2026", null)
                .setTimestamp(Instant.now());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'crime ====================
        else if (command.equals("'crime")) {
            boolean sukses = random.nextBoolean();
            String[] result;
            String status;
            Color color;
            
            if (sukses) {
                result = crimeSukses[random.nextInt(crimeSukses.length)];
                status = "✅ SUKSES";
                color = Color.GREEN;
            } else {
                result = crimeDeshtim[random.nextInt(crimeDeshtim.length)];
                status = "❌ DËSHTIM";
                color = Color.RED;
            }
            
            int shuma = Integer.parseInt(result[1]);
            db.shtoPara(userId, shuma);
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(color)
                .setTitle("😈 PUNË KRIMINALE 😈")
                .setDescription("**" + event.getAuthor().getName() + "** " + result[0])
                .addField("📊 Statusi", status, true)
                .addField("💰 Rezultati", (shuma > 0 ? "+" : "") + shuma + " lekë", true)
                .setFooter("ShqipBot © 2026", null)
                .setTimestamp(Instant.now());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'rob @user ====================
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
            
            int shuma = random.nextInt(3000) + 500;
            boolean suksesi = random.nextDouble() > 0.4;
            int balanceViktima = db.merrBalance(target.getId());
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(suksesi ? Color.GREEN : Color.RED)
                .setTitle("🔫 GRABITJE 🔫");
            
            if (suksesi && balanceViktima >= shuma) {
                db.zbritPara(target.getId(), shuma);
                db.shtoPara(userId, shuma);
                embed.setDescription("**" + event.getAuthor().getName() + "** grabiti @" + targetName)
                    .addField("💰 Shuma", "+" + shuma + " lekë", true)
                    .addField("🎯 Viktima", "@" + targetName, true)
                    .addField("📊 Statusi", "✅ SUKSES", true);
            } else {
                db.zbritPara(userId, 2000);
                embed.setDescription("**" + event.getAuthor().getName() + "** u kap duke grabitur @" + targetName)
                    .addField("💰 Humbja", "-2000 lekë", true)
                    .addField("🚔 Statusi", "❌ DËSHTIM", true);
            }
            
            embed.setFooter("ShqipBot © 2026", null)
                .setTimestamp(Instant.now());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'bal ====================
        else if (command.equals("'bal")) {
            com.shqipbot.User userData = db.merrPerdorues(userId);
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.CYAN)
                .setTitle("💰 GJENDJA FINANCIARE 💰")
                .setDescription("**" + event.getAuthor().getName() + "**")
                .addField("💵 Në xhep", userData.getBalance() + " lekë", true)
                .addField("🏦 Në bankë", userData.getBank() + " lekë", true)
                .addField("💳 Gjithsej", (userData.getBalance() + userData.getBank()) + " lekë", true)
                .addField("📡 Ping", jda.getGatewayPing() + " ms", true)
                .setFooter("ShqipBot © 2026 | Paratë në bankë janë të sigurta!", null)
                .setTimestamp(Instant.now());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'dep ====================
        else if (command.equals("'dep")) {
            long depozitaFundit = db.merrDepozitaFundit(userId);
            long tani = System.currentTimeMillis();
            long diferenca = tani - depozitaFundit;
            long dite = 24 * 60 * 60 * 1000;
            
            com.shqipbot.User userData = db.merrPerdorues(userId);
            int balance = userData.getBalance();
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle("🏦 DEPOZITO NË BANKË 🏦");
            
            if (diferenca < dite) {
                long oreMbetur = (dite - diferenca) / (60 * 60 * 1000);
                embed.setDescription("⏳ Ke marrë bonusin ditor sot!")
                    .addField("⏰ Provo pas", oreMbetur + " orësh", true);
            } else {
                db.shtoBank(userId, 1000);
                db.updateDepozita(userId, tani);
                
                if (balance > 0) {
                    db.shtoBank(userId, balance);
                    db.zbritPara(userId, balance);
                }
                
                com.shqipbot.User updatedUser = db.merrPerdorues(userId);
                embed.setDescription("🎉 **" + event.getAuthor().getName() + "** depozitoi **" + 
                        (balance > 0 ? balance + " lekë + " : "") + "1000 lekë** bonus ditor në bankë!")
                    .setColor(Color.GREEN)
                    .addField("💰 Në xhep", updatedUser.getBalance() + " lekë", true)
                    .addField("🏦 Në bankë", updatedUser.getBank() + " lekë", true)
                    .addField("💳 Gjithsej", (updatedUser.getBalance() + updatedUser.getBank()) + " lekë", true);
            }
            
            embed.setFooter("ShqipBot © 2026 | Bonusi vjen çdo 24 orë!", null)
                .setTimestamp(Instant.now());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'with ====================
        else if (command.equals("'with")) {
            com.shqipbot.User userData = db.merrPerdorues(userId);
            int bank = userData.getBank();
            
            if (bank < 1000) {
                event.getChannel().sendMessage("❌ Nuk ke mjaftueshëm para në bankë! Ke " + bank + " lekë.").queue();
                return;
            }
            
            int shuma = 1000;
            db.zbritBank(userId, shuma);
            db.shtoPara(userId, shuma);
            
            com.shqipbot.User updatedUser = db.merrPerdorues(userId);
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("💸 TËRHIQJE NGA BANKA 💸")
                .setDescription("**" + event.getAuthor().getName() + "** tërhoqi **1000 lekë** nga banka!")
                .addField("💰 Në xhep", updatedUser.getBalance() + " lekë", true)
                .addField("🏦 Në bankë", updatedUser.getBank() + " lekë", true)
                .setFooter("ShqipBot © 2026 | Banka ka gjithmonë para!", null)
                .setTimestamp(Instant.now());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'lb ====================
        else if (command.equals("'lb")) {
            List<com.shqipbot.User> topUsers = db.getTopBalances();
            
            if (topUsers.isEmpty()) {
                event.getChannel().sendMessage("❌ Nuk ka përdorues në databazë.").queue();
                return;
            }
            
            StringBuilder msgBuilder = new StringBuilder("🏆 **LEADERBOARD GLOBAL** 🏆\n");
            msgBuilder.append("(Bazuar në paratë në xhep)\n");
            msgBuilder.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            int rank = 1;
            for (com.shqipbot.User userData : topUsers) {
                String medal = "";
                if (rank == 1) medal = "🥇 ";
                else if (rank == 2) medal = "🥈 ";
                else if (rank == 3) medal = "🥉 ";
                else medal = rank + ". ";
                
                String username = userData.getUsername();
                if (username == null || username.isEmpty()) {
                    username = "Përdorues i panjohur";
                }
                
                msgBuilder.append(medal).append("**").append(username).append("** - ")
                       .append(userData.getBalance()).append(" lekë\n");
                rank++;
            }
            
            event.getChannel().sendMessage(msgBuilder.toString()).queue();
        }
        
        // ==================== 'weekly ====================
        else if (command.equals("'weekly")) {
            List<com.shqipbot.User> topUsers = db.getWeeklyTop();
            
            if (topUsers.isEmpty()) {
                event.getChannel().sendMessage("❌ Nuk ka fitime këtë javë.").queue();
                return;
            }
            
            StringBuilder msgBuilder = new StringBuilder("📅 **LEADERBOARD JAVOR** 📅\n");
            msgBuilder.append("(Fitimet e kësaj jave)\n");
            msgBuilder.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            int rank = 1;
            for (com.shqipbot.User userData : topUsers) {
                String medal = "";
                if (rank == 1) medal = "🥇 ";
                else if (rank == 2) medal = "🥈 ";
                else if (rank == 3) medal = "🥉 ";
                else medal = rank + ". ";
                
                String username = userData.getUsername();
                if (username == null || username.isEmpty()) {
                    username = "Përdorues i panjohur";
                }
                
                msgBuilder.append(medal).append("**").append(username).append("** - ")
                       .append(userData.getBalance()).append(" lekë\n");
                rank++;
            }
            
            event.getChannel().sendMessage(msgBuilder.toString()).queue();
        }
        
        // ==================== 'monthly ====================
        else if (command.equals("'monthly")) {
            List<com.shqipbot.User> topUsers = db.getMonthlyTop();
            
            if (topUsers.isEmpty()) {
                event.getChannel().sendMessage("❌ Nuk ka fitime këtë muaj.").queue();
                return;
            }
            
            StringBuilder msgBuilder = new StringBuilder("📆 **LEADERBOARD MUJOR** 📆\n");
            msgBuilder.append("(Fitimet e këtij muaji)\n");
            msgBuilder.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            int rank = 1;
            for (com.shqipbot.User userData : topUsers) {
                String medal = "";
                if (rank == 1) medal = "🥇 ";
                else if (rank == 2) medal = "🥈 ";
                else if (rank == 3) medal = "🥉 ";
                else medal = rank + ". ";
                
                String username = userData.getUsername();
                if (username == null || username.isEmpty()) {
                    username = "Përdorues i panjohur";
                }
                
                msgBuilder.append(medal).append("**").append(username).append("** - ")
                       .append(userData.getBalance()).append(" lekë\n");
                rank++;
            }
            
            event.getChannel().sendMessage(msgBuilder.toString()).queue();
        }
        
        // ==================== /add_money ====================
        else if (command.startsWith("/add_money") && userId.equals(ADMIN_ID)) {
            String[] parts = command.split(" ");
            if (parts.length == 3 && parts[1].startsWith("@")) {
                String targetUser = parts[1].substring(1);
                try {
                    int amount = Integer.parseInt(parts[2]);
                    db.shtoPara(targetUser, amount);
                    event.getChannel().sendMessage("✅ I shtove @" + targetUser + " " + amount + " lekë.").queue();
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage("❌ Shuma duhet të jetë numër!").queue();
                }
            } else {
                event.getChannel().sendMessage("❌ Përdorimi: `/add_money @user 1000`").queue();
            }
        }
        
        // ==================== 'help ====================
        else if (command.equals("'help")) {
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("🇦🇱 SHQIPBOT - Lista e Komandave 🇦🇱")
                .setDescription("Boti ekonomik 100% në gjuhën shqipe!")
                .addField("💼 **PUNË**", "`'work` - Puno në Shqipëri (20 punë alla shqiptare)", false)
                .addField("🍑 **SHËRBIME**", "`'slut` - Shërbime të dyshimta", false)
                .addField("😈 **KRIM**", "`'crime` - Punë kriminale", false)
                .addField("🔫 **GRABITJE**", "`'rob @user` - Grabit dikë", false)
                .addField("💰 **FINANCA**", 
                    "`'bal` - Gjendja\n" +
                    "`'dep` - Depozito në bankë + bonus ditor\n" +
                    "`'with` - Tërhiq nga banka", false)
                .addField("🏆 **LEADERBOARD**", 
                    "`'lb` - Leaderboard global\n" +
                    "`'weekly` - Leaderboard javor\n" +
                    "`'monthly` - Leaderboard mujor", false)
                .setFooter("ShqipBot © 2026 | Bëhu i pasur në stilin shqiptar!", null)
                .setTimestamp(Instant.now());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'start ====================
        else if (command.equals("'start") || command.equals("/start")) {
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("🇦🇱 MIRË SE VJEN NË SHQIPBOT! 🇦🇱")
                .setDescription("Boti ekonomik **100%** në gjuhën shqipe!")
                .addField("💰 Fillo me", "**1000 lekë** në xhep!", true)
                .addField("📋 Provo", "`'work`, `'bal`, `'help`", true)
                .addField("🏆 Leaderboard", "`'lb`, `'weekly`, `'monthly`", true)
                .addField("🏦 Banka", "`'dep` - Depozito + bonus ditor", true)
                .setFooter("ShqipBot © 2026 | Bëhu i pasur!", null)
                .setTimestamp(Instant.now());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }
}