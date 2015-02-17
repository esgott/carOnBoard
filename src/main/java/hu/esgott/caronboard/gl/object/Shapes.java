package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public class Shapes {

    public static void drawRectangle(GL2 gl, final float bottomLeftX,
            final float bottomLeftY, final float topRightX,
            final float topRightY) {
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex2f(bottomLeftX, bottomLeftY);
        gl.glVertex2f(topRightX, bottomLeftY);
        gl.glVertex2f(topRightX, topRightY);
        gl.glVertex2f(bottomLeftX, topRightY);
        gl.glEnd();
    }

}
