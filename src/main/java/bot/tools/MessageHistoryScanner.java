package bot.tools;

import bot.managers.MessageStatsManager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.util.List;

public class MessageHistoryScanner extends ListenerAdapter {

    private final MessageStatsManager db;

    public MessageHistoryScanner(MessageStatsManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().equalsIgnoreCase("!scmsg")) return;
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

        Guild guild = event.getGuild();
        event.getChannel().sendMessage("📥 서버 전체 메시지 스캔 시작...").queue();

        new Thread(() -> {
            for (TextChannel channel : guild.getTextChannels()) {
                scanChannel(channel);
            }
            event.getChannel().sendMessage("✅ 모든 채널 메시지 스캔 완료!").queue();
        }).start();
    }

    private void scanChannel(TextChannel channel) {
        MessageHistory history = channel.getHistory();
        int batchCount = 0;

        while (true) {
            List<Message> messages = history.retrievePast(100).complete();
            if (messages.isEmpty()) break;

            for (Message message : messages) {
                if (message.getAuthor().isBot()) continue;
                try {
                    db.addMessage(message.getAuthor().getId());
                } catch (SQLException e) {
                    System.err.println("DB 저장 오류: " + e.getMessage());
                }
            }
            batchCount += messages.size();
            if (batchCount % 1000 == 0) {
                System.out.println("...스캔한 메시지 수: " + batchCount);
            }
        }
    }
}
