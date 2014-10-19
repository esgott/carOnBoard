package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public abstract class DrawableObject {

    private float posX = 0;
    private float posY = 0;

    public abstract void update(final long time);

    public abstract void draw(final GL2 gl);

    public void move(final float x, final float y) {
        posX += x;
        posY += y;
    }

    public float X() {
        return posX;
    }

    public float Y() {
        return posY;
    }

    public abstract String getName();

}
