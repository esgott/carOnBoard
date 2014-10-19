package hu.esgott.caronboard.gl;

import javax.media.opengl.GL2;

public class Camera {

    private static final float CAMERA_SPEED = 0.05f;

    private float x = 0;
    private float y = 0;

    public void move(final GL2 gl) {
        gl.glTranslatef(x, y, 0.0f);
    }

    public void up() {
        y += CAMERA_SPEED;
    }

    public void down() {
        y -= CAMERA_SPEED;
    }

    public void left() {
        x -= CAMERA_SPEED;
    }

    public void right() {
        x += CAMERA_SPEED;
    }

}
