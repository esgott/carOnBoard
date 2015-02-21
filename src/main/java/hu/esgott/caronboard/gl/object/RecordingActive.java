package hu.esgott.caronboard.gl.object;

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
        this.visible = visible;
    }

}
