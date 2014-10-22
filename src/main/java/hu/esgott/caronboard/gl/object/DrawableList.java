package hu.esgott.caronboard.gl.object;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL2;

public class DrawableList extends DrawableObject {

    private static final float DIFF = 0.575f;

    private float width = 0.0f;
    private float height = 0.0f;
    private List<Text> items = new ArrayList<>();
    private List<Float> positions = new ArrayList<>();

    public DrawableList(float width, float height) {
        this.width = width;
        this.height = height;

        positions.add(0.2f - DIFF);
        positions.add(0.2f);
        positions.add(0.2f + DIFF);
        positions.add(0.2f + 2 * DIFF);
        positions.add(0.2f + 3 * DIFF);

        addItem(0);
        addItem(1);
        addItem(2);
        addItem(3);
        addItem(4);
    }

    private void addItem(int num) {
        final int size = 350;
        Text item = new Text("item" + num, size);
        item.move(0.2f, positions.get(num));
        items.add(item);
    }

    @Override
    public void updateChildren(final long time) {
        items.forEach(item -> {
            item.update(time);
        });
    }

    @Override
    public void updateObject() {
        Iterator<Float> pos = positions.iterator();
        items.stream().forEachOrdered(item -> {
            item.moveTowards(0.2f, pos.next());
        });
    }

    @Override
    public void draw(GL2 gl) {
        gl.glTranslatef(X(), Y(), 0);

        gl.glColor3f(0, 0, 1);
        drawRectangle(gl, 0, 0, width, height);

        List<Text> visibleItems = items.subList(1, 4);
        Iterator<Float> pos = positions.iterator();
        items.stream().forEachOrdered(item -> {
            float position = pos.next();
            if (visibleItems.contains(item) || !item.at(0.2f, position)) {
                gl.glPushMatrix();
                item.draw(gl);
                gl.glPopMatrix();
            }
        });

        gl.glColor3f(1, 0, 0);
        drawFrame(gl);
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

    private void drawFrame(GL2 gl) {
        drawRectangle(gl, 0, 0, width, 0.1f);
        drawRectangle(gl, width - 0.1f, 0, width, height);
        drawRectangle(gl, 0, height - 0.1f, width, height);
        drawRectangle(gl, 0, 0, 0.1f, height);
    }

    @Override
    public String getName() {
        return "list";
    }

    public void forward() {
        Text first = items.remove(0);
        items.add(first);
        first.moveTo(0.2f, positions.get(4));
    }

}
