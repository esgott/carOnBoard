package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.devices.AudioFeedback;
import hu.esgott.caronboard.devices.AudioFeedback.A;

import javax.media.opengl.GL2;

public class RecordingActive extends DrawableObject {

    private boolean visible = false;

    @Override
    public void draw(GL2 gl) {
        if (visible) {
            gl.glPushMatrix();
            gl.glTranslatef(X(), Y(), 0.0f);

            gl.glColor3f(0.0f, 0.0f, 0.0f);
            Shapes.drawCircle(gl, 0.8f);
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            Shapes.drawCircle(gl, 0.7f);

            gl.glPopMatrix();
        }
    }

    @Override
    public String getName() {
        return "Recording";
    }

    public void setVisible(boolean visible) {
        if (!this.visible && visible) {
            AudioFeedback.getInstance().play(A.CORRECT);
        }
        this.visible = visible;
    }

}
