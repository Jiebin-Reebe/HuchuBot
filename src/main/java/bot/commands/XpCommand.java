package bot.commands;

import bot.managers.MessageStatsManager;
import bot.system.XpSystem;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class XpCommand extends ListenerAdapter {

    private final XpSystem xpSystem;
    private final MessageStatsManager db;

    public XpCommand(XpSystem xpSystem, MessageStatsManager db) {
        this.xpSystem = xpSystem;
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String content = event.getMessage().getContentRaw();
        if (!content.startsWith("!")) return;

        String[] parts = content.split("\\s+");
        String command = parts[0].toLowerCase();

        try {
            if (command.equals("!msgcount")) {
                handleMsgCount(event, parts);
            } else if (command.equals("!leaderboard")) {
                handleLeaderboard(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessage("DB 오류가 발생했습니다.").queue();
        }
    }

    private void handleMsgCount(MessageReceivedEvent event, String[] parts) throws SQLException {
        String userId;

        if (parts.length > 1 && !event.getMessage().getMentions().getUsers().isEmpty()) {
            // 멘션된 첫 번째 유저 가져오기
            User mentionedUser = event.getMessage().getMentions().getUsers().get(0);
            Member mentionedMember = event.getGuild().getMember(mentionedUser);
            if (mentionedMember != null) {
                userId = mentionedMember.getId();
            } else {
                // 멤버가 없으면 User ID 직접 사용
                userId = mentionedUser.getId();
            }
        } else if (parts.length > 1) {
            // 멘션 없이 ID 직접 입력했을 때
            userId = parts[1];
        } else {
            // 아무것도 없으면 명령어 작성자 본인
            userId = event.getAuthor().getId();
        }

        double totalXp = xpSystem.getTotalXp(userId);
        int level = xpSystem.calculateLevel(totalXp);

        String userTag = event.getJDA().getUserById(userId) != null
                ? event.getJDA().getUserById(userId).getAsTag()
                : userId;

        Map<String, Integer> stats = db.getUserStats(userId);
        int messageCount = stats.getOrDefault("message_count", 0);

        String response = String.format("%s 님의 XP 정보:\n" +
                        "- 레벨: %d\n" +
                        "- 총 XP: %.1f (메세지 %d)\n",
                userTag, level, totalXp, messageCount);

        event.getChannel().sendMessage(response).queue();
    }

    private void handleLeaderboard(MessageReceivedEvent event) throws SQLException {
        Map<String, int[]> allStats = db.getAllUserStats();

        Map<String, Double> xpMap = new HashMap<>();
        for (Map.Entry<String, int[]> entry : allStats.entrySet()) {
            int messageCount = entry.getValue()[0];
            int voiceMinutes = entry.getValue()[1];
            double xp = xpSystem.calculateXp(messageCount);
            xpMap.put(entry.getKey(), xp);
        }

        List<Map.Entry<String, Double>> topList = xpMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder("🏆 서버 XP 리더보드 TOP 10\n\n");
        int rank = 1;
        for (Map.Entry<String, Double> entry : topList) {
            String userId = entry.getKey();
            double totalXp = entry.getValue();
            int level = xpSystem.calculateLevel(totalXp);

            String mention = "<@" + userId + ">";

            sb.append(String.format("%d. %s — 레벨 %d, XP %.1f\n", rank++, mention, level, totalXp));
        }

        event.getChannel().sendMessage(sb.toString()).queue();
    }
}
