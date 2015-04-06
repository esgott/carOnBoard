package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.devices.AudioFeedback;
import hu.esgott.caronboard.devices.MediaPlayerDevice;
import hu.esgott.caronboard.devices.MediaPlayerDevice.Source;
import hu.esgott.caronboard.gl.Textures;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.media.opengl.GL2;

public class MediaScreen extends DrawableObject {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final int SEEK_MILLISEC = 2000;

    private final DrawableList sourceList = new DrawableList("SourceList",
            3.4f, 0.4f, 200, 0.25f, 0.4f, true);
    private final DrawableList trackList = new DrawableList("TrackList", 3.4f,
            0.9f, 200, 0.25f, 0.62f);
    private final PlaybackControl playbackControl;
    private DrawableObject selected;
    private final MediaPlayerDevice playerDevice = new MediaPlayerDevice();
    private final AudioFeedback audioFeedback = AudioFeedback.getInstance();

    public MediaScreen(Textures textures) {
        playbackControl = new PlaybackControl(textures, 0.2f);

        sourceList.move(-1.7f, 0.55f);
        trackList.move(-1.7f, -0.4f);
        playbackControl.move(0.0f, -0.7f);

        sourceList.setNeighbours(trackList, playbackControl);
        trackList.setNeighbours(playbackControl, sourceList);
        playbackControl.setNeighbours(sourceList, trackList);

        sourceList.setElements(playerDevice.getSourceNames());
        updateTrackList();

        trackList.displaySelection(true);
        select(trackList);
        updateAudio(true);
        selectionOff();
    }

    private void select(DrawableObject newSelection) {
        if (selected != null) {
            selected.setSelected(false);
        }
        newSelection.setSelected(true);
        selected = newSelection;
        log.info("Selected: " + selected.getName());
    }

    private void updateAudio(boolean forward) {
        String source = sourceList.getSelectedName();
        int track = trackList.getSelectedNum();
        if (sourceList.changed()) {
            playerDevice.sourceAndTrackTts(source, track);
        } else if (trackList.changed()) {
            playerDevice.trackTts(source, track);
        }
        if (sourceList.changed() || trackList.changed()) {
            playerDevice.select(source, track);
        } else if (selected == playbackControl) {
            if (forward) {
                playerDevice.seek(SEEK_MILLISEC);
            } else {
                playerDevice.seek(-SEEK_MILLISEC);
            }
        }
    }

    private void updateTrackList() {
        if (sourceList.changed()) {
            trackList.setElements(playerDevice
                    .getMediaFilesForSource(sourceList.getSelectedName()));
        }
    }

    @Override
    public void updateChildren(final long time) {
        sourceList.update(time);
        trackList.update(time);
    }

    @Override
    public void draw(GL2 gl) {
        sourceList.draw(gl);
        trackList.draw(gl);
        playbackControl.draw(gl);
    }

    @Override
    public String getName() {
        return "MediaScreen";
    }

    public void processMatch(String match) {
        switch (match) {
        case "next_track":
            trackList.forwardAction();
            updateAudio(true);
            break;
        case "previous_track":
            trackList.backwardAction();
            updateAudio(false);
            break;
        case "next_source":
            sourceList.forwardAction();
            updateTrackList();
            updateAudio(true);
            break;
        case "previous_source":
            sourceList.backwardAction();
            updateTrackList();
            updateAudio(false);
            break;
        case "play":
            playerDevice.play();
            playbackControl.setPlayback(true);
            break;
        case "pause":
            playerDevice.pause();
            playbackControl.setPlayback(false);
            break;
        case "media":
            int mediaIndex = Arrays.asList(playerDevice.getSources()).indexOf(
                    Source.MEDIA);
            sourceList.select(mediaIndex);
            updateTrackList();
            updateAudio(false);
            break;
        case "radio":
            int radioIndex = Arrays.asList(playerDevice.getSources()).indexOf(
                    Source.RADIO);
            sourceList.select(radioIndex);
            updateTrackList();
            updateAudio(false);
            break;
        default:
            log.info("Unrecognized match " + match);
        }
    }

    @Override
    public void backwardAction() {
        selected.backwardAction();
        if (selected == sourceList) {
            updateTrackList();
        }
        updateAudio(false);
    }

    @Override
    public void forwardAction() {
        selected.forwardAction();
        if (selected == sourceList) {
            updateTrackList();
        }
        updateAudio(true);
    }

    @Override
    public void selectNext() {
        audioFeedback.play(AudioFeedback.A.BTN_BEEP);
        select(selected.getNext());
    }

    @Override
    public void selectPrevious() {
        audioFeedback.play(AudioFeedback.A.BTN_BEEP);
        select(selected.getPrevious());
    }

    public void selectionOn() {
        selected.setSelected(true);
        log.info("Selection ON");
    }

    public void selectionOff() {
        selected.setSelected(false);
        log.info("Selection OFF");
    }

    public void playPause() {
        playerDevice.togglePause();
        playbackControl.togglePlayback();
    }

    public void increaseVolume() {
        playerDevice.increaseVolume();
    }

    public void decreaseVolume() {
        playerDevice.decreaseVolume();
    }

    public void setRecordingState(boolean on) {
        playerDevice.setRecordingState(on);
    }

    public void setTtsState(boolean on) {
        playerDevice.setTtsState(on);
    }

    public void dispose() {
        playerDevice.dispose();
    }
}
