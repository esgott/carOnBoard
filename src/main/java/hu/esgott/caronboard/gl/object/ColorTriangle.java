package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class ColorTriangle extends DrawableObject {

    private static final float SPEED = 0.001f;

    private long previousTime;
    private float theta = 0;
    private double s = 0;
    private double c = 0;

    public ColorTriangle(final long time) {
        previousTime = time;
    }

    @Override
    public void update(final long time) {
        float delta = (time - previousTime) * SPEED;
        theta += delta;
        s = Math.sin(theta);
        c = Math.cos(theta);
        previousTime = time;
    }

    @Override
    public void draw(final GL2 gl) {
        gl.glTranslatef(X(), Y(), 0);

        gl.glBegin(GL.GL_TRIANGLES);
        gl.glColor3f(1, 0, 0);
        gl.glVertex2d(-c, -c);
        gl.glColor3f(0, 1, 0);
        gl.glVertex2d(0, c);
        gl.glColor3f(0, 0, 1);
        gl.glVertex2d(s, -s);
        gl.glEnd();
    }

    @Override
    public String getName() {
        return "triangle";
    }

}
