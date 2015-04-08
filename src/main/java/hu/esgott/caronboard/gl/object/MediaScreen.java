package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;
import hu.esgott.caronboard.devices.AudioFeedback;
import hu.esgott.caronboard.devices.MediaPlayerDevice;
import hu.esgott.caronboard.devices.MediaPlayerDevice.Source;
import hu.esgott.caronboard.gl.Textures;

import java.util.Arrays;
import java.util.List;
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
    private final Text progress = new Text("--:--/--:--", 90);
    private DrawableObject selected;
    private final MediaPlayerDevice playerDevice = new MediaPlayerDevice();
    private final AudioFeedback audioFeedback = AudioFeedback.getInstance();

    public MediaScreen(Textures textures) {
        playbackControl = new PlaybackControl(textures, 0.2f);

        sourceList.move(-1.7f, 0.55f);
        trackList.move(-1.7f, -0.4f);
        playbackControl.move(0.0f, -0.7f);
        progress.move(1.2f, -0.5f);

        sourceList.setNeighbours(trackList, playbackControl);
        trackList.setNeighbours(playbackControl, sourceList);
        playbackControl.setNeighbours(sourceList, trackList);

        sourceList.setElements(playerDevice.getSourceNames());
        updateTrackList();

        trackList.displaySelection(true);
        select(trackList);
        updateAudio(true);
        CommandQueue.getInstance().notifyGui(GuiCommand.TRACK_CHANGED);
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

    public List<String> getTracks() {
        return playerDevice
                .getMediaFilesForSource(sourceList.getSelectedName());
    }

    public int getSelectedTrack() {
        return trackList.getSelectedNum();
    }

    @Override
    public void updateChildren(final long time) {
        sourceList.update(time);
        trackList.update(time);
    }

    @Override
    public void updateObject() {
        String current = playerDevice.getCurrentTime();
        String total = playerDevice.getTotalTime();
        progress.setText(current + "/" + total);
    }

    @Override
    public void draw(GL2 gl) {
        sourceList.draw(gl);
        trackList.draw(gl);
        playbackControl.draw(gl);
        drawProgress(gl);
    }

    private void drawProgress(GL2 gl) {
        gl.glPushMatrix();
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        progress.draw(gl);
        gl.glPopMatrix();
    }

    @Override
    public String getName() {
        return "MediaScreen";
    }

    public void processMatch(String match) {
        switch (match) {
        case "next_track":
            nextTrack();
            CommandQueue.getInstance().notifyGui(GuiCommand.TRACK_CHANGED);
            break;
        case "previous_track":
            prerviousTrack();
            CommandQueue.getInstance().notifyGui(GuiCommand.TRACK_CHANGED);
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

    public void nextTrack() {
        trackList.forwardAction();
        updateAudio(true);
    }

    public void prerviousTrack() {
        trackList.backwardAction();
        updateAudio(false);
    }

    @Override
    public void backwardAction() {
        selected.backwardAction();
        if (selected == sourceList) {
            updateTrackList();
        }
        if (selected != playbackControl) {
            CommandQueue.getInstance().notifyGui(GuiCommand.TRACK_CHANGED);
        }
        updateAudio(false);
    }

    @Override
    public void forwardAction() {
        selected.forwardAction();
        if (selected == sourceList) {
            updateTrackList();
        }
        if (selected != playbackControl) {
            CommandQueue.getInstance().notifyGui(GuiCommand.TRACK_CHANGED);
        }
        updateAudio(true);
    }

    @Override
    public void selectNext() {
        select(selected.getNext());
        playSelectionTts();
    }

    private void playSelectionTts() {
        String source = sourceList.getSelectedName();
        if (selected == sourceList) {
            playerDevice.sourceTts(source);
        } else if (selected == trackList) {
            int track = trackList.getSelectedNum();
            playerDevice.trackTts(source, track);
        } else {
            audioFeedback.play(AudioFeedback.A.BTN_BEEP);
        }
    }

    @Override
    public void selectPrevious() {
        select(selected.getPrevious());
        playSelectionTts();
    }

    @Override
    public void selectDown() {
        selectNext();
    }

    @Override
    public void selectUp() {
        selectPrevious();
    }

    @Override
    public void selectionOn() {
        selected.setSelected(true);
        log.info("Selection ON");
    }

    @Override
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

    public void trackTts() {
        String source = sourceList.getSelectedName();
        int track = trackList.getSelectedNum();
        playerDevice.trackTts(source, track);
    }
}
