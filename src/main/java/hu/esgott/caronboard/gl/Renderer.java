package hu.esgott.caronboard.gl;

import hu.esgott.caronboard.gl.object.ColorTriangle;
import hu.esgott.caronboard.gl.object.DrawableObject;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class Renderer implements GLEventListener {

    private GLU glu = new GLU();
    private List<DrawableObject> objects = new ArrayList<>();

    public Renderer() {
        long time = System.currentTimeMillis();
        objects.add(new ColorTriangle(time));
    }

    @Override
    public void display(final GLAutoDrawable drawable) {
        long time = System.currentTimeMillis();
        update(time);
        GL2 gl = drawable.getGL().getGL2();
        render(gl);
    }

    private void update(final long time) {
        objects.stream().forEach(object -> object.update(time));
    }

    private void render(final GL2 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        objects.stream().forEach(object -> object.draw(gl));
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
        final float nonZeroHeight;
        if (height == 0) {
            nonZeroHeight = 1;
        } else {
            nonZeroHeight = height;
        }
        GL2 gl = drawable.getGL().getGL2();
        float aspect = (float) width / nonZeroHeight;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (width > height) {
            glu.gluOrtho2D(-1.0 * aspect, 1.0 * aspect, -1.0, 1.0);
        } else {
            glu.gluOrtho2D(-1.0, 1.0, -1.0 / aspect, 1.0 / aspect);
        }
    }

}
