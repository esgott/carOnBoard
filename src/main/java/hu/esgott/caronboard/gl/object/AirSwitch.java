package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.gl.Textures;
import hu.esgott.caronboard.gl.Textures.ID;

import javax.media.opengl.GL2;

public class AirSwitch extends DrawableObject {

    private static final float PICTOGRAM_SIZE = 0.2f;
    private static final float PICTOGRAM_RELATIVE_RADIUS = 0.09f;

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
        drawCircular(gl, ID.DEFROST, PICTOGRAM_RELATIVE_RADIUS, 210.0f);
        drawCircular(gl, ID.DOWNDEFROST, PICTOGRAM_RELATIVE_RADIUS, 150.0f);
        drawCircular(gl, ID.DOWN, PICTOGRAM_RELATIVE_RADIUS, 90.0f);
        drawCircular(gl, ID.UPDOWN, PICTOGRAM_RELATIVE_RADIUS, 30.0f);
        drawCircular(gl, ID.UP, PICTOGRAM_RELATIVE_RADIUS, -30.0f);
    }

    private void drawCircular(GL2 gl, ID textureId, float relativeRadius,
            float angle) {
        double radian = Math.toRadians(angle);
        float r = radius + relativeRadius;
        double x = Math.cos(radian) * r;
        double y = Math.sin(radian) * r;

        gl.glPushMatrix();
        gl.glTranslatef((float) x, (float) y, 0.0f);
        drawTexture(gl, textureId);
        gl.glPopMatrix();
    }

    private void drawTexture(GL2 gl, ID textureId) {
        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        final float half = PICTOGRAM_SIZE / 2;

        textures.enableTexture(textureId, gl);
        gl.glBegin(GL2.GL_QUADS);

        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex2f(-half, -half);

        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex2f(half, -half);

        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex2f(half, half);

        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex2f(-half, half);

        gl.glEnd();
        textures.disableTexture(textureId, gl);
    }

    @Override
    public String getName() {
        return "AirSwitch";
    }

}
