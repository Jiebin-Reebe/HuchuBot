package bot.system;

import bot.managers.MessageStatsManager;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;

public class XpSystem extends ListenerAdapter {
    private final MessageStatsManager db;

    public XpSystem(MessageStatsManager db) {
        this.db = db;
    }

    public double getTotalXp(String userId) throws SQLException {
        int messageCount = db.getUserStats(userId).getOrDefault("message_count", 0);
        return calculateXp(messageCount);
    }

    public double calculateXp(int messageCount) {
        return messageCount;  // 메시지 수만큼 XP 부여 (1 메시지 = 1 XP)
    }

    public int calculateLevel(double totalXp) {
        return (int) Math.floor((-1 + Math.sqrt(1 + 4 * (totalXp / 3.75))) / 2);  // 레벨이 지수적으로 증가
    }

    public String getProgressBar(double totalXp, int level) {
        double currentLevelXp = 3.75 * level * (level + 1);
        double nextLevelXp = 3.75 * (level + 1) * (level + 2);
        double progress = (totalXp - currentLevelXp) / (nextLevelXp - currentLevelXp);

        int totalBars = 10;
        int filledBars = (int) Math.round(progress * totalBars);

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filledBars; i++) {
            bar.append("■");
        }
        for (int i = filledBars; i < totalBars; i++) {
            bar.append("□");
        }

        int percent = (int) (progress * 100);
        return bar.toString() + " " + percent + "%";
    }
}