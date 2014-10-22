package hu.esgott.caronboard.gl.object;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

public class DrawableList extends DrawableObject {

    private float width = 0.0f;
    private float height = 0.0f;
    private List<Text> items = new ArrayList<>();

    public DrawableList(float width, float height) {
        this.width = width;
        this.height = height;
        final int size = 350;
        Text item1 = new Text("item1", size);
        item1.move(0.2f, 0.2f);
        items.add(item1);
        Text item2 = new Text("item2", size);
        item2.move(0.2f, 0.75f);
        items.add(item2);
        Text item3 = new Text("item3", size);
        item3.move(0.2f, 1.35f);
        items.add(item3);
    }

    @Override
    public void update(long time) {
    }

    @Override
    public void draw(GL2 gl) {
        gl.glTranslatef(X(), Y(), 0);

        gl.glColor3f(1, 0, 0);
        drawRectangle(gl, 0, 0, width, height);
        gl.glColor3f(0, 0, 1);
        drawRectangle(gl, 0.1f, 0.1f, width - 0.1f, height - 0.1f);

        items.stream().forEach(item -> {
            gl.glPushMatrix();
            item.draw(gl);
            gl.glPopMatrix();
        });
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
