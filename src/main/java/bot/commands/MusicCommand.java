package bot.commands;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import bot.music.PlayerManager;

public class MusicCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        String[] parts = msg.split(" ", 2);
        String command = parts[0];

        switch (command) {
            // play
            case "!재생":
            case "!플레이":
            case "!ㅍ":
            case "!play":
            case "!p":
                if (parts.length < 2) {
                    event.getChannel().sendMessage("노래제목을 넣으라냥 🎵").queue();
                } else {
                    playMusic(event, parts[1]);
                }
                break;

            // 일시정지
            case "!일시정지":
            case "!ㅇ":
            case "!i":
                togglePause(event);
                break;

            // clear
            case "!제거":
            case "!ㅊ":
            case "!clear":
            case "!c":
                clearQueue(event);
                break;

            // queue
            case "!재생목록":
            case "!ㅋ":
            case "!queue":
            case "!q":
                showQueue(event);
                break;

            // leave
            case "!나가기":
            case "!나가":
            case "!ㄴ":
            case "!leave":
            case "!l":
                clearQueue(event);
                leaveChannel(event);
                break;

            // remove
            case "!삭제":
            case "!ㄹ":
            case "!remove":
            case "!r":
                if (parts.length < 2) {
                    event.getChannel().sendMessage("제거할 곡 번호를 입력하라냥! 🗑️").queue();
                } else {
                    removeFromQueue(event, parts[1].trim());
                }
                break;

            // skip
            case "!스킵":
            case "!ㅅ":
            case "!skip":
            case "!s":
                skipMusic(event);
        }
    }

    public void playMusic(MessageReceivedEvent event, String text) {
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getChannel().sendMessage("음성채널에 들어가라냥 🎧").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }

        PlayerManager.getINSTANCE().loadAndPlay(event.getChannel().asTextChannel(), text, event.getMember());
    }

    /*public void stopMusic(MessageReceivedEvent event) {
        var manager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        manager.audioPlayer.stopTrack();
        event.getChannel().sendMessage("⏹️ 음악을 정지했다냥! 대기열은 그대로 남아있다냥.").queue();
    }*/

    public void clearQueue(MessageReceivedEvent event) {
        var manager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        manager.audioPlayer.stopTrack();
        manager.scheduler.clearQueue();
        event.getChannel().sendMessage("🧹 대기열을 싹 비웠다냥!").queue();
    }

    public void showQueue(MessageReceivedEvent event) {
        var manager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        var queue = manager.scheduler.getQueue();

        if (queue.isEmpty()) {
            event.getChannel().sendMessage("대기열이 비었다냥 📝").queue();
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
            event.getChannel().sendMessage("잘 있어라냥 👋").queue();
        } else {
            event.getChannel().sendMessage("쉬고 있는데 왜 부르냥 😴").queue();
        }
    }

    public void removeFromQueue(MessageReceivedEvent event, String arg) {
        var manager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        var queue = manager.scheduler.getQueue();

        if (arg.equalsIgnoreCase("c") || arg.equalsIgnoreCase("ㅎ")) {
            if (manager.audioPlayer.getPlayingTrack() != null) {
                var title = manager.audioPlayer.getPlayingTrack().getInfo().title;
                manager.audioPlayer.stopTrack(); // 재생 중 곡 제거
                event.getChannel().sendMessage("🛑 현재 재생 중인 '" + title + "' 곡을 제거했다냥!").queue();
            } else {
                event.getChannel().sendMessage("지금 재생 중인 곡이 없다냥 😿").queue();
            }
            return;
        }

        if (arg.equalsIgnoreCase("l") || arg.equalsIgnoreCase("ㅁ")) {
            if (queue.isEmpty()) {
                event.getChannel().sendMessage("대기열이 비어있다냥.").queue();
                return;
            }
            var queueList = new java.util.ArrayList<>(queue);
            var removed = queueList.remove(queueList.size() - 1);
            queue.clear();
            queue.addAll(queueList);
            event.getChannel().sendMessage("🗑️ 마지막 곡 '" + removed.getInfo().title + "' 을(를) 제거했음!").queue();
            return;
        }

        int index;
        try {
            index = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("제거할 곡 번호를 숫자로 입력하라냥! 혹은 `c or ㅎ (현재)`, `l or ㅁ (마지막)` 도 된다냥 🔢").queue();
            return;
        }

        if (index < 1 || index > queue.size()) {
            event.getChannel().sendMessage("해당 번호의 곡이 없다냥.").queue();
            return;
        }

        var queueList = new java.util.ArrayList<>(queue);
        var removed = queueList.remove(index - 1);
        queue.clear();
        queue.addAll(queueList);

        event.getChannel().sendMessage("❌ '" + removed.getInfo().title + "' 을(를) 대기열에서 제거했디냥").queue();
    }

    public void togglePause(MessageReceivedEvent event) {
        var manager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());

        if (manager.audioPlayer.getPlayingTrack() == null) {
            event.getChannel().sendMessage("지금 재생 중인 곡이 없다냥 😿").queue();
            return;
        }

        boolean isPaused = manager.audioPlayer.isPaused();
        manager.audioPlayer.setPaused(!isPaused);

        if (isPaused) {
            event.getChannel().sendMessage("▶ 일시정지된 곡을 다시 재생했다냥!").queue();
        } else {
            event.getChannel().sendMessage("⏸️ 곡을 일시정지했다냥!").queue();
        }
    }

    public void skipMusic(MessageReceivedEvent event) {
        var manager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());

        if (manager.audioPlayer.getPlayingTrack() == null && manager.scheduler.getQueue().isEmpty()) {
            event.getChannel().sendMessage("⏭️ 스킵할 곡이 없다냥!").queue();
            return;
        }

        manager.scheduler.nextTrack();
        event.getChannel().sendMessage("⏭️ 다음 곡으로 스킵했다냥!").queue();
    }
}
