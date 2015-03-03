package hu.esgott.caronboard.devices;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MediaPlayerDevice {

    public enum Source {
        RADIO, MEDIA
    }

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final double VOLUME_STEP = 0.1;

    private final List<String> mediaFiles = new ArrayList<>();
    private final List<String> radioFiles = new ArrayList<>();
    private MediaPlayer player;
    private double oldVolume;

    public MediaPlayerDevice() {
        mediaFiles.add("Sultans of swing.mp3");
        mediaFiles.add("Dani California.mp3");
        mediaFiles.add("Feeling this.mp3");
        mediaFiles.add("Highway To Hell.mp3");
        mediaFiles.add("Layla.mp3");
        mediaFiles.add("Living Loving Maid.mp3");
        mediaFiles.add("Piece of my Heart.mp3");
        mediaFiles.add("Strange Love.mp3");
        mediaFiles.add("Sweet child o'mine.mp3");

        radioFiles.add("jazzy.mp3");
        radioFiles.add("totalcar.mp3");

        initJavaFX();
    }

    private void initJavaFX() {
        @SuppressWarnings("unused")
        JFXPanel fxPanel = new JFXPanel();
    }

    public Source[] getSources() {
        return Source.values();
    }

    public List<String> getMediaFilesForSource(String source) {
        Source sourceEnum = Source.valueOf(Source.class, source);
        ;
        switch (sourceEnum) {
        case RADIO:
            return radioFiles;
        case MEDIA:
            return mediaFiles;
        default:
            return null;
        }
    }

    public List<String> getSourceNames() {
        List<Source> values = Arrays.asList(Source.values());
        System.out.println(values);
        return values.stream().map(item -> item.toString())
                .collect(Collectors.toList());
    }

    public void select(String source, int num) {
        boolean playing = false;
        if (player != null) {
            playing = player.getStatus() == MediaPlayer.Status.PLAYING;
            disposePlayer();
        }

        List<String> fileList = getMediaFilesForSource(source);
        String mediaFile = fileList.get(num);
        Media media = new Media(new File(mediaFile).toURI().toString());
        player = new MediaPlayer(media);

        if (playing) {
            player.play();
            log.info("Playing " + mediaFile + " (" + num + ") from " + source);
        }
    }

    public void togglePause() {
        if (player != null) {
            if (player.getStatus() == MediaPlayer.Status.PLAYING) {
                player.pause();
                log.info("Playback paused");
            } else {
                player.play();
                log.info("Start playing in state " + player.getStatus());
            }
        } else {
            log.warning("No player created.");
        }
    }

    public void play() {
        if (player != null) {
            log.info("Playing in state " + player.getStatus());
            player.play();
        } else {
            log.warning("No player created to play.");
        }
    }

    public void pause() {
        if (player != null) {
            log.info("Pausing in state " + player.getStatus());
            player.pause();
        } else {
            log.warning("No player created to pause.");
        }
    }

    public void seek(int millis) {
        if (player != null) {
            Duration current = player.getCurrentTime();
            Duration diff = new Duration(millis);
            Duration newPosition = current.add(diff);
            player.seek(newPosition);
            log.info("Seeked millisecs " + millis);
        }
    }

    public void increaseVolume() {
        double current = player.getVolume();
        if (current < 1.0) {
            setVolume(current + VOLUME_STEP);
        }
    }

    public void decreaseVolume() {
        double current = player.getVolume();
        if (current > 0.0) {
            setVolume(current - VOLUME_STEP);
        }
    }

    private void setVolume(double newVolume) {
        player.setVolume(newVolume);
        log.info("Volume set to " + newVolume);
    }

    public void setRecordingVolume() {
        oldVolume = player.getVolume();
        player.setVolume(0.1);
        log.info("Recording volume set, old volme stored " + oldVolume);
    }

    public void setSavedVolume() {
        player.setVolume(oldVolume);
        log.info("Saved volume reset");
    }

    public void dispose() {
        disposePlayer();
        disposeJavaFX();
    }

    private void disposePlayer() {
        if (player != null) {
            player.stop();
            player.dispose();
            player = null;
        }
    }

    private void disposeJavaFX() {
        Platform.exit();
    }

}
