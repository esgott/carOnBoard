package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.gl.Textures;
import hu.esgott.caronboard.gl.Textures.ID;

import javax.media.opengl.GL2;

public class AirSwitch extends DrawableObject {

    private static final float PICTOGRAM_SIZE = 0.2f;
    private static final float PICTOGRAM_RELATIVE_RADIUS = 0.09f;
    private static final float INDEX_WIDTH_DEG = 7.5f;
    private static final float RELATIVE_INDEX_LENGTH = 0.2f;
    private static final float INDEX_SPEED = 2.0f;

    private final float radius;
    private int indexAngle = 90;
    private float currentIndexAngle = indexAngle;
    private final Textures textures;

    public AirSwitch(final float radius, final Textures textures) {
        this.radius = radius;
        this.textures = textures;
    }

    @Override
    public void updateObject() {
        float normalizedIndexAngle = normalizeAngle(indexAngle);
        float indexDiff = angleDiff(currentIndexAngle, normalizedIndexAngle);
        float indexRotation = INDEX_SPEED * elapsedTime();
        if (indexRotation < Math.abs(indexDiff)) {
            if (indexDiff < 0) {
                currentIndexAngle += indexRotation;
            } else {
                currentIndexAngle -= indexRotation;
            }
            currentIndexAngle = normalizeAngle(currentIndexAngle);
        } else {
            currentIndexAngle = normalizedIndexAngle;
        }
    }

    private float angleDiff(float a, float b) {
        float diff = Math.abs(a - b);
        if (diff > 180) {
            diff -= 360;
        }
        return Math.abs(diff);
    }

    private float normalizeAngle(float angle) {
        return (angle %= 360) >= 0 ? angle : (angle + 360);
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
        drawIndex(gl);
    }

    private void drawPictograms(GL2 gl) {
        drawCircular(gl, ID.DEFROST, 210.0f);
        drawCircular(gl, ID.DOWNDEFROST, 150.0f);
        drawCircular(gl, ID.DOWN, 90.0f);
        drawCircular(gl, ID.UPDOWN, 30.0f);
        drawCircular(gl, ID.UP, -30.0f);
    }

    private void drawCircular(GL2 gl, ID textureId, float angle) {
        double radian = Math.toRadians(angle);
        float r = radius + PICTOGRAM_RELATIVE_RADIUS;

        gl.glPushMatrix();
        gl.glTranslatef(xForCircle(radian, r), yForCircle(radian, r), 0.0f);
        drawTexture(gl, textureId);
        gl.glPopMatrix();
    }

    private float xForCircle(double angle, float r) {
        return (float) (Math.cos(angle) * r);
    }

    private float yForCircle(double angle, float r) {
        return (float) (Math.sin(angle) * r);
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

    private void drawIndex(GL2 gl) {
        final double angleRadian = Math.toRadians(currentIndexAngle);
        final double widthRadian = Math.toRadians(INDEX_WIDTH_DEG);
        final double angleMax = angleRadian + widthRadian;
        final double angleMin = angleRadian - widthRadian;
        final float rMax = radius - TemperatureDisplay.SELECTION_THICKNESS;
        final float rMin = radius * RELATIVE_INDEX_LENGTH;

        gl.glColor3f(0.0f, 0.0f, 0.2f);
        gl.glBegin(GL2.GL_QUADS);

        gl.glVertex2f(xForCircle(angleMin, rMax), yForCircle(angleMin, rMax));
        gl.glVertex2f(xForCircle(angleMax, rMax), yForCircle(angleMax, rMax));
        gl.glVertex2f(xForCircle(angleMax, rMin), yForCircle(angleMax, rMin));
        gl.glVertex2f(xForCircle(angleMin, rMin), yForCircle(angleMin, rMin));

        gl.glEnd();
    }

    @Override
    public String getName() {
        return "AirSwitch";
    }

    @Override
    public void forwardAction() {
        if (indexAngle > -30) {
            indexAngle -= 60;
        } else {
            indexAngle = 210;
        }
    }

    @Override
    public void backwardAction() {
        if (indexAngle < 210) {
            indexAngle += 60;
        } else {
            indexAngle = -30;
        }
    }

}
