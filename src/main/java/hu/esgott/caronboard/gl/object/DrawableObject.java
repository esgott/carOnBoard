package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public abstract class DrawableObject {

    private static final float MAX_SPEED = 0.002f;

    private float posX = 0;
    private float posY = 0;
    private long previousTime;
    private long currentTime;
    private long elapsedTime;

    public DrawableObject() {
        currentTime = System.currentTimeMillis();
    }

    public void update(final long time) {
        previousTime = currentTime;
        currentTime = time;
        elapsedTime = currentTime - previousTime;
        updateChildren(time);
        updateObject();
    }

    public long elapsedTime() {
        return elapsedTime;
    }

    public void updateChildren(final long time) {
    }

    public void updateObject() {
    }

    public abstract void draw(final GL2 gl);

    public void move(final float x, final float y) {
        posX += x;
        posY += y;
    }

    public void moveTo(final float x, final float y) {
        posX = x;
        posY = y;
    }

    public void moveTowards(final float x, final float y) {
        float xDiff = x - posX;
        float yDiff = y - posY;
        float xDistance = MAX_SPEED * elapsedTime;
        float yDistance = MAX_SPEED * elapsedTime;
        if (xDistance < Math.abs(xDiff)) {
            if (xDiff > 0) {
                posX += xDistance;
            } else {
                posX -= xDistance;
            }
        } else {
            posX = x;
        }
        if (yDistance < Math.abs(yDiff)) {
            if (yDiff > 0) {
                posY += yDistance;
            } else {
                posY -= yDistance;
            }
        } else {
            posY = y;
        }
    }

    public boolean at(final float x, final float y) {
        return Math.abs(x - posX) < 0.001f && Math.abs(y - posY) < 0.001f;
    }

    public float X() {
        return posX;
    }

    public float Y() {
        return posY;
    }

    public abstract String getName();

}
