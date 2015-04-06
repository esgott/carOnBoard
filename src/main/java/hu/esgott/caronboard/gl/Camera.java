package hu.esgott.caronboard.gl;

import javax.media.opengl.GL2;

public class Camera {

    private static final float CAMERA_SPEED = 0.05f;

    private long previousTime = 0;
    private float x = 0;
    private float realX = 0;
    private float y = 0;

    public void move(final GL2 gl) {
        gl.glTranslatef(realX, y, 0.0f);
    }

    public void update(long time) {
        long elapsed = time - previousTime;
        float diff = Math.abs(realX - x);
        float s = CAMERA_SPEED * elapsed;
        if (s < diff) {
            if (realX < x) {
                realX += s;
            } else {
                realX -= s;
            }
        } else {
            realX = x;
        }
        previousTime = time;
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

    public void left(float distance) {
        x -= distance;
    }

    public void right() {
        x += CAMERA_SPEED;
    }

    public void right(float distance) {
        x += distance;
    }

}
