package fr.kainovaii.guardian.bot.listeners;

import fr.kainovaii.guardian.bot.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlashCommandListener extends ListenerAdapter
{
    private final Map<String, Command> commands = new HashMap<>();

    public SlashCommandListener(List<Command> commandList)
    {
        for (Command cmd : commandList) {
            commands.put(cmd.getName(), cmd);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        Command cmd = commands.get(event.getName());
        if (cmd != null) {
            try {
                cmd.execute(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            event.reply("Commande inconnue!").setEphemeral(true).queue();
        }
    }
}