package hu.esgott.caronboard.devices;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class MediaPlayerDevice implements BasicPlayerListener {

    public enum Source {
        RADIO, MEDIA
    }

    private final Logger log = Logger.getLogger(getClass().getName());

    private final List<String> mediaFiles = new ArrayList<>();
    private final List<String> radioFiles = new ArrayList<>();
    private BasicPlayer player;

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
            playing = player.getStatus() == BasicPlayer.PLAYING;
            dispose();
        }

        Source activeSource = Source.valueOf(Source.class, source);
        player = new BasicPlayer();
        player.addBasicPlayerListener(this);

        List<String> fileList = getMediaFilesForSource(activeSource);
        String mediaFile = fileList.get(num);

        try {
            player.open(new File(mediaFile));
            if (playing) {
                player.play();
            }
        } catch (BasicPlayerException e) {
            log.severe("Error on selection: " + e.getMessage());
        }
    }

    public void togglePause() {
        if (player != null) {
            try {
                if (player.getStatus() == BasicPlayer.PLAYING) {
                    player.pause();
                    log.info("Playback paused");
                } else if (player.getStatus() == BasicPlayer.PAUSED) {
                    player.resume();
                    log.info("Playback resumed");
                } else {
                    player.play();
                    log.info("Start playing in state " + player.getStatus());
                }
            } catch (BasicPlayerException e) {
                log.severe("Error when pausing: " + e.getMessage());
            }
        } else {
            log.warning("No player created.");
        }
    }

    public void dispose() {
        if (player != null) {
            try {
                player.stop();
                player = null;
            } catch (BasicPlayerException e) {
                log.severe("Error disposing player: " + e.getMessage());
            }
        }
    }

    @Override
    public void opened(Object stream, Map properties) {
        log.info("Opened");
    }

    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata,
            Map properties) {
    }

    @Override
    public void setController(BasicController controller) {
    }

    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        log.info("State: " + event);
    }

}
