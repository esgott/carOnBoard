package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.gl.Textures;
import hu.esgott.caronboard.gl.Textures.ID;

import javax.media.opengl.GL2;

public class PlaybackControl extends DrawableObject {

    private final float scale;
    private final Textures textures;

    public PlaybackControl(final Textures textures, final float scale) {
        this.scale = scale;
        this.textures = textures;
    }

    @Override
    public void draw(GL2 gl) {
        if (selected()) {
            gl.glColor3f(1, 1, 0);
            Shapes.drawRectangle(gl, X() - 1.71f, Y() - 0.25f, X() + 1.71f,
                    Y() + 0.25f);
            gl.glColor3f(0, 0, 0);
            Shapes.drawRectangle(gl, X() - 1.7f, Y() - 0.24f, X() + 1.7f,
                    Y() + 0.24f);
        }

        textures.enableTexture(ID.METAL, gl);

        Shapes.drawTriangle(gl, X(), Y(), -90.0f, scale);

        Shapes.drawTriangle(gl, X() + 0.9f, Y(), -90.0f, scale);
        Shapes.drawTriangle(gl, X() + 1.31f, Y(), -90.0f, scale);

        Shapes.drawTriangle(gl, X() - 0.9f, Y(), 90.0f, scale);
        Shapes.drawTriangle(gl, X() - 1.31f, Y(), 90.0f, scale);

        textures.disableTexture(ID.METAL, gl);
    }

    @Override
    public String getName() {
        return "PlaybackControl";
    }

}
