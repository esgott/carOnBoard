package hu.esgott.caronboard.gl.object;

import java.util.logging.Logger;

import javax.media.opengl.GL2;

public class VentillationScreen extends DrawableObject {

    private final Logger log = Logger.getLogger(getClass().getName());

    private final DrawableList trackList = new DrawableList("SmallTrackList",
            3.4f, 0.4f, 200, 0.25f, 0.4f, true);
    private final TemperatureDisplay temp1 = new TemperatureDisplay(0.4f);
    private DrawableObject selected = trackList;
    private final MediaScreen mediaScreen;

    public VentillationScreen(MediaScreen mediaScreen) {
        this.mediaScreen = mediaScreen;

        trackList.move(-5.7f, -0.3f);
        temp1.move(-5.1f, 0.55f);

        trackList.setNeighbours(temp1, temp1);
        temp1.setNeighbours(trackList, trackList);
    }

    @Override
    public void updateChildren(long time) {
        trackList.update(time);
    };

    public void syncTrackList() {
        trackList.setElements(mediaScreen.getTracks());
        trackList.select(mediaScreen.getSelectedTrack());
    }

    @Override
    public void draw(GL2 gl) {
        trackList.draw(gl);
        temp1.draw(gl);
    }

    @Override
    public String getName() {
        return "VentillationScreen";
    }

    @Override
    public void selectionOn() {
        selected.setSelected(true);
    }

    @Override
    public void selectionOff() {
        selected.setSelected(false);
    }

    @Override
    public void selectNext() {
        select(selected.getNext());
    }

    private void select(DrawableObject selected) {
        this.selected.setSelected(false);
        selected.setSelected(true);
        this.selected = selected;
        log.info("Selected: " + selected.getName());
    }

    @Override
    public void selectPrevious() {
        select(selected.getPrevious());
    }

    @Override
    public void backwardAction() {
        selected.backwardAction();
        if (selected == trackList) {
            mediaScreen.prerviousTrack();
        }
    }

    @Override
    public void forwardAction() {
        selected.forwardAction();
        if (selected == trackList) {
            mediaScreen.nextTrack();
        }
    }

}
