package fr.kainovaii.guardian.bot.commands;

import fr.kainovaii.guardian.core.Command;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PingCommand implements Command
{
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public SlashCommandData getCommandData()
    {
        return Commands.slash(getName(), "Répond avec Pong!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Pong!").queue();
    }
}