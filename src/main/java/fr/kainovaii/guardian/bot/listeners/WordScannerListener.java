package fr.kainovaii.guardian.bot.listeners;

import fr.kainovaii.guardian.core.ConfigManager;
import fr.kainovaii.guardian.core.PerspectiveConfig;
import fr.kainovaii.guardian.domain.alert.AlertRepository;
import fr.kainovaii.guardian.domain.penalty.PenaltyRepository;
import fr.kainovaii.guardian.core.Loader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class WordScannerListener extends ListenerAdapter
{
    private final List<String> forbiddenWords = Arrays.asList(
        "merde", "putain", "con", "connard", "salope", "enculé", "bordel", "chiant", "nique", "ta gueule", "pute",
        "salaud", "pédé", "trou du cul", "bite", "chatte", "foutre", "enculer", "connasse", "abruti", "idiot",
        "ordure", "enfoiré", "crétin", "foutre-merde", "garce", "enflure", "connard de merde", "sale con",
        "sac à merde", "pute de merde", "fils de pute", "batard", "bouffon", "salaud de première", "tête de noeud",
        "trouduc", "branleur", "charogne", "foutre", "casse-couilles", "salaud de merde", "idiot de merde",
        "crétin de première", "mange-merde", "sale bâtard", "tête de con", "abruti fini", "fils de chien",
        "pute borgne", "sale enculé", "vieille pute", "gros con", "petit con", "connard fini", "tête de bite",
        "fuck", "shit", "bitch", "asshole", "bastard", "dick", "pussy", "cunt", "slut", "damn",
        "fag", "douchebag", "prick", "bollocks", "twat", "cock", "bugger", "arse", "shithead",
        "motherfucker", "son of a bitch", "asswipe", "asshat", "dipshit", "scumbag", "jackass",
        "twatwaffle", "dickhead", "cocksucker", "piss off", "wanker", "shitbag", "fuckface", "shitfuck",
        "cumdumpster", "fuckhead", "assclown", "cuntface", "dumbass", "bastard son", "bitch ass",
        "cockwomble", "shitstain", "fuckboy", "fucktard", "arsehole", "motherfucking", "shitface",
        "cockhead", "asslicker", "dickwad", "prickface", "twatwaffle", "shitdick", "fucknut", "dickface",
        "fuckstick", "shitbagger", "twatface", "assface", "dickweed", "wankstain", "cocksplat", "assmunch",
        "shitweasel", "fucknugget", "shitfuckery", "cockmongler", "dickbrain", "arseholeface", "fucktacular",
        "shitstorm", "assmonger", "fucktardery", "cuntmonger", "dickhole", "shitfucker", "arsebandit",
        "cockjockey", "dickmonger", "assholeface", "twatsack", "fuckbucket", "shitlicker", "dicklicker",
        "assbiscuit", "shitnugget", "fuckbag", "cuntbucket", "dickfacepuncher", "assnugget", "cockwaffle",
        "fuckwit", "dickshit", "shitmonkey", "assbucket", "cockmuncher", "dickheadfuck", "twatlicker",
        "fuckmonger", "shitlicking", "asslicking", "cockfucker", "dicklicking", "fuckpuddle", "shitpuddle",
        "cuntlicker", "assfucker", "twatpuddle", "dickmuffin", "cocknugget", "assmuffin", "shitfacepuncher"
    );

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
    private final String PERSPECTIVE_API_KEY;
    private final BlockingQueue<MessageData> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WordScannerListener(String token)
    {
        PerspectiveConfig config = ConfigManager.getPerspective();
        PERSPECTIVE_API_KEY = token;
        executor.submit(() -> {
            while (true) {
                try {
                    MessageData data = queue.take();
                    double toxicity = getToxicityScore(data.message);
                    if (toxicity >= config.getAlertThreshold())
                    {
                        AlertRepository alertRepository = new AlertRepository();
                        alertRepository.create(data.member.getId(), data.member.getEffectiveName(), data.word, data.message, data.channel.getName(), data.timestamp, toxicity);
                        sendAlert(data.member, data.channel, data.word, data.message,  data.channel.getName(), data.timestamp, data.member.getJDA().getSelfUser().getAvatarUrl(), toxicity);

                        if (toxicity >= config.getPenaltyThreshold())
                        {
                            Role mutedRole = data.channel.getGuild().getRolesByName("Muted", true).stream().findFirst().orElse(null);
                            if (mutedRole != null && !data.member.getRoles().contains(mutedRole)) {
                                data.channel.getGuild().addRoleToMember(data.member, mutedRole).queue(
                                    success -> {
                                        PenaltyRepository penaltyRepository = new PenaltyRepository();
                                        penaltyRepository.create(data.member.getId(), data.member.getEffectiveName(), "Toxicity score +0.7", "Guardian", data.timestamp, "Mute", "pending", toxicity);
                                    },
                                    error -> error.printStackTrace()
                                );
                            }
                        }
                    }
                    Thread.sleep(500); // 2 messages/sec max
                } catch (InterruptedException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;

        TextChannel alertChannel = event.getGuild().getTextChannelById(Loader.getChannelModo());
        if (alertChannel == null) return;

        Member member = event.getMember();
        if (member == null) return;

        String originalMessage = event.getMessage().getContentDisplay();
        String cleanedMessage = normalizeMessage(originalMessage);

        for (String word : forbiddenWords) {
            String wordClean = word.toLowerCase();
            if (containsWord(cleanedMessage, wordClean)) {
                String timestamp = event.getMessage().getTimeCreated().format(timeFormatter);
                queue.offer(new MessageData(member, originalMessage, word, alertChannel, timestamp));
                break;
            }
        }
    }

    // Nettoyage : supprime accents, met en minuscule et remplace ponctuation par espaces
    private String normalizeMessage(String message)
    {
        String normalized = Normalizer.normalize(message, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // enlève accents
        return normalized.replaceAll("[^a-zA-Z0-9]", " ").toLowerCase();
    }

    // Vérifie si le mot existe dans le message comme mot entier
    private boolean containsWord(String message, String word)
    {
        String padded = " " + message + " ";
        return padded.contains(" " + word + " ");
    }

    private double getToxicityScore(String message) throws IOException
    {
        String payload = "{ \"comment\": {\"text\": \"" + message.replace("\"", "\\\"") + "\"}, " +
                "\"languages\": [\"fr\"], \"requestedAttributes\": {\"TOXICITY\": {}} }";

        URL url = new URL("https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + PERSPECTIVE_API_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes("utf-8"));
        }

        try (InputStream is = conn.getInputStream()) {
            String response = new String(is.readAllBytes(), "utf-8");
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            return json.getAsJsonObject("attributeScores")
                    .getAsJsonObject("TOXICITY")
                    .getAsJsonObject("summaryScore")
                    .get("value").getAsDouble();
        }
    }

    public void sendAlert(Member member, TextChannel alertChannel, String word, String originalMessage, String messageChannel, String timestamp, String avatar, double toxicity)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("⚠ Mot interdit détecté !");
        embed.setDescription(member.getAsMention() + " a utilisé un mot interdit : `" + word + "`\n\n" +
                "**Message :** " + originalMessage + "\n" +
                "**Salon :** #" + messageChannel + "\n" +
                "**Heure :** " + timestamp + "\n" +
                "**Score de toxicité :** " + String.format("%.2f", toxicity));
        embed.setThumbnail(member.getUser().getAvatarUrl());
        embed.setFooter(timestamp, avatar);

        alertChannel.sendMessageEmbeds(embed.build()).queue();
    }

    private static class MessageData
    {
        final Member member;
        final String message;
        final String word;
        final TextChannel channel;
        final String timestamp;

        MessageData(Member member, String message, String word, TextChannel channel, String timestamp)
        {
            this.member = member;
            this.message = message;
            this.word = word;
            this.channel = channel;
            this.timestamp = timestamp;
        }
    }
}
