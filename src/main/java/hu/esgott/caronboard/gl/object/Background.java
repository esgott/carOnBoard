package hu.esgott.caronboard.gl.object;

import javax.media.opengl.GL2;

public class Background extends DrawableObject {

    @Override
    public void draw(GL2 gl) {
        gl.glColor3f(0.1f, 0.1f, 0.1f);
        Shapes.drawRectangle(gl, -100, -100, 100, 100);
    }

    @Override
    public String getName() {
        return "background";
    }

}
