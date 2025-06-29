package org.github.reebe.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.github.reebe.bot.commands.ChattingReaction;
import org.github.reebe.bot.commands.MusicCommand;
import org.github.reebe.bot.commands.SlashCommandReaction;
import org.github.reebe.bot.managers.BotTokenManager;
import org.github.reebe.bot.managers.ShutdownManager;

import java.util.EnumSet;

public class DiscordBot {

    public static void main(String[] args) {
        BotTokenManager tokenManager = new BotTokenManager();
        String token = tokenManager.getDiscordBotToken();

        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES
        );

        JDABuilder builder = JDABuilder.createDefault(token)
                .enableIntents(intents)
                // 상메
                .setActivity(Activity.customStatus("츄르 먹는중..."))
                // 이벤트 리스너
                .addEventListeners(
                        new MusicCommand(),
                        new ChattingReaction(),
                        new SlashCommandReaction()
                );
        var jda = builder.build();

        //JVM 종료시 음성 채널에서 나감
        ShutdownManager.registerShutdownHook(jda);
    }
}