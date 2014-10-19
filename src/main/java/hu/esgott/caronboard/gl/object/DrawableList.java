package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public class DrawableList extends DrawableObject {

    private float width = 0.0f;
    private float height = 0.0f;

    public DrawableList(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(long time) {
    }

    @Override
    public void draw(GL2 gl) {
        gl.glTranslatef(X(), Y(), 0);

        gl.glColor3f(1, 0, 0);
        drawRectangle(gl, X(), Y(), X() + width, Y() + height);
        gl.glColor3f(0, 0, 1);
        drawRectangle(gl, X() + 0.1f, Y() + 0.1f, X() + width - 0.1f, Y()
                + height - 0.1f);
    }

    private void drawRectangle(GL2 gl, final float bottomLeftX,
            final float bottomLeftY, final float topRightX,
            final float topRightY) {
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex2f(bottomLeftX, bottomLeftY);
        gl.glVertex2f(topRightX, bottomLeftY);
        gl.glVertex2f(topRightX, topRightY);
        gl.glVertex2f(bottomLeftX, topRightY);
        gl.glEnd();
    }

    @Override
    public String getName() {
        return "list";
    }

}
