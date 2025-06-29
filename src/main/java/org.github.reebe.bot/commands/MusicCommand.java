package org.github.reebe.bot.commands;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.github.reebe.bot.music.PlayerManager;

public class MusicCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        String[] parts = msg.split(" ", 2);
        String command = parts[0];

        switch (command) {
            // play
            case "!ㅍ":
            case "!p":
                if (parts.length < 2) {
                    event.getChannel().sendMessage("노래제목을 넣으라냥");
                } else {
                    playMusic(event, parts[1]);
                }
                break;

            // stop
            case "!ㅅ":
            case "!s":
                stopMusic(event);
                break;

            // queue
            case "!ㅋ":
            case "!q":
                showQueue(event);
                break;

            // leave
            case "!ㄴ":
            case "!l":
                leaveChannel(event);
                break;
        }
    }

    public void playMusic(MessageReceivedEvent event, String text) {
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.getChannel().sendMessage("음성채널에 들어가라냥").queue();
            return;
        }

        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }

        String link = "ytsearch: " + text + " 노래";
        PlayerManager.getINSTANCE().loadAndPlay(event.getChannel().asTextChannel(), link, event.getMember());
    }

    public void stopMusic(MessageReceivedEvent event) {
        var manager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        manager.audioPlayer.stopTrack();
        manager.scheduler.clearQueue();
        event.getChannel().sendMessage("⏹️ 음악을 정지하고 큐를 비웠습니다.").queue();
    }

    public void showQueue(MessageReceivedEvent event) {
        var manager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        var queue = manager.scheduler.getQueue();

        if (queue.isEmpty()) {
            event.getChannel().sendMessage("대기열이 비었다냥").queue();
            return;
        }

        StringBuilder builder = new StringBuilder("📜 대기열 목록:\n");
        int index = 1;
        for (var track : queue) {
            builder.append(index++)
                    .append(". ")
                    .append(track.getInfo().title)
                    .append(" (by ")
                    .append(track.getInfo().author)
                    .append(")\n");
        }

        event.getChannel().sendMessage(builder.toString()).queue();
    }

    public void leaveChannel(MessageReceivedEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (audioManager.isConnected()) {
            audioManager.closeAudioConnection();
            event.getChannel().sendMessage("잘 있어라냥").queue();
        } else {
            event.getChannel().sendMessage("쉬고 있는데 왜 부르냥").queue();
        }
    }
}
