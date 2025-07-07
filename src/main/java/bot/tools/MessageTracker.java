package bot.tools;

import bot.managers.MessageStatsManager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;

public class MessageTracker extends ListenerAdapter {
    private final MessageStatsManager db;

    public MessageTracker(MessageStatsManager db) {
        this.db = db;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String userId = event.getAuthor().getId();
        try {
            db.addMessage(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}