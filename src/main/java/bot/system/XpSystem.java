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
        return (int) Math.floor(Math.log10(totalXp + 1) * 5);  // 레벨이 지수적으로 증가
    }
}