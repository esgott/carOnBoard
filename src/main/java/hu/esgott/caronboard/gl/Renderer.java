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

    private static final float MOVE_SPEED = 0.05f;

    private GLU glu = new GLU();
    private List<DrawableObject> objects = new ArrayList<>();
    private int selectedObject = 0;
    private Camera camera = new Camera();

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
        camera.move(gl);

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
            camera.left();
            break;
        case KeyEvent.VK_RIGHT:
            camera.right();
            break;
        case KeyEvent.VK_UP:
            camera.up();
            break;
        case KeyEvent.VK_DOWN:
            camera.down();
            break;
        case KeyEvent.VK_PAGE_DOWN:
            selectNextObject();
            break;
        case KeyEvent.VK_PAGE_UP:
            selectPreviousObject();
            break;
        case KeyEvent.VK_W:
            objects.get(selectedObject).move(0, MOVE_SPEED);
            break;
        case KeyEvent.VK_S:
            objects.get(selectedObject).move(0, -MOVE_SPEED);
            break;
        case KeyEvent.VK_A:
            objects.get(selectedObject).move(-MOVE_SPEED, 0);
            break;
        case KeyEvent.VK_D:
            objects.get(selectedObject).move(MOVE_SPEED, 0);
            break;
        default:
            System.out.println("Not expected key");
        }
    }

    private void selectNextObject() {
        if (selectedObject < objects.size() - 1) {
            selectedObject++;
        } else {
            selectedObject = 0;
        }
        System.out.println(objects.get(selectedObject).getName());
    }

    private void selectPreviousObject() {
        if (selectedObject > 0) {
            selectedObject--;
        } else {
            selectedObject = objects.size() - 1;
        }
        System.out.println(objects.get(selectedObject).getName());
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

}
