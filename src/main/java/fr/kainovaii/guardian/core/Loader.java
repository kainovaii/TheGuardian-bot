package fr.kainovaii.guardian.core;

import fr.kainovaii.guardian.domain.user.UserRepository;
import fr.kainovaii.guardian.bot.listeners.BoostedListener;
import fr.kainovaii.guardian.bot.listeners.SlashCommandListener;
import fr.kainovaii.guardian.bot.listeners.WelcomeListener;
import fr.kainovaii.guardian.bot.listeners.WordScannerListener;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.reflections.Reflections;

import java.util.*;
        import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Loader
{
    public final static Logger LOGGER = Logger.getLogger("Guardian");
    private final SQLite sqlite = new SQLite(Loader.LOGGER);
    private static String token;
    private static String guildId;
    private static String channelId;
    private static String channelModoId;
    private static String webPort;
    private static String perspectiveApiKey;
    private static JDA jda;
    private static List<Map<String, Object>> memberCache = new ArrayList<>();

    public void connectDatabase()
    {
        System.out.println("Loading database");
        sqlite.connectDatabaseForCurrentThread();
        sqlite.ensureTablesExist();
    }

    public void loadConfigAndEnv()
    {
        new ConfigManager().load();
        EnvLoader env = new EnvLoader();
        env.load();
        String envType = env.get("ENVIRONMENT");

        if ("PROD".equalsIgnoreCase(envType)) {
            token = env.get("DISCORD_TOKEN_PROD");
            guildId = env.get("GUILD_ID_PROD");
            channelId = env.get("CHANNEL_ID_PROD");
            channelModoId = env.get("CHANNEL_ID_MODO_PROD");
        } else {
            token = env.get("DISCORD_TOKEN_DEV");
            guildId = env.get("GUILD_ID_DEV");
            channelId = env.get("CHANNEL_ID_DEV");
            channelModoId = env.get("CHANNEL_ID_MODO_DEV");
        }

        webPort = env.get("PORT_WEB");
        perspectiveApiKey = env.get("PERSPECTIVE_API_KEY");
    }

    public void initBot() throws InterruptedException
    {
        List<Command> commands = loadCommands();
        buildJDA(commands);
        configurePresence();
        registerGuildCommands(commands);
    }

    private List<Command> loadCommands()
    {
        Reflections reflections = new Reflections("fr.kainovaii.guardian.bot.commands");
        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);
        return commandClasses.stream()
            .map(cls -> {
                try {
                    return cls.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    Loader.LOGGER.severe("Impossible d’instancier la commande : " + cls.getName());
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private void buildJDA(List<Command> commands) throws InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(token,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT)
            .enableCache(CacheFlag.MEMBER_OVERRIDES)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .addEventListeners(
                new SlashCommandListener(commands),
                new WelcomeListener(),
                new WordScannerListener(perspectiveApiKey),
                new BoostedListener())
            .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);

        jda = builder.build();
        jda.awaitReady();
        preloadMembersCache();
    }

    private void configurePresence()
    {
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.watching("Monitored the server !"));
    }

    private void registerGuildCommands(List<Command> commands)
    {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null) {
            guild.updateCommands()
            .addCommands(commands.stream().map(Command::getCommandData).toList())
            .queue(
            success -> System.out.println("Commandes mises à jour !"),
            error -> System.err.println("Erreur lors de l’update des commandes : " + error.getMessage())
            );
        } else {
            System.err.println("La guild avec l'ID " + guildId + " n'a pas été trouvée !");
        }
    }

    public static void preloadMembersCache()
    {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;
        guild.loadMembers() .onSuccess(members -> memberCache = WebUtils.toTemplateMembers(members)).onError(Throwable::printStackTrace);
    }

    public void startWebServer() { new WebServer().start(); }

    public static JDA getJda() { return jda; }
    public static Guild getGuild() { return jda.getGuildById(guildId); }
    public static long getChannel() { return Long.parseLong(channelId); }
    public static long getChannelModo() { return Long.parseLong(channelModoId); }
    public static int getWebPort() { return Integer.parseInt(webPort); }
    public static List<Map<String, Object>> getMemberCache() { return memberCache; }

    public void registerMotd()
    {
        EnvLoader env = new EnvLoader();
        PerspectiveConfig config = ConfigManager.getPerspective();

        env.load();
        final String RESET = "\u001B[0m";
        final String CYAN = "\u001B[36m";
        final String YELLOW = "\u001B[33m";
        final String GREEN = "\u001B[32m";
        final String MAGENTA = "\u001B[35m";

        System.out.println(CYAN + "+--------------------------------------+" + RESET);
        System.out.println(CYAN + "|          Guardian Bot 1.0            |" + RESET);
        System.out.println(CYAN + "+--------------------------------------+" + RESET);
        System.out.println(GREEN + "| Developpeur       : KainoVaii        |" + RESET);
        System.out.println(GREEN + "| Version           : 1.0              |" + RESET);
        System.out.println(GREEN + "| Environnement     : " + env.get("ENVIRONMENT") + "              |" + RESET);
        System.out.println(YELLOW + "| Discord Guild     : " + guildId + "             |" + RESET);
        System.out.println(YELLOW + "| Channel principal : " + channelId + "             |" + RESET);
        System.out.println(YELLOW + "| Channel moderation: " + channelModoId + "             |" + RESET);
        System.out.println(MAGENTA + "| Threshold alert           : " +  config.getAlertThreshold() + "      |" + RESET);
        System.out.println(MAGENTA + "| Threshold penalty         : " +  config.getPenaltyThreshold() + "      |" + RESET);
        System.out.println(CYAN + "+--------------------------------------+" + RESET);
        System.out.println(CYAN + "|      Chargement des modules...       |" + RESET);
        System.out.println(CYAN + "+--------------------------------------+" + RESET);
        System.out.println();
    }

    public void initUser()
    {
        UserRepository userRepository = new UserRepository();
        if (!UserRepository.userExist("admin")) {
            userRepository.create("admin", "$2a$12$8oYepa4rQw2xixu1KpvTbeg9aVAifZCUZGhn5/rfE7ugjqk9SXi5q","ADMIN");
        }
    }
}
