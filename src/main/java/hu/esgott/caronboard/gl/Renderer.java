package hu.esgott.caronboard.gl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public class Renderer implements GLEventListener {

    private static final float STEP = 0.01f;

    private double theta = 0;
    private double s = 0;
    private double c = 0;

    @Override
    public void display(final GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        update();
        render(gl);
    }

    private void update() {
        theta += STEP;
        s = Math.sin(theta);
        c = Math.cos(theta);
    }

    private void render(final GL2 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

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
    public void dispose(final GLAutoDrawable drawable) {
    }

    @Override
    public void init(final GLAutoDrawable drawable) {
        drawable.getGL().setSwapInterval(1);
    }

    @Override
    public void reshape(final GLAutoDrawable drawable, final int x,
            final int y, final int width, final int height) {
    }

}
