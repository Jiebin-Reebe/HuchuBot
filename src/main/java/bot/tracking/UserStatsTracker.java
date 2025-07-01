package bot.tracking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class UserStatsTracker extends ListenerAdapter {
    private static final File jsonFile = new File("user_stats.json");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Map<String, UserStats> statsMap = new HashMap<>();

    public UserStatsTracker() {
        loadStats();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        if (user.isBot()) return;

        String userId = user.getId();
        UserStats stats = statsMap.getOrDefault(userId, new UserStats());
        stats.messageCount++;
        statsMap.put(userId, stats);
        saveStats();
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Member member = event.getMember();
        String userId = member.getId();
        UserStats stats = statsMap.getOrDefault(userId, new UserStats());
        long now = Instant.now().getEpochSecond();

        if (event.getOldValue() == null && event.getNewValue() != null) {
            // Joined voice
            stats.lastVoiceJoinTime = now;
        } else if (event.getOldValue() != null && event.getNewValue() == null) {
            // Left voice
            if (stats.lastVoiceJoinTime > 0) {
                stats.voiceTimeTotal += now - stats.lastVoiceJoinTime;
                stats.lastVoiceJoinTime = 0;
            }
        }

        statsMap.put(userId, stats);
        saveStats();
    }

    public static int getMessageCount(String userId) {
        return statsMap.getOrDefault(userId, new UserStats()).messageCount;
    }

    public static long getVoiceChatTime(String userId) {
        return statsMap.getOrDefault(userId, new UserStats()).voiceTimeTotal;
    }

    private static void loadStats() {
        if (jsonFile.exists()) {
            try {
                statsMap = objectMapper.readValue(jsonFile, new TypeReference<>() {});
            } catch (IOException e) {
                e.printStackTrace();
                statsMap = new HashMap<>();
            }
        } else {
            statsMap = new HashMap<>();
        }
    }

    private static void saveStats() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, statsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserStatsString(User user) {
        UserStats stats = statsMap.getOrDefault(user.getId(), new UserStats());
        long minutes = stats.voiceTimeTotal / 60;
        return "📊 통계 for " + user.getName() + ":\n" +
                "- 총 채팅 수: " + stats.messageCount + "개\n" +
                "- 음성 채팅 시간: 약 " + minutes + "분";
    }

    static class UserStats {
        public int messageCount = 0;
        public long voiceTimeTotal = 0;
        public long lastVoiceJoinTime = 0;
    }
}
