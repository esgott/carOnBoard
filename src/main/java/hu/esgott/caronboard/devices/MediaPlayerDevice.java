package hu.esgott.caronboard.devices;

import hu.esgott.caronboard.devices.AudioFeedback.A;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MediaPlayerDevice {

    public enum Source {
        RADIO, MEDIA
    }

    private class Playable {
        public String uri;
        public String name;
        public A tts;

        public Playable(String fileName, String name, A tts) {
            this.name = name;
            this.tts = tts;
            uri = new File(fileName).toURI().toString();
        }
    }

    private final Logger log = Logger.getLogger(getClass().getName());

    private final List<Playable> mediaFiles = new ArrayList<>();
    private final List<Playable> radioFiles = new ArrayList<>();
    private MediaPlayer player;
    private final VolumeLevel volume = new VolumeLevel();
    private boolean recordingActive = false;
    private boolean ttsActive = false;

    public MediaPlayerDevice() {
        mediaFiles.add(new Playable("resources/music/Sultans of swing.mp3",
                "Sultans of Swing", A.SULTANS));
        mediaFiles.add(new Playable("resources/music/Dani California.mp3",
                "Dani California", A.DANI));
        mediaFiles.add(new Playable("resources/music/Feeling this.mp3",
                "Feeling this", A.FEELING));
        mediaFiles.add(new Playable("resources/music/Highway To Hell.mp3",
                "Highway To Hell", A.HIGHWAY));
        mediaFiles.add(new Playable("resources/music/Layla.mp3", "Layla",
                A.LAYLA));
        mediaFiles.add(new Playable("resources/music/Living Loving Maid.mp3",
                "Living Loving Maid", A.LIVIN));
        mediaFiles.add(new Playable("resources/music/Piece of my Heart.mp3",
                "Piece of my Heart", A.PIECE));
        mediaFiles.add(new Playable("resources/music/Strange Love.mp3",
                "Strange Love", A.STRANGE));
        mediaFiles.add(new Playable("resources/music/Sweet child o'mine.mp3",
                "Sweet Child O' Mine", A.SWEETCHILD));

        radioFiles.add(new Playable("resources/music/jazzy.mp3", "Jazzy",
                A.JAZZY));
        radioFiles.add(new Playable("resources/music/totalcar.mp3", "Totalcar",
                A.TOTALCAR));
    }

    public Source[] getSources() {
        return Source.values();
    }

    public List<String> getMediaFilesForSource(String source) {
        switch (enumValue(source)) {
        case RADIO:
            return radioFiles.stream().map(element -> element.name)
                    .collect(Collectors.toList());
        case MEDIA:
            return mediaFiles.stream().map(element -> element.name)
                    .collect(Collectors.toList());
        default:
            return null;
        }
    }

    private Source enumValue(String source) {
        return Source.valueOf(Source.class, source);
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

        String mediaFile = getList(source).get(num).uri;
        Media media = new Media(mediaFile);
        player = new MediaPlayer(media);
        setVolume();

        if (playing) {
            player.play();
            log.info("Playing " + mediaFile + " (" + num + ") from " + source);
        }
    }

    private List<Playable> getList(String source) {
        switch (enumValue(source)) {
        case MEDIA:
            return mediaFiles;
        case RADIO:
            return radioFiles;
        }
        return null;
    }

    public void trackTts(String source, int num) {
        A ttsId = getList(source).get(num).tts;
        AudioFeedback.getInstance().play(ttsId);
    }

    public void sourceTts(String source) {
        switch (enumValue(source)) {
        case MEDIA:
            AudioFeedback.getInstance().play(A.MEDIA);
            break;
        case RADIO:
            AudioFeedback.getInstance().play(A.RADIO);
            break;
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
        volume.increase();
        setVolume();
    }

    public void decreaseVolume() {
        volume.decrease();
        setVolume();
    }

    private void setVolume() {
        if (player != null) {
            player.setVolume(volume.getVolume());
        }
        log.info("Volume set to " + volume);
    }

    public void setRecordingState(boolean on) {
        if (on && !ttsActive) {
            storeVolume();
        } else if (!ttsActive) {
            loadVolume();
        }
        recordingActive = on;
    }

    private void storeVolume() {
        log.info("Recording volume set, old volme stored " + volume);
        volume.storeVolume();
        player.setVolume(volume.getVolume());
    }

    private void loadVolume() {
        volume.restoreVolume();
        player.setVolume(volume.getVolume());
        log.info("Saved volume reset " + volume);
    }

    public void setTtsState(boolean on) {
        if (on && !recordingActive) {
            storeVolume();
        } else if (!recordingActive) {
            loadVolume();
        }
        ttsActive = on;
    }

    public void dispose() {
        disposePlayer();
    }

    private void disposePlayer() {
        if (player != null) {
            player.stop();
            player.dispose();
            player = null;
        }
    }

}
