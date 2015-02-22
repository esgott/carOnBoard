package hu.esgott.caronboard.devices;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
    private boolean playing = true;
    private Source activeSource = Source.RADIO;
    private BasicPlayer player;
    private int currentFile = 0;

    public MediaPlayerDevice() {
        String sultans = "Sultans of swing.mp3";

        mediaFiles.add(sultans);
        // TODO add media files

        radioFiles.add(sultans);
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

    public boolean playing() {
        return playing;
    }

    public void play() {
        String mediaFile = getMediaFilesForSource(activeSource)
                .get(currentFile);
        player = new BasicPlayer();
        player.addBasicPlayerListener(this);
        try {
            player.open(new File(mediaFile));
            player.play();
        } catch (BasicPlayerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("Playback started");
    }

    public void dispose() {
        if (player != null) {
            try {
                player.stop();
            } catch (BasicPlayerException e) {
                log.severe("Error disposing player: " + e.getMessage());
            }
        }
    }

    @Override
    public void opened(Object arg0, Map arg1) {
        log.info("Opened");
    }

    @Override
    public void progress(int arg0, long arg1, byte[] arg2, Map arg3) {
    }

    @Override
    public void setController(BasicController arg0) {
    }

    @Override
    public void stateUpdated(BasicPlayerEvent arg0) {
        log.info("State: " + arg0);
    }

}
