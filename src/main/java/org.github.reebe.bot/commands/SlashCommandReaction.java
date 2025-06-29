package org.github.reebe.bot.commands;


import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandReaction extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch(event.getName()) {
            case "서버생성일":
                event.reply("서버는 2022년 1월 30일에 만들어 졌다냥!").queue();
                break;
            case "서버장":
                event.reply("서버장은 리베다냥").queue();
                break;
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandDatas = new ArrayList<>();
        commandDatas.add(
                Commands.slash("서버생성일", "서버는 언제 만들어졌냥?")
        );
        commandDatas.add(
                Commands.slash("서버장", "서버장은 누구냥?")
        );

        event.getGuild().updateCommands().addCommands(commandDatas).queue();
    }

}
