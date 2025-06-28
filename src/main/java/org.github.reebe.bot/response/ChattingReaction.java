package org.github.reebe.bot.response;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChattingReaction extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String str = event.getMessage().getContentDisplay();

        switch(str) {
            case "뭐해" :
            case "뭐해?" :
                event.getMessage().reply("그러게 뭐할까").queue();
        }
    }
}
