package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.leap.AudioFeedback;

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
    private final AudioFeedback audioFeedback = AudioFeedback.getInstance();

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
        gl.glPushMatrix();

        gl.glTranslatef(X(), Y(), 0);

        if (selected()) {
            gl.glColor3f(1, 1, 0);
            Shapes.drawRectangle(gl, -0.01f, -0.01f, width + 0.01f,
                    height + 0.01f);
        }

        gl.glColor3f(0, 0, 1);
        Shapes.drawRectangle(gl, 0, 0, width, height);

        itemHandler.getRenderedItems().stream().forEachOrdered(item -> {
            gl.glPushMatrix();
            enableClip(item, gl);
            item.draw(gl);
            gl.glDisable(GL2.GL_CLIP_PLANE0);
            gl.glPopMatrix();
        });

        gl.glColor3f(1, 0, 0);
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
        audioFeedback.play(AudioFeedback.A.BTN_BEEP);
        changed = !itemHandler.lastItem();
        Text newItem = itemHandler.next();
        float lastPosition = positions.get(positions.size() - 1);
        newItem.moveTo(0.2f, lastPosition);
        log.info("In list " + getName() + " selected "
                + itemHandler.getCurrentString());
    }

    @Override
    public void backwardAction() {
        audioFeedback.play(AudioFeedback.A.BTN_BEEP);
        changed = !itemHandler.firstItem();
        Text newItem = itemHandler.prev();
        newItem.moveTo(0.2f, positions.get(0));
        log.info("In list " + getName() + " selected "
                + itemHandler.getCurrentString());
    }

    public void setElements(List<String> elements) {
        itemHandler.addItems(elements);

        Iterator<Float> pos = positions.iterator();
        itemHandler.getRenderedItems().stream().forEachOrdered(item -> {
            item.move(0.2f, pos.next());
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

}
