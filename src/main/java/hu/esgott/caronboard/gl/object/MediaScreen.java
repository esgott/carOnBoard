package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public class MediaScreen extends DrawableObject {

    private final DrawableList sourceList = new DrawableList(3.4f, 0.35f, 200,
            0.25f, -0.15f);
    private final DrawableList trackList = new DrawableList(3.4f, 0.9f, 200,
            0.25f, 0.15f);
    private final PlaybackControl playbackControl = new PlaybackControl(0.2f);

    public MediaScreen() {
        sourceList.move(-1.7f, 0.6f);
        trackList.move(-1.7f, -0.4f);
        playbackControl.move(0.0f, -0.7f);
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

    @Override
    public void backwardAction() {
        trackList.backwardAction();
        sourceList.backwardAction();
    }

    @Override
    public void forwardAction() {
        trackList.forwardAction();
        sourceList.forwardAction();
    }

}
