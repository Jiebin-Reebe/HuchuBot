package bot;

import bot.commands.XpCommand;
import bot.managers.MessageStatsManager;
import bot.system.XpSystem;
import bot.trackers.MessageHistoryScanner;
import bot.trackers.MessageTracker;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import bot.commands.ChattingReaction;
import bot.commands.MusicCommand;
import bot.commands.SlashCommandReaction;
import bot.managers.BotTokenManager;
import bot.managers.ShutdownManager;

import java.sql.SQLException;
import java.util.EnumSet;

public class DiscordBot {

    public static void main(String[] args) throws SQLException {
        MessageStatsManager db = new MessageStatsManager("message_stats.db");
        XpSystem xpSystem = new XpSystem(db);

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
                        new MessageHistoryScanner(db),
                        new MessageTracker(db),
                        xpSystem,
                        new XpCommand(xpSystem, db),
                        new MusicCommand(),
                        new ChattingReaction(),
                        new SlashCommandReaction()
                );
        var jda = builder.build();

        //JVM 종료시 음성 채널에서 나감
        ShutdownManager.registerShutdownHook(jda);
    }
}