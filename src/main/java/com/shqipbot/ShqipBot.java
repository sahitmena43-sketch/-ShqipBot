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
import java.util.concurrent.TimeUnit;

public class ShqipBot extends ListenerAdapter {
    
    private Database db;
    private Random random;
    private JDA jda;
    private String lastCommand = "";
    private long lastCommandTime = 0;
    
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
                .setTitle("🔫 GRABISTJE 🔫");
            
            if (suksesi && balanceViktima >= shuma) {
                db.zbritPara(target.getId(), shuma);
                db.shtoPara(userId, shuma);
                embed.setDescription("**" + event.getAuthor().getName() + "** grabisi @" + targetName)
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
        
        // ==================== 'help ====================
        else if (command.equals("'help")) {
            EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("🇦🇱 SHQIPBOT - Lista e Komandave 🇦🇱")
                .setDescription("Boti ekonomik 100% në gjuhën shqipe!")
                .addField("💼 **PUNË**", "`'work` - Puno në Shqipëri (20 punë alla shqiptare)", false)
                .addField("🍑 **SHËRBIME**", "`'slut` - Shërbime të dyshimta", false)
                .addField("😈 **KRIM**", "`'crime` - Punë kriminale", false)
                .addField("🔫 **GRABISTJE**", "`'rob @user` - Grabiste dikë", false)
                .addField("💰 **FINANCA**", "`'bal` - Gjendja\n`'dep` - Bonus ditor\n`'with` - Tërhiq", false)
                .setFooter("ShqipBot © 2026", null)
                .setTimestamp(new Date().toInstant());
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }
}