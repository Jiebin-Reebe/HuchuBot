package bot.system;

import bot.managers.MessageStatsManager;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class XpSystem extends ListenerAdapter {
    private final MessageStatsManager db;
    private final double voiceWeight;  // 음성 채팅 분당 XP 가중치
    private final Map<String, Long> joinTimestamps; // userId -> 입장 시간(ms)

    public XpSystem(MessageStatsManager db, double voiceWeight) {
        this.db = db;
        this.voiceWeight = voiceWeight;
        this.joinTimestamps = new HashMap<>();
    }

    // 지수 함수로 레벨 계산
    public int calculateLevel(double totalXp) {
        int level = 0;
        double a = 50;  // 시작 XP 필요량
        double b = 1.2; // 증가율
        while (totalXp >= a * Math.pow(b, level)) {
            level++;
        }
        return level;
    }

    // 총 XP 계산 (메시지 + 음성 채팅)
    public double calculateXp(int messageCount, int voiceMinutes) {
        return messageCount + voiceMinutes * voiceWeight;
    }

    // DB에서 불러온 메시지 수 + 음성 채팅 분으로 XP 구하기
    public double getTotalXp(String userId) throws SQLException {
        Map<String, Integer> stats = db.getUserStats(userId);
        int messageCount = stats.getOrDefault("message_count", 0);
        int voiceMinutes = stats.getOrDefault("voice_minutes", 0);
        return calculateXp(messageCount, voiceMinutes);
    }

    // 음성 채팅 입장/퇴장 이벤트 추적하여 DB에 저장
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        String userId = event.getMember().getId();

        if (event.getChannelJoined() != null && event.getChannelLeft() == null) {
            // 음성 채널 입장
            joinTimestamps.put(userId, System.currentTimeMillis());
        } else if (event.getChannelLeft() != null && event.getChannelJoined() == null) {
            // 음성 채널 퇴장
            Long joinTime = joinTimestamps.remove(userId);
            if (joinTime != null) {
                long durationMs = System.currentTimeMillis() - joinTime;
                int minutes = (int) (durationMs / 60000);
                if (minutes > 0) {
                    try {
                        db.addVoiceMinutes(userId, minutes);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

