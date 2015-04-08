package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.gl.Textures;
import hu.esgott.caronboard.gl.Textures.ID;

import javax.media.opengl.GL2;

public class ImageObject extends DrawableObject {

    private static final float FRAME_THICKNESS = 0.1f;
    private static final float SELECTION_THICKNESS = 0.02f;

    private final Textures textures;
    private final ID textureId;
    private final float width;
    private final float height;

    ImageObject(final Textures textures, final ID textureId, final float width,
            final float height) {
        this.textures = textures;
        this.textureId = textureId;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(X(), Y(), 0);

        drawContent(gl);

        gl.glPopMatrix();
    }

    private void drawContent(GL2 gl) {
        drawSelection(gl);
        drawFrame(gl);
        drawImage(gl);
    }

    private void drawSelection(GL2 gl) {
        if (selected()) {
            gl.glColor3f(1, 1, 0);
            Shapes.drawRectangle(gl, width + SELECTION_THICKNESS, height
                    + SELECTION_THICKNESS);
        }
    }

    private void drawFrame(GL2 gl) {
        gl.glColor3f(0.0f, 0.0f, 0.2f);
        Shapes.drawRectangle(gl, width, height);
    }

    private void drawImage(GL2 gl) {
        textures.enableTexture(textureId, gl);
        Shapes.drawRectangle(gl, width - FRAME_THICKNESS, height
                - FRAME_THICKNESS);
        textures.disableTexture(textureId, gl);
    }

    @Override
    public String getName() {
        return "Image";
    }

}
