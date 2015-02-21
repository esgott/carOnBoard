package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public class Shapes {

    public static void drawRectangle(GL2 gl, final float bottomLeftX,
            final float bottomLeftY, final float topRightX,
            final float topRightY) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(bottomLeftX, bottomLeftY);
        gl.glVertex2f(topRightX, bottomLeftY);
        gl.glVertex2f(topRightX, topRightY);
        gl.glVertex2f(bottomLeftX, topRightY);
        gl.glEnd();
    }

    public static void drawTriangle(GL2 gl, final float posX, final float posY,
            final float rotation, final float scale) {
        gl.glPushMatrix();

        gl.glTranslatef(posX, posY, 0);
        gl.glScalef(scale, scale, 1.0f);
        gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);

        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex2f(0.0f, 1.0f);
        gl.glVertex2f(-1.0f, -1.0f);
        gl.glVertex2f(1.0f, -1.0f);
        gl.glEnd();

        gl.glPopMatrix();
    }

    public static void drawCircle(GL2 gl, final float radius) {
        gl.glBegin(GL2.GL_POLYGON);

        for (float i = 0.0f; i < (Math.PI * 2); i += (Math.PI * 2 / 360)) {
            float x = (float) Math.cos(i) * radius;
            float y = (float) Math.sin(i) * radius;
            gl.glVertex2f(x, y);
        }

        gl.glEnd();
    }

}
