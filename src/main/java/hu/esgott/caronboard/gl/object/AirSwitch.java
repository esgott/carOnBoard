package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public class AirSwitch extends DrawableObject {

    private final float radius;

    public AirSwitch(final float radius) {
        this.radius = radius;
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
        Shapes.drawCircle(gl, radius - TemperatureDisplay.SELECTION_THICKNESS);
        gl.glColor3f(0.0f, 0.05f, 0.4f);
        Shapes.drawCircle(gl, radius - TemperatureDisplay.SELECTION_THICKNESS
                - TemperatureDisplay.FRAME_THICKNESS);
    }

    @Override
    public String getName() {
        return "AirSwitch";
    }

}
