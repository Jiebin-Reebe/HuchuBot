package bot.commands;

import bot.managers.MessageStatsManager;
import bot.system.XpSystem;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MessageXpCommand extends ListenerAdapter {

    private final XpSystem xpSystem;
    private final MessageStatsManager db;

    public MessageXpCommand(XpSystem xpSystem, MessageStatsManager db) {
        this.xpSystem = xpSystem;
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String content = event.getMessage().getContentRaw();
        if (!content.startsWith("!")) return;

        String[] parts = content.split(" ", 2);
        String command = parts[0];

        try {
            switch (command) {
                case "!ㅁㅅㅈ수":
                case "!메세지수":
                case "!msgcount":
                case "!msgc":
                    handleMsgCount(event, parts);
                    break;

                case "!리더보드":
                case "!순위":
                case "!ㅅㅇ":
                case "!leaderboard":
                    handleLeaderboard(event);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessage("❌ DB 오류가 발생했습니다.").queue();
        }
    }

    private void handleMsgCount(MessageReceivedEvent event, String[] parts) throws SQLException {
        String userId;

        if (parts.length > 1 && !event.getMessage().getMentions().getUsers().isEmpty()) {
            User mentionedUser = event.getMessage().getMentions().getUsers().get(0);
            userId = mentionedUser.getId();
        } else if (parts.length > 1) {
            userId = parts[1];
        } else {
            userId = event.getAuthor().getId();
        }

        double totalXp = xpSystem.getTotalXp(userId);
        int level = xpSystem.calculateLevel(totalXp);

        double nextLevelXp = 3.75 * (level + 1) * (level + 2);
        double remainingXp = nextLevelXp - totalXp;

        String progressBar = xpSystem.getProgressBar(totalXp, level);

        Map<String, Integer> stats = db.getUserStats(userId);
        int messageCount = stats.getOrDefault("message_count", 0);
        String mention = "<@" + userId + ">";

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📊 유저 XP 정보")
                .setColor(Color.CYAN)
                .setDescription(mention + "님의 통계입니다.")
                .addField("레벨", String.valueOf(level), true)
                .addField("XP", String.format("%.1f", totalXp), true)
                .addField("메시지 수", String.valueOf(messageCount), true)
                .addField("다음 레벨까지 남은 XP", String.format("%.1f", remainingXp), false)
                .addField("진행도", progressBar, false)
                .setFooter("메시지 기반 XP 시스템");

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void handleLeaderboard(MessageReceivedEvent event) throws SQLException {
        Map<String, int[]> allStats = db.getAllUserStats();

        Map<String, Double> xpMap = new HashMap<>();
        for (Map.Entry<String, int[]> entry : allStats.entrySet()) {
            int messageCount = entry.getValue()[0];
            double xp = xpSystem.calculateXp(messageCount);
            xpMap.put(entry.getKey(), xp);
        }

        List<Map.Entry<String, Double>> topList = xpMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🏆 서버 XP 리더보드")
                .setColor(Color.ORANGE)
                .setFooter("메시지 기반 XP 시스템");

        int rank = 1;
        for (Map.Entry<String, Double> entry : topList) {
            String userId = entry.getKey();
            double totalXp = entry.getValue();
            int level = xpSystem.calculateLevel(totalXp);

            String mention = "<@" + userId + ">";
            embed.addField(rank + "위", mention + "\n레벨 " + level + " | XP " + String.format("%.1f", totalXp), false);
            rank++;
        }

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
