package fr.kainovaii.guardian.listeners;

import fr.kainovaii.guardian.Main;
import fr.kainovaii.guardian.utils.Loader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import java.awt.*;

public class WelcomeListener extends ListenerAdapter
{
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        Member member = event.getMember();
        TextChannel channel = event.getGuild().getTextChannelById(Loader.getChannel());
        if (channel == null) return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("🔥 Bienvenue chez les Kassos F2P !");
        embed.setDescription(
            "Salut " + member.getAsMention() + " ! Prépare-toi à plonger dans l'aventure !\n\n" +
            "📜 Consulte les règles dans #règlement.\n" +
            "⚔️ Rejoins les discussions !"
        );
        embed.setThumbnail(member.getUser().getAvatarUrl());
        embed.setFooter("Que le loot soit avec toi ! 🏆");

        channel.sendMessageEmbeds(embed.build()).queue();
    }
}
