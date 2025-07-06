package bot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChattingReaction extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        String[] parts = msg.split(" ", 2);

        switch(parts[0]) {
            case "!서버생성일":
                event.getChannel().sendMessage("서버는 2022년 1월 30일에 만들어 졌다냥!\n").queue();
                break;

            case "!서버장":
                event.getMessage().reply("서버장은 리베다냥").queue();
                break;

            case "!메세지수":
                
        }
    }
}