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
import java.util.*;

public class ShqipBot extends ListenerAdapter {
    
    private Database db;
    private Random random;
    private JDA jda;
    private String lastCommand = "";
    private long lastCommandTime = 0;
    
    // Sistem bastesh
    private Map<String, Bet> bets = new HashMap<>();
    private Map<String, List<Bet>> userBets = new HashMap<>();
    
    private final String ADMIN_ID = "123456789012345678"; // Zëvendëso me ID-në tënde!
    
    // ==================== 20 PUNË ALLA SHQIPTARE ====================
    private final String[][] punet = {
        {"Shitës Euro në Rrugë", "Shite Euro false por turistët i deshën", "150"},
        {"Kontrabandist TVSH", "Fshehe TVSH-në në tavan por ra dhe u zbulove", "200"},
        {"Mësues i Gjuhës Shqipe", "Mësove 'qe' dhe 'ka' por nxënësit thanë 'ka qe'", "100"},
        {"Menaxher Nëntoke", "Menaxhove biznesin nga bodrumi por policia erdhi", "180"},
        {"Falsifikues Diplomash", "Bëre diplomë për kafshën por e mori seriozisht", "250"},
        {"Përkthyes në Gjyq", "Përktheve nga shqip në shqip por gjyqtari u hutua", "120"},
        {"Shitës Kravatash", "Shite kravata me ngjyra të çmendura por të gjithë i blenë", "90"},
        {"Konsulent Politik", "Këshillove politikanin por ai bëri të kundërtën", "300"},
        {"Gazetar Sporti", "Shkruajti lajm të rremë por u besuan", "130"},
        {"Agjent Imobiliash", "Shite banesë në Tiranë por ishte në Mars", "250"},
        {"Rregullues Shorti", "Rregullove shortin e Kupës por u zbulove", "400"},
        {"Kameraman Dasmash", "Xhirove dasmën por ngatërrove nusen me kunatën", "110"},
        {"Shitës Teli", "Shite tel të ndryshkur si të ri", "70"},
        {"Noter i Rrugës", "Noterove dokumente pa vizë por i deshën", "160"},
        {"Eksportues Vaji", "Eksportove vaj ulliri por ishte vaj makine", "220"},
        {"Trajner Futbolli", "Trajnovat skuadrën por ata luanin basketboll", "190"},
        {"Shitës Uji", "Shite ujë të paster por ishte nga lumi", "80"},
        {"Menaxher Kulture", "Organizove koncert por këngëtari ishte i dehur", "140"},
        {"Krijues Sloganesh", "Krijove slogan por e përdori kundërshtari", "170"},
        {"Punonjës i BSH", "Punoje në BSH por i ngatërrove statistikat", "210"}
    };
    
    // ==================== SLUT - 10 SUKSESE + 10 DËSHTIME ====================
    private final String[][] slutSukses = {
        {"Tregove vallëzim te Pink Floyd", "200"},
        {"Këndove në dasmë me fustan të shkurtër", "350"},
        {"Bëre live në TikTok me veshje provokuese", "500"},
        {"Bëre foto për OnlyFans në Kala", "450"},
        {"Kërcen në 'Puls' në mes të natës", "400"},
        {"Ofrove shërbim për shefin e plazhit", "300"},
        {"Shite trupin në 'New York' për 24 orë", "250"},
        {"Bëre striptiz në bachelorette party", "380"},
        {"Fotove veten me Berishën", "420"},
        {"Shfaqve trupin në makinë te Liqeni", "150"}
    };
    
    private final String[][] slutDeshtim = {
        {"Ofrove masazh por klienti ishte inspektor", "-100"},
        {"Të kapën me klient në makinë te Liqeni", "-250"},
        {"Shefës i ofrove 'shërbim special' për të mbajtur punën", "-300"},
        {"Ofrove taksi me 'shërbim shtesë' por ishte polic", "-150"},
        {"Bëre foto për OnlyFans por ishte policia", "-200"},
        {"Tregove trupin në plazh dhe të arrestuan", "-180"},
        {"Këndove në dasmë por ishte e fejuara", "-220"},
        {"Ofrove shërbim në hotel por ishte nusja", "-280"},
        {"Bëre striptiz në familje", "-350"},
        {"Shite trupin por ishte vetëm një shaka", "-130"}
    };
    
    // ==================== CRIME - 10 SUKSESE + 10 DËSHTIME ====================
    private final String[][] crimeSukses = {
        {"Shite CD të paluajtshme te 'Rruga e Kavajës'", "500"},
        {"Kontrabandove cigare nga Mali i Zi", "1000"},
        {"Shite narkotikë te shkolla 'Fan Noli'", "800"},
        {"Vodhe bakër nga Kantieri", "600"},
        {"Falsifikove Euro në shtëpi", "1200"},
        {"Shite armë për bandën e Durrësit", "900"},
        {"U fute ilegalisht në Greqi", "400"},
        {"Shite patentë të rreme", "750"},
        {"Vodhe makinë në Tiranë", "650"},
        {"Grabitëse dyqani në Vlorë", "550"}
    };
    
