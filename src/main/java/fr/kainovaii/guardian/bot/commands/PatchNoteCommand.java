package fr.kainovaii.guardian.bot.commands;

import fr.kainovaii.guardian.core.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PatchNoteCommand implements Command
{
    @Override
    public String getName() {
        return "update";
    }

    @Override
    public SlashCommandData getCommandData()
    {
        return Commands.slash(getName(), "Patch not list");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event)
    {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ Vous n'avez pas la permission d'utiliser cette commande.").setEphemeral(true).queue();
            return;
        }

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("🚀 Patch note");
        embed.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        embed.setDescription(
            "Voici les dernières nouveautés :\n\n" +
            "🔹 **Update 1 :** Nouveau message d'accueil.\n" +
            "🔹 **Update 2 :** Nouveau message pour les boosters.\n\n \n\n"
        );
        embed.setFooter("Dernière mise à jour : " + now, event.getJDA().getSelfUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
    }
}