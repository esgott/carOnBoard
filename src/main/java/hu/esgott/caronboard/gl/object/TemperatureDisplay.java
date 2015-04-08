package hu.esgott.caronboard.gl.object;

import java.util.logging.Logger;

import javax.media.opengl.GL2;

public class TemperatureDisplay extends DrawableObject {

    private final Logger log = Logger.getLogger(getClass().getName());

    public static final float SELECTION_THICKNESS = 0.01f;
    public static final float FRAME_THICKNESS = 0.05f;

    private final float radius;
    private int temp = 22;
    private final Text text = new Text("22", 300);

    public TemperatureDisplay(final float radius) {
        this.radius = radius;
        text.move(-0.2f, -0.1f);
    }

    @Override
    public void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(X(), Y(), 0);

        drawContent(gl);

        gl.glPopMatrix();
    }

    private void drawContent(GL2 gl) {
        if (selected()) {
            gl.glColor3f(1, 1, 0);
            Shapes.drawCircle(gl, radius);
        }
        gl.glColor3f(0.0f, 0.0f, 0.2f);
        Shapes.drawCircle(gl, radius - SELECTION_THICKNESS);
        gl.glColor3f(0.0f, 0.05f, 0.4f);
        Shapes.drawCircle(gl, radius - SELECTION_THICKNESS - FRAME_THICKNESS);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        text.draw(gl);
    }

    @Override
    public String getName() {
        return "TemperatureDisplay";
    }

    @Override
    public void forwardAction() {
        temp++;
        setTemp();
    }

    private void setTemp() {
        text.setText(Integer.toString(temp));
        log.info("Temperature set to " + temp);
    }

    @Override
    public void backwardAction() {
        temp--;
        setTemp();
    }

}