    private final String[][] crimeDeshtim = {
        {"U përpoqe të korruptosh policin", "-300"},
        {"Të kapën me armë pa leje te 'Farka'", "-500"},
        {"Grabitëse banke por ra alarmi", "-700"},
        {"Shite narkotikë por ishte polic në civil", "-400"},
        {"Kontrabandove duhan dhe të kapën në kufi", "-600"},
        {"U përpoqe të vjedhësh bankë por u mbyll", "-350"},
        {"Shite armë por ishin kuti për fëmijë", "-250"},
        {"Falsifikove dokumenta dhe të zbuluan", "-450"},
        {"U fute ilegalisht dhe të kthyen mbrapsht", "-200"},
        {"Vodhe makinë por ishte e policisë", "-550"}
    };
    
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
        
        startBetScheduler();
    }
    
    private void startBetScheduler() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkExpiredBets();
            }
        }, 0, 60000);
    }
    
    private void checkExpiredBets() {
        long now = System.currentTimeMillis();
        List<String> toRemove = new ArrayList<>();
        
        for (Map.Entry<String, Bet> entry : bets.entrySet()) {
            Bet bet = entry.getValue();
            if (now > bet.expiryTime) {
                toRemove.add(entry.getKey());
            }
        }
        
        for (String betId : toRemove) {
            bets.remove(betId);
            for (List<Bet> userBetList : userBets.values()) {
                userBetList.removeIf(b -> b.betId.equals(betId));
            }
        }
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
        
        // ==================== 'work ====================
        if (command.equals("'work")) {
            int index = random.nextInt(punet.length);
            int fitimi = Integer.parseInt(punet[index][2]);
            db.shtoPara(userId, fitimi);
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("🇦🇱 PUNË ALLA SHQIPTARE 🇦🇱")
                .setDescription("**" + event.getAuthor().getName() + "** punoi si **" + punet[index][0] + "**")
                .addField("📝 Përshkrimi", punet[index][1], false)
                .addField("💰 Fitimi", "+" + fitimi + " lekë", true)
                .setFooter("ShqipBot © 2026", null)
                .setTimestamp(new Date().toInstant());
            
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
                .setTimestamp(new Date().toInstant());
            
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
                .setTimestamp(new Date().toInstant());
            
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
            
            int shuma = random.nextInt(300) + 50;
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
                db.zbritPara(userId, 200);
                embed.setDescription("**" + event.getAuthor().getName() + "** u kap duke grabisur @" + targetName)
                    .addField("💰 Humbja", "-200 lekë", true)
                    .addField("🚔 Statusi", "❌ DËSHTIM", true);
            }
            
            embed.setFooter("ShqipBot © 2026", null)
                .setTimestamp(new Date().toInstant());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'bal ====================
        else if (command.equals("'bal")) {
            int balance = db.merrBalance(userId);
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.CYAN)
                .setTitle("💰 GJENDJA FINANCIARE 💰")
                .setDescription("**" + event.getAuthor().getName() + "** ka **" + balance + "** lekë në xhep!")
                .addField("📡 Ping", jda.getGatewayPing() + " ms", true)
                .setFooter("ShqipBot © 2026", null)
                .setTimestamp(new Date().toInstant());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'dep ====================
        else if (command.equals("'dep")) {
            long depozitaFundit = db.merrDepozitaFundit(userId);
            long tani = System.currentTimeMillis();
            long diferenca = tani - depozitaFundit;
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle("🏦 DEPOZITO BONUS DITOR 🏦");
            
            if (diferenca < 86400000) {
                long oreMbetur = (86400000 - diferenca) / 3600000;
                embed.setDescription("⏳ Ke marrë bonusin sot!")
                    .addField("⏰ Provo pas", oreMbetur + " orësh", true);
            } else {
                db.shtoPara(userId, 100);
                db.updateDepozita(userId, tani);
                embed.setDescription("🎉 **" + event.getAuthor().getName() + "** depozitoi **100 lekë** bonus ditor!")
                    .setColor(Color.GREEN)
                    .addField("💰 Bonus", "+100 lekë", true);
            }
            
            embed.setFooter("ShqipBot © 2026", null)
                .setTimestamp(new Date().toInstant());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'with ====================
        else if (command.equals("'with")) {
            db.zbritPara(userId, 100);
            int balance = db.merrBalance(userId);
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle("💸 TËRHIQJE PARASH 💸")
                .setDescription("**" + event.getAuthor().getName() + "** tërhoqi 50 lekë por taksohesh 50 lekë!")
                .addField("💰 Gjendja e re", balance + " lekë", true)
                .addField("💸 Takse", "-100 lekë", true)
                .setFooter("ShqipBot © 2026", null)
                .setTimestamp(new Date().toInstant());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'bet ====================
        else if (command.startsWith("'bet")) {
            handleBetCommand(event, message, userId, username);
        }
        
        // ==================== 'match ====================
        else if (command.startsWith("'match") && userId.equals(ADMIN_ID)) {
            String[] parts = command.split(" ");
            if (parts.length >= 4) {
                String team1 = parts[1];
                String team2 = parts[2];
                String time = parts[3];
                announceMatch(event, team1, team2, time);
            } else {
                event.getChannel().sendMessage("❌ Përdorimi: `'match Skuadra1 Skuadra2 20:00`").queue();
            }
        }
        
        // ==================== /add_money ====================
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
        
        // ==================== 'help ====================
        else if (command.equals("'help")) {
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("🇦🇱 SHQIPBOT - Lista e Komandave 🇦🇱")
                .setDescription("Boti ekonomik 100% në gjuhën shqipe!")
                .addField("💼 **PUNË**", "`'work` - Puno në Shqipëri (20 punë alla shqiptare)", false)
                .addField("🍑 **SHËRBIME**", "`'slut` - Shërbime të dyshimta", false)
                .addField("😈 **KRIM**", "`'crime` - Punë kriminale", false)
                .addField("🔫 **GRABITJE**", "`'rob @user` - Grabite dikë", false)
                .addField("💰 **FINANCA**", "`'bal` - Gjendja\n`'dep` - Bonus ditor\n`'with` - Tërhiq", false)
                .addField("⚽ **BASTE**", 
                    "`'bet 1 100 Skuadra1` - Bast për fitues Skuadra 1 (2.0x)\n" +
                    "`'bet 2 100 Skuadra2` - Bast për fitues Skuadra 2 (2.0x)\n" +
                    "`'bet 3 100 Draw` - Bast për barazim (4.0x)\n" +
                    "`'match Skuadra1 Skuadra2 20:00` - Krijon ndeshje (Admin)", false)
                .setFooter("ShqipBot © 2026 | Bëhu i pasur në stilin shqiptar!", null)
                .setTimestamp(new Date().toInstant());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        
        // ==================== 'start ====================
        else if (command.equals("'start") || command.equals("/start")) {
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("🇦🇱 MIRË SE VJEN NË SHQIPBOT! 🇦🇱")
                .setDescription("Boti ekonomik **100%** në gjuhën shqipe!")
                .addField("💰 Fillo me", "**100 lekë** në xhep!", true)
                .addField("📋 Provo", "`'work`, `'bal`, `'help`", true)
                .addField("⚽ Bastet", "`'bet 1 100 Skënderbeu`", true)
                .setFooter("ShqipBot © 2026 | Bëhu i pasur!", null)
                .setTimestamp(new Date().toInstant());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }
    
    // ==================== HANDLER PËR BASTE ====================
    private void handleBetCommand(MessageReceivedEvent event, String message, String userId, String username) {
        String[] parts = message.split(" ");
        if (parts.length < 4) {
            event.getChannel().sendMessage("❌ Përdorimi: `'bet 1 100 Skuadra` (1=Fitues Skuadra 1, 2=Fitues Skuadra 2, 3=Barazim)").queue();
            return;
        }
        
        try {
            int tipi = Integer.parseInt(parts[1]);
            int amount = Integer.parseInt(parts[2]);
            
            StringBuilder teamName = new StringBuilder();
            for (int i = 3; i < parts.length; i++) {
                teamName.append(parts[i]).append(" ");
            }
            String team = teamName.toString().trim();
            
            if (tipi < 1 || tipi > 3) {
                event.getChannel().sendMessage("❌ Zgjidh: 1=Fitues Skuadra 1, 2=Fitues Skuadra 2, 3=Barazim").queue();
                return;
            }
            
            int balance = db.merrBalance(userId);
            if (amount > balance) {
                event.getChannel().sendMessage("❌ Nuk ke mjaftueshëm lekë! Ke " + balance + " lekë.").queue();
                return;
            }
            
            if (amount <= 0) {
                event.getChannel().sendMessage("❌ Shuma duhet të jetë pozitive!").queue();
                return;
            }
            
            db.zbritPara(userId, amount);
            
            String[] tipet = {"🏆 Fitues Skuadra 1", "🏆 Fitues Skuadra 2", "🤝 Barazim"};
            double[] koeficientet = {2.0, 2.0, 4.0};
            
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle("⚽ BAST FUTBOLLI ⚽")
                .setDescription("**" + username + "** vendosi një bast!")
                .addField("💰 Shuma", amount + " lekë", true)
                .addField("🎯 Lloji", tipet[tipi - 1], true)
                .addField("⚽ Skuadra", team, true)
                .addField("📊 Koeficienti", koeficientet[tipi - 1] + "x", true)
                .addField("🏆 Fitimi i mundshëm", (int)(amount * koeficientet[tipi - 1]) + " lekë", true)
                .setFooter("ShqipBot © 2026 | Fat të mirë!", null)
                .setTimestamp(new Date().toInstant());
            
            String betId = UUID.randomUUID().toString();
            Bet bet = new Bet(userId, username, team, tipi, amount, koeficientet[tipi - 1], System.currentTimeMillis() + 7200000);
            bets.put(betId, bet);
            userBets.computeIfAbsent(userId, k -> new ArrayList<>()).add(bet);
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("❌ Format i gabuar! Përdor: `'bet 1 100 Skënderbeu`").queue();
        }
    }
    
    // ==================== KLASA BET ====================
    private class Bet {
        String userId;
        String username;
        String team;
        int type;
        int amount;
        double coefficient;
        long expiryTime;
        String betId;
        boolean isSettled = false;
        
        Bet(String userId, String username, String team, int type, int amount, double coefficient, long expiryTime) {
            this.userId = userId;
            this.username = username;
            this.team = team;
            this.type = type;
            this.amount = amount;
            this.coefficient = coefficient;
            this.expiryTime = expiryTime;
            this.betId = UUID.randomUUID().toString();
        }
    }
    
    // ==================== METODA PËR NDESHJET ====================
    private void announceMatch(MessageReceivedEvent event, String team1, String team2, String time) {
        event.getChannel().sendMessage("@everyone 📢 **" + team1 + "** vs **" + team2 + "** do të zhvillojnë ndeshjen në **" + time + "** !\n" +
                         "\n" +
                         "💰 **Koeficientët:**\n" +
                         "• Fitues " + team1 + ": **2.0x**\n" +
                         "• Fitues " + team2 + ": **2.0x**\n" +
                         "• Barazim: **4.0x**\n" +
                         "\n" +
                         "💡 **Vë bastet tani!** \n" +
                         "• `'bet 1 100 " + team1 + "` - Bast për fitues " + team1 + "\n" +
                         "• `'bet 2 100 " + team2 + "` - Bast për fitues " + team2 + "\n" +
                         "• `'bet 3 100 Draw` - Bast për barazim\n" +
                         "\n" +
                         "📢 **Basti mbyllet pas 2 orësh!**").queue();
        
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                int result = random.nextInt(3);
                String resultText;
                String winnerName;
                int winnerType;
                
                if (result == 0) {
                    resultText = "🏆 **" + team1 + "** fitoi ndeshjen!";
                    winnerName = team1;
                    winnerType = 1;
                } else if (result == 1) {
                    resultText = "🏆 **" + team2 + "** fitoi ndeshjen!";
                    winnerName = team2;
                    winnerType = 2;
                } else {
                    resultText = "🤝 Ndeshja përfundoi **Barazim**!";
                    winnerName = "Draw";
                    winnerType = 3;
                }
                
                String resultAnnouncement = "@everyone 📢 **Rezultati përfundimtar!**\n" +
                                            resultText + "\n" +
                                            "\n" +
                                            "✅ **Fituesit e basteve janë shpërblyer!**";
                
                event.getChannel().sendMessage(resultAnnouncement).queue();
                
                List<String> winners = new ArrayList<>();
                for (Map.Entry<String, Bet> entry : bets.entrySet()) {
                    Bet bet = entry.getValue();
                    
                    if (!bet.isSettled) {
                        boolean won = false;
                        
                        if (winnerType == 1 && bet.type == 1 && bet.team.equalsIgnoreCase(team1)) {
                            won = true;
                        } else if (winnerType == 2 && bet.type == 2 && bet.team.equalsIgnoreCase(team2)) {
                            won = true;
                        } else if (winnerType == 3 && bet.type == 3) {
                            won = true;
                        }
                        
                        if (won) {
                            int winAmount = (int)(bet.amount * bet.coefficient);
                            db.shtoPara(bet.userId, winAmount);
                            bet.isSettled = true;
                            winners.add("🎉 **" + bet.username + "** fitoi **" + winAmount + "** lekë!");
                        } else {
                            bet.isSettled = true;
                            winners.add("😢 **" + bet.username + "** humbi **" + bet.amount + "** lekë.");
                        }
                    }
                }
                
                if (!winners.isEmpty()) {
                    String winMessage = "@everyone 📊 **Përfundimi i basteve!**\n" +
                                       String.join("\n", winners);
                    event.getChannel().sendMessage(winMessage).queue();
                }
                
                bets.clear();
            }
        }, 7200000);
    }
}