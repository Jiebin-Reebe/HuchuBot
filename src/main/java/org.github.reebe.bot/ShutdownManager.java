package org.github.reebe.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;

public class ShutdownManager {
    public static void registerShutdownHook(JDA jda) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🧹 봇 종료 중...");
            for (Guild guild : jda.getGuilds()) {
                AudioManager audioManager = guild.getAudioManager();
                if (audioManager.isConnected()) {
                    audioManager.closeAudioConnection();
                    System.out.println("👋 나간 서버: " + guild.getName());
                }
            }
            jda.shutdown();
            System.out.println("✅ 종료 완료.");
        }));
    }
}
