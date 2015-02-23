package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.gl.Textures;
import hu.esgott.caronboard.gl.Textures.ID;

import javax.media.opengl.GL2;

public class Background extends DrawableObject {

    private Textures textures;

    public Background(Textures textures) {
        this.textures = textures;
    }

    @Override
    public void draw(GL2 gl) {
        textures.enableTexture(ID.BACKGROUND, gl);
        Shapes.drawRectangle(gl, -10, -10, 10, 10);
        textures.disableTexture(ID.BACKGROUND, gl);
    }

    @Override
    public String getName() {
        return "background";
    }

}
