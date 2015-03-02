package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.gl.Textures;

import javax.media.opengl.GL2;

public class PlaybackControl extends DrawableObject {

    private final float scale;

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
