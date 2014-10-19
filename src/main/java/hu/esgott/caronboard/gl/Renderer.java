package hu.esgott.caronboard.gl;

import hu.esgott.caronboard.gl.object.ColorTriangle;
import hu.esgott.caronboard.gl.object.DrawableObject;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class Renderer implements GLEventListener, KeyListener {

    private static final float CAMERA_SPEED = 0.05f;

    private GLU glu = new GLU();
    private List<DrawableObject> objects = new ArrayList<>();
    private float cameraX = 0;
    private float cameraY = 0;

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

        gl.glPushMatrix();
        gl.glTranslatef(cameraX, cameraY, 0.0f);

        objects.stream().forEach(object -> {
            gl.glPushMatrix();
            object.draw(gl);
            gl.glPopMatrix();
        });

        gl.glPopMatrix();
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

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
        case KeyEvent.VK_Q:
            System.exit(0);
            break;
        case KeyEvent.VK_LEFT:
            cameraX -= CAMERA_SPEED;
            break;
        case KeyEvent.VK_RIGHT:
            cameraX += CAMERA_SPEED;
            break;
        case KeyEvent.VK_UP:
            cameraY += CAMERA_SPEED;
            break;
        case KeyEvent.VK_DOWN:
            cameraY -= CAMERA_SPEED;
            break;
        default:
            System.out.println("Not expected key");
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

}
