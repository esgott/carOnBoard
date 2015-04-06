package hu.esgott.caronboard.gl.object;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.media.opengl.GL2;

public class DrawableList extends DrawableObject {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final float FRAME_THICKNESS = 0.06f;

    private final float width;
    private final float height;
    private final String name;
    private final ItemHandler itemHandler;
    private List<Float> positions = new ArrayList<>();
    private boolean changed = true;
    private boolean circular = false;
    private boolean displaySelection = false;

    public DrawableList(final String name, final float width,
            final float height, final int fontSize,
            final float diffBetweenLines, final float lineOffset) {
        this.width = width;
        this.height = height;
        this.name = name;
        itemHandler = new ItemHandler(fontSize);

        for (int i = -1; i < 4; i++) {
            positions.add(lineOffset - (i * diffBetweenLines));
        }
    }

    public DrawableList(final String name, final float width,
            final float height, final int fontSize,
            final float diffBetweenLines, final float lineOffset,
            final boolean circular) {
        this(name, width, height, fontSize, diffBetweenLines, lineOffset);
        this.circular = circular;
    }

    @Override
    public void updateChildren(final long time) {
        itemHandler.getRenderedItems().forEach(item -> {
            item.update(time);
        });
    }

    @Override
    public void updateObject() {
        changed = false;
        Iterator<Float> pos = positions.iterator();
        itemHandler.getRenderedItems().stream().forEachOrdered(item -> {
            item.moveTowards(0.2f, pos.next());
        });
    }

    @Override
    public void draw(GL2 gl) {
        gl.glPushMatrix();

        gl.glTranslatef(X(), Y(), 0);

        if (selected()) {
            gl.glColor3f(1, 1, 0);
            Shapes.drawFrame(gl, 0, 0, width, height, 0.01f);
        }

        gl.glColor3f(0.0f, 0.05f, 0.4f);
        Shapes.drawRectangle(gl, 0, 0, width, height);
        if (displaySelection) {
            gl.glColor3f(0.1f, 0.2f, 0.5f);
            Shapes.drawRectangle(gl, 0.1f, positions.get(3) + 0.18f,
                    width - 0.1f, positions.get(3) + 0.425f);
        }

        itemHandler.getRenderedItems().stream().forEachOrdered(item -> {
            gl.glPushMatrix();
            enableClip(item, gl);
            item.draw(gl);
            gl.glDisable(GL2.GL_CLIP_PLANE0);
            gl.glPopMatrix();
        });

        gl.glColor3f(0.0f, 0.0f, 0.2f);
        drawFrame(gl);

        gl.glPopMatrix();
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
        Shapes.drawRectangle(gl, 0, 0, width, FRAME_THICKNESS);
        Shapes.drawRectangle(gl, width - FRAME_THICKNESS, 0, width, height);
        Shapes.drawRectangle(gl, 0, height - FRAME_THICKNESS, width, height);
        Shapes.drawRectangle(gl, 0, 0, FRAME_THICKNESS, height);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void forwardAction() {
        boolean lastItem = itemHandler.lastItem();
        if (lastItem && circular) {
            itemHandler.goToFirst();
            resetPositions();
            changed = true;
        } else if (!lastItem) {
            Text newItem = itemHandler.next();
            float lastPosition = positions.get(positions.size() - 1);
            newItem.moveTo(0.2f, lastPosition);
            changed = true;
        } else {
            changed = false;
        }
        feedback();
    }

    @Override
    public void backwardAction() {
        boolean firstItem = itemHandler.firstItem();
        if (firstItem && circular) {
            itemHandler.goToLast();
            resetPositions();
            changed = true;
        } else if (!firstItem) {
            Text newItem = itemHandler.prev();
            newItem.moveTo(0.2f, positions.get(0));
            changed = true;
        } else {
            changed = false;
        }
        feedback();
    }

    public void select(int index) {
        changed = itemHandler.select(index);
        if (changed) {
            resetPositions();
            feedback();
        }
    }

    private void feedback() {
        log.info("In list " + getName() + " selected "
                + itemHandler.getCurrentString());
    }

    public void setElements(List<String> elements) {
        itemHandler.clearItems();
        itemHandler.addItems(elements);

        resetPositions();
    }

    private void resetPositions() {
        Iterator<Float> pos = positions.iterator();
        itemHandler.getRenderedItems().stream().forEachOrdered(item -> {
            item.moveTo(0.2f, pos.next());
        });
    }

    public int getSelectedNum() {
        return itemHandler.getCurrentNum();
    }

    public String getSelectedName() {
        return itemHandler.getCurrentString();
    }

    public boolean changed() {
        return changed;
    }

    public void displaySelection(boolean on) {
        displaySelection = on;
    }

}
