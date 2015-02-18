package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public class PlaybackControl extends DrawableObject {

    private final float scale;

    public PlaybackControl(final float scale) {
        this.scale = scale;
    }

    @Override
    public void draw(GL2 gl) {
        gl.glColor3f(0, 0, 1);

        Shapes.drawTriangle(gl, X(), Y(), -90.0f, scale);

        Shapes.drawTriangle(gl, X() + 0.9f, Y(), -90.0f, scale);
        Shapes.drawTriangle(gl, X() + 1.31f, Y(), -90.0f, scale);

        Shapes.drawTriangle(gl, X() - 0.9f, Y(), 90.0f, scale);
        Shapes.drawTriangle(gl, X() - 1.31f, Y(), 90.0f, scale);
    }

    @Override
    public String getName() {
        return "PlaybackControl";
    }

}
