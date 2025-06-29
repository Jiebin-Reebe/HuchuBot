package org.github.reebe.bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        audioPlayerManager.getConfiguration().setOpusEncodingQuality(AudioConfiguration.OPUS_QUALITY_MAX);
        audioPlayerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);


        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true);
        this.audioPlayerManager.registerSourceManager(youtube);


        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public static PlayerManager getINSTANCE() {
        if(INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel textChannel, String input, Member client) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        musicManager.scheduler.setTextChannel(textChannel);

        String trackURL;

        if (input.startsWith("https://")) {
            trackURL = input;
        } else {
            trackURL = "ytsearch:" + input;
        }

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                if (musicManager.audioPlayer.getPlayingTrack() != null) {
                    // 이미 재생 중이면 큐에 넣고 메시지 보냄
                    musicManager.scheduler.queue(audioTrack);
                    // 메시지는 queue() 메서드 안에서 처리하므로 여기서는 생략 가능
                } else {
                    // 재생 중이 아니면 바로 재생
                    musicManager.audioPlayer.startTrack(audioTrack, false);
                    textChannel.sendMessageFormat(
                            "▶ 지금 재생중: '%s' (by '%s')",
                            audioTrack.getInfo().title,
                            audioTrack.getInfo().author
                    ).queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    AudioTrack first = playlist.getTracks().get(0);
                    if (musicManager.audioPlayer.getPlayingTrack() != null) {
                        musicManager.scheduler.queue(first);
                    } else {
                        musicManager.audioPlayer.startTrack(first, false);
                        textChannel.sendMessageFormat(
                                "▶ 지금 재생중: '%s' (by '%s')",
                                first.getInfo().title,
                                first.getInfo().author
                        ).queue();
                    }
                    return;
                }
                for (AudioTrack track : playlist.getTracks()) {
                    musicManager.scheduler.queue(track);
                }
                textChannel.sendMessageFormat(
                        "▶ 재생목록 '%s' 의 총 %d곡을 대기열에 추가했다냥",
                        playlist.getName(),
                        playlist.getTracks().size()
                ).queue();
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage("❌ 일치하는 결과가 없다냥: " + trackURL).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                textChannel.sendMessage("⚠️ 재생할 수 없다냥: " +  e.getMessage()).queue();
            }
        });
    }

}