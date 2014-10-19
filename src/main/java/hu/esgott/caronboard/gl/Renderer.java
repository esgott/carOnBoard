package hu.esgott.caronboard.gl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public class Renderer implements GLEventListener {

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glColor3f(1, 0, 0);
        gl.glVertex2f(-1, -1);
        gl.glColor3f(0, 1, 0);
        gl.glVertex2f(0, 1);
        gl.glColor3f(0, 0, 1);
        gl.glVertex2f(1, -1);
        gl.glEnd();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int length) {
        // TODO Auto-generated method stub

    }

}
