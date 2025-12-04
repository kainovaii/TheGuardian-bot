package fr.kainovaii.guardian.bot.listeners;

import fr.kainovaii.guardian.core.Loader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class BoostedListener extends ListenerAdapter
{
    @Override
    public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent event)
    {
        Member member = event.getMember();
        TextChannel channel = event.getGuild().getTextChannelById(Loader.getChannel());
        if (channel == null) return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.MAGENTA);
        embed.setTitle("💎 Nouveau boost !");
        embed.setDescription(
            "Merci " + event.getUser().getAsMention() + " d’avoir boosté le serveur ! 🚀\n\n" +
            "✨ En récompense, tu as maintenant accès au **salon contributeurs** 💬 " +
            "où les boosters peuvent discuter"
        );
        embed.setThumbnail(member.getUser().getAvatarUrl());
        embed.setFooter("Tu fais briller le serveur ✨");

        channel.sendMessageEmbeds(embed.build()).queue();
    }
}
