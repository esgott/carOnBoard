package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.devices.AudioFeedback;
import hu.esgott.caronboard.devices.AudioFeedback.A;
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
            3.4f, 0.4f, 200, 0.25f, 0.4f);
    private final ImageObject map;
    private DrawableObject selected = trackList;
    private DrawableObject selectedInUpRow = temp1;
    private final MediaScreen mediaScreen;
    private final AudioFeedback audioFeedback = AudioFeedback.getInstance();

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

        temp1.setNeighbours(airSwitch, temp2);
        airSwitch.setNeighbours(temp2, temp1);
        temp2.setNeighbours(temp1, airSwitch);
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
        audioFeedback();
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
        audioFeedback();
        if (upRow()) {
            selectedInUpRow = selected;
        }
    }

    private void audioFeedback() {
        if (selected == temp1) {
            audioFeedback.play(A.LEFT_TEMP);
        } else if (selected == airSwitch) {
            audioFeedback.play(A.AIRSWITCH);
        } else if (selected == temp2) {
            audioFeedback.play(A.RIGHT_TEMP);
        } else if (selected == trackList) {
            mediaScreen.trackTts();
        } else if (selected == map) {
            audioFeedback.play(A.NAVIGATION);
        }
    }

    @Override
    public void selectPrevious() {
        select(selected.getPrevious());
    }

    @Override
    public void selectUp() {
        if (upRow()) {
            select(map);
        } else if (selected == trackList) {
            select(selectedInUpRow);
        } else {
            select(trackList);
        }
    }

    private boolean upRow() {
        return selected == temp1 || selected == airSwitch || selected == temp2;
    }

    @Override
    public void selectDown() {
        if (upRow()) {
            select(trackList);
        } else if (selected == trackList) {
            select(map);
        } else {
            select(selectedInUpRow);
        }
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
