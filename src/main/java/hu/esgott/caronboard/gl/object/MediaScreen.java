package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.leap.AudioFeedback;

import java.util.logging.Logger;

import javax.media.opengl.GL2;

public class MediaScreen extends DrawableObject {

    private final Logger log = Logger.getLogger(getClass().getName());

    private final DrawableList sourceList = new DrawableList(3.4f, 0.35f, 200,
            0.25f, -0.15f);
    private final DrawableList trackList = new DrawableList(3.4f, 0.9f, 200,
            0.25f, 0.15f);
    private final PlaybackControl playbackControl = new PlaybackControl(0.2f);
    private DrawableObject selected;

    public MediaScreen() {
        sourceList.move(-1.7f, 0.6f);
        trackList.move(-1.7f, -0.4f);
        playbackControl.move(0.0f, -0.7f);

        sourceList.setNeighbours(trackList, playbackControl);
        trackList.setNeighbours(playbackControl, sourceList);
        playbackControl.setNeighbours(sourceList, trackList);

        select(trackList);
    }

    private void select(DrawableObject newSelection) {
        if (selected != null) {
            selected.setSelected(false);
        }
        newSelection.setSelected(true);
        selected = newSelection;
        log.info("Selected: " + selected.getName());
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
            break;
        case "previous_track":
            trackList.backwardAction();
            break;
        case "next_source":
            sourceList.forwardAction();
            break;
        case "previous_source":
            sourceList.backwardAction();
            break;
        case "play":
            log.info("Play");
            // TODO connect play
            break;
        case "pause":
            log.info("Pause");
            // TODO connect pause
            break;
        default:
            log.info("Unrecognized match " + match);
        }
    }

    @Override
    public void backwardAction() {
        selected.backwardAction();
    }

    @Override
    public void forwardAction() {
        selected.forwardAction();
    }

    @Override
    public void selectNext() {
        AudioFeedback.play(AudioFeedback.A.CORRECT);
        select(selected.getNext());
    }

    @Override
    public void selectPrevious() {
        AudioFeedback.play(AudioFeedback.A.CORRECT);
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
}
