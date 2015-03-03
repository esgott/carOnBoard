package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.devices.MediaPlayerDevice;
import hu.esgott.caronboard.devices.MediaPlayerDevice.Source;
import hu.esgott.caronboard.gl.Textures;
import hu.esgott.caronboard.leap.AudioFeedback;

import java.util.logging.Logger;

import javax.media.opengl.GL2;

public class MediaScreen extends DrawableObject {

    private final Logger log = Logger.getLogger(getClass().getName());

    private final DrawableList sourceList = new DrawableList("SourceList",
            3.4f, 0.35f, 200, 0.25f, 0.35f);
    private final DrawableList trackList = new DrawableList("TrackList", 3.4f,
            0.9f, 200, 0.25f, 0.62f);
    private final PlaybackControl playbackControl;
    private DrawableObject selected;
    private final MediaPlayerDevice playerDevice = new MediaPlayerDevice();
    private final AudioFeedback audioFeedback = AudioFeedback.getInstance();

    public MediaScreen(Textures textures) {
        playbackControl = new PlaybackControl(textures, 0.2f);

        sourceList.move(-1.7f, 0.6f);
        trackList.move(-1.7f, -0.4f);
        playbackControl.move(0.0f, -0.7f);

        sourceList.setNeighbours(trackList, playbackControl);
        trackList.setNeighbours(playbackControl, sourceList);
        playbackControl.setNeighbours(sourceList, trackList);

        sourceList.setElements(playerDevice.getSourceNames());
        trackList
                .setElements(playerDevice.getMediaFilesForSource(Source.MEDIA));

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
        if ((selected == sourceList && sourceList.changed())
                || (selected == trackList && trackList.changed())) {
            String source = sourceList.getSelectedName();
            int track = trackList.getSelectedNum();
            if (!source.equals("MEDIA")) {
                track = 0;
            }
            playerDevice.select(source, track);
        } else if (selected == playbackControl) {
            if (forward) {
                playerDevice.seek(2000);
            } else {
                playerDevice.seek(-2000);
            }
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
            updateAudio(true);
            break;
        case "previous_source":
            sourceList.backwardAction();
            updateAudio(false);
            break;
        case "play":
            playerDevice.play();
            break;
        case "pause":
            playerDevice.pause();
            break;
        default:
            log.info("Unrecognized match " + match);
        }
    }

    @Override
    public void backwardAction() {
        selected.backwardAction();
        updateAudio(false);
    }

    @Override
    public void forwardAction() {
        selected.forwardAction();
        updateAudio(true);
    }

    @Override
    public void selectNext() {
        audioFeedback.play(AudioFeedback.A.CORRECT);
        select(selected.getNext());
    }

    @Override
    public void selectPrevious() {
        audioFeedback.play(AudioFeedback.A.CORRECT);
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
    }

    public void increaseVolume() {
        audioFeedback.increaseVolume();
        audioFeedback.play(AudioFeedback.A.BTN_BEEP);
        playerDevice.increaseVolume();
    }

    public void decreaseVolume() {
        audioFeedback.decreaseVolume();
        audioFeedback.play(AudioFeedback.A.BTN_BEEP);
        playerDevice.decreaseVolume();
    }

    public void setRecorderVolume() {
        playerDevice.setRecordingVolume();
    }

    public void setSavedVolume() {
        playerDevice.setSavedVolume();
    }

    public void dispose() {
        playerDevice.dispose();
    }
}
