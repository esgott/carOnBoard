package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public class MediaScreen extends DrawableObject {

    private DrawableList sourceList = new DrawableList(3.4f, 0.35f, 200, 0.25f,
            -0.15f);
    private DrawableList trackList = new DrawableList(3.4f, 0.9f, 200, 0.25f,
            0.15f);

    public MediaScreen() {
        sourceList.move(-1.7f, 0.6f);
        trackList.move(-1.7f, -0.4f);
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
