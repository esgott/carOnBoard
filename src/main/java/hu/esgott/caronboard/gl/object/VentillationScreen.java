package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.gl.Textures;
import hu.esgott.caronboard.gl.Textures.ID;

import java.util.logging.Logger;

import javax.media.opengl.GL2;

public class VentillationScreen extends DrawableObject {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final float CIRCULAR_RADIUS = 0.4f;

    private final TemperatureDisplay temp1 = new TemperatureDisplay(
            CIRCULAR_RADIUS);
    private final AirSwitch airSwitch;
    private final TemperatureDisplay temp2 = new TemperatureDisplay(
            CIRCULAR_RADIUS);
    private final DrawableList trackList = new DrawableList("SmallTrackList",
            3.4f, 0.4f, 200, 0.25f, 0.4f, true);
    private final ImageObject map;
    private DrawableObject selected = trackList;
    private final MediaScreen mediaScreen;

    public VentillationScreen(final MediaScreen mediaScreen,
            final Textures textures) {
        this.mediaScreen = mediaScreen;
        airSwitch = new AirSwitch(CIRCULAR_RADIUS - 0.1f, textures);
        map = new ImageObject(textures, ID.MAP, 3.4f, 0.58f);

        temp1.move(-5.1f, 0.55f);
        airSwitch.move(-4.0f, 0.50f);
        temp2.move(-2.9f, 0.55f);
        trackList.move(-5.7f, -0.3f);
        map.move(-4.0f, -0.65f);

        temp1.setNeighbours(airSwitch, map);
        airSwitch.setNeighbours(temp2, temp1);
        temp2.setNeighbours(trackList, airSwitch);
        trackList.setNeighbours(map, temp2);
        map.setNeighbours(temp1, trackList);
    }

    @Override
    public void updateChildren(long time) {
        airSwitch.update(time);
        trackList.update(time);
    };

    public void syncTrackList() {
        trackList.setElements(mediaScreen.getTracks());
        trackList.select(mediaScreen.getSelectedTrack());
    }

    @Override
    public void draw(GL2 gl) {
        temp1.draw(gl);
        airSwitch.draw(gl);
        temp2.draw(gl);
        trackList.draw(gl);
        map.draw(gl);
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
