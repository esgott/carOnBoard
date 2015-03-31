package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.gl.Textures;

import javax.media.opengl.GL2;

public class PlaybackControl extends DrawableObject {

    private final float scale;
    private boolean playback = false;

    public PlaybackControl(final Textures textures, final float scale) {
        this.scale = scale;
    }

    @Override
    public void draw(GL2 gl) {
        if (selected()) {
            gl.glColor3f(1, 1, 0);
            Shapes.drawFrame(gl, X() - 1.7f, Y() - 0.24f, X() + 1.7f,
                    Y() + 0.24f, 0.01f);
        }

        gl.glColor3f(0.0f, 0.05f, 0.4f);

        if (playback) {
            Shapes.drawTriangle(gl, X(), Y(), -90.0f, scale);
        } else {
            Shapes.drawRectangle(gl, -0.2f, -0.9f, -0.04f, -0.5f);
            Shapes.drawRectangle(gl, 0.04f, -0.9f, 0.2f, -0.5f);
        }

        Shapes.drawTriangle(gl, X() + 0.9f, Y(), -90.0f, scale);
        Shapes.drawTriangle(gl, X() + 1.31f, Y(), -90.0f, scale);

        Shapes.drawTriangle(gl, X() - 0.9f, Y(), 90.0f, scale);
        Shapes.drawTriangle(gl, X() - 1.31f, Y(), 90.0f, scale);
    }

    public void setPlayback(boolean playing) {
        playback = playing;
    }

    public void togglePlayback() {
        playback = !playback;
    }

    @Override
    public String getName() {
        return "PlaybackControl";
    }

}
