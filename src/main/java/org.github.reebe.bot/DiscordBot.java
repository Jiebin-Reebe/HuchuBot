package org.github.reebe.bot;

import net.dv8tion.jda.api.JDABuilder;

public class DiscordBot {

    public static void main(String[] args) {
        BotTokenManager tokenManager = new BotTokenManager();
        String token = tokenManager.getDiscordBotToken();
        JDABuilder.createDefault(token)
                .build();
    }
}