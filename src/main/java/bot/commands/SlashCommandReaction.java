package bot.commands;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandReaction extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch(event.getName()) {



        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandDatas = new ArrayList<>();
        /*commandDatas.add(
                Commands.slash("서버생성일", "서버는 언제 만들어졌냥?")
        );
        commandDatas.add(
                Commands.slash("서버장", "서버장은 누구냥?")
        );*/

        event.getGuild().updateCommands().addCommands(commandDatas).queue();
    }

}
