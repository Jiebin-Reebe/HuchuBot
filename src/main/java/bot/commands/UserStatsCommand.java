package bot.commands;

import bot.tracking.UserStatsTracker;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserStatsCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // 봇의 메시지는 무시
        if (event.getAuthor().isBot()) return;

        String content = event.getMessage().getContentRaw();
        Member member = event.getMember();

        if (member == null) return;

        String userId = member.getId();

        switch (content) {
            case "!메세지수":
                int count = UserStatsTracker.getMessageCount(userId);
                event.getChannel().sendMessage(
                        member.getEffectiveName() + " 님은 지금까지 " + count + "개의 메시지를 보냈다냥! 💬"
                ).queue();
                break;

            case "!음성채팅시간":
                long seconds = UserStatsTracker.getVoiceChatTime(userId);
                String formatted = formatTime(seconds);
                event.getChannel().sendMessage(
                        member.getEffectiveName() + " 님은 지금까지 음성 채팅을 " + formatted + " 동안 사용했다냥! 🎧"
                ).queue();
                break;
        }
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%02d시간 %02d분 %02d초", hours, minutes, secs);
    }
}
