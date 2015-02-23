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

public class MediaPlayerDevice {

    public enum Source {
        RADIO, MEDIA
    }

    private final Logger log = Logger.getLogger(getClass().getName());

    private final List<String> mediaFiles = new ArrayList<>();
    private final List<String> radioFiles = new ArrayList<>();
    private MediaPlayer player;

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

        radioFiles.add("Sultans of swing.mp3");
        // TODO add radio files

        initJavaFX();
    }

    private void initJavaFX() {
        JFXPanel fxPanel = new JFXPanel();
    }

    public Source[] getSources() {
        return Source.values();
    }

    public List<String> getMediaFilesForSource(Source source) {
        switch (source) {
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

        Source activeSource = Source.valueOf(Source.class, source);
        List<String> fileList = getMediaFilesForSource(activeSource);
        String mediaFile = fileList.get(num);
        Media media = new Media(new File(mediaFile).toURI().toString());
        player = new MediaPlayer(media);

        if (playing) {
            player.play();
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
