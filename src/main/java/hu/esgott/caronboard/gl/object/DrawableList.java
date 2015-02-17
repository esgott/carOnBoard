package hu.esgott.caronboard.gl.object;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL2;

public class DrawableList extends DrawableObject {

    private static final float DIFF = 0.575f;
    private static final float FRAME_THICKNESS = 0.07f;

    private float width = 0.0f;
    private float height = 0.0f;
    private final ItemHandler itemHandler = new ItemHandler(350);
    private List<Float> positions = new ArrayList<>();

    public DrawableList(float width, float height) {
        this.width = width;
        this.height = height;

        for (int i = -1; i < 4; i++) {
            positions.add(0.2f + (i * DIFF));
        }

        for (int i = 0; i < 5; i++) {
            itemHandler.addItem("item" + (i + 1));
        }

        Iterator<Float> pos = positions.iterator();
        itemHandler.getRenderedItems().stream().forEachOrdered(item -> {
            item.move(0.2f, pos.next());
        });
    }

    @Override
    public void updateChildren(final long time) {
        itemHandler.getRenderedItems().forEach(item -> {
            item.update(time);
        });
    }

    @Override
    public void updateObject() {
        Iterator<Float> pos = positions.iterator();
        itemHandler.getRenderedItems().stream().forEachOrdered(item -> {
            item.moveTowards(0.2f, pos.next());
        });
    }

    @Override
    public void draw(GL2 gl) {
        gl.glTranslatef(X(), Y(), 0);

        gl.glColor3f(0, 0, 1);
        drawRectangle(gl, 0, 0, width, height);

        itemHandler.getRenderedItems().stream().forEachOrdered(item -> {
            gl.glPushMatrix();
            enableClip(item, gl);
            item.draw(gl);
            gl.glDisable(GL2.GL_CLIP_PLANE0);
            gl.glPopMatrix();
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

    private void enableClip(final Text item, final GL2 gl) {
        double[] yPlane = { 0, 1, 0, 0 };
        boolean needed = false;
        if (item.Y() < 0) {
            yPlane[3] = item.Y();
            needed = true;
        } else if (item.Y() + item.height() > height) {
            yPlane[3] = (height - item.Y());
            yPlane[1] = -1;
            needed = true;
        }
        if (needed) {
            gl.glClipPlane(GL2.GL_CLIP_PLANE0, yPlane, 0);
            gl.glEnable(GL2.GL_CLIP_PLANE0);
        }
    }

    private void drawFrame(final GL2 gl) {
        drawRectangle(gl, 0, 0, width, FRAME_THICKNESS);
        drawRectangle(gl, width - FRAME_THICKNESS, 0, width, height);
        drawRectangle(gl, 0, height - FRAME_THICKNESS, width, height);
        drawRectangle(gl, 0, 0, FRAME_THICKNESS, height);
    }

    @Override
    public String getName() {
        return "list";
    }

    public void forward() {
        Text newItem = itemHandler.next();
        float lastPosition = positions.get(positions.size() - 1);
        newItem.moveTo(0.2f, lastPosition);
    }

    public void backward() {
        Text newItem = itemHandler.prev();
        newItem.moveTo(0.2f, positions.get(0));
    }

}
