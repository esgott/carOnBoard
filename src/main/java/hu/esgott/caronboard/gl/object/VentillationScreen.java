package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public class VentillationScreen extends DrawableObject {

    private final DrawableList trackList = new DrawableList("SmallTrackList",
            3.4f, 0.4f, 200, 0.25f, 0.4f, true);
    private final MediaScreen mediaScreen;

    public VentillationScreen(MediaScreen mediaScreen) {
        this.mediaScreen = mediaScreen;

        trackList.move(-5.7f, -0.3f);
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
    }

    @Override
    public String getName() {
        return "VentillationScreen";
    }

    @Override
    public void selectionOn() {
        trackList.setSelected(true);
    }

    @Override
    public void selectionOff() {
        trackList.setSelected(false);
    }

    @Override
    public void backwardAction() {
        trackList.backwardAction();
        mediaScreen.prerviousTrack();
    }

    @Override
    public void forwardAction() {
        trackList.forwardAction();
        mediaScreen.nextTrack();
    }

}
