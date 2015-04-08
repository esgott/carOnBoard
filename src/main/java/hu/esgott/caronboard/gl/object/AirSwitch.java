package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.gl.Textures;
import hu.esgott.caronboard.gl.Textures.ID;

import javax.media.opengl.GL2;

public class AirSwitch extends DrawableObject {

    private final float radius;
    private final Textures textures;

    public AirSwitch(final float radius, final Textures textures) {
        this.radius = radius;
        this.textures = textures;
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
        drawPictograms(gl);
    }

    private void drawPictograms(GL2 gl) {
        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        textures.enableTexture(ID.DEFROST, gl);
        Shapes.drawRectangle(gl, 0.0f, 0.0f, 2.0f, 2.0f);
        textures.disableTexture(ID.DEFROST, gl);
    }

    @Override
    public String getName() {
        return "AirSwitch";
    }

}
