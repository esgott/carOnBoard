package hu.esgott.caronboard.gl.object;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Background extends DrawableObject {

    private final Logger log = Logger.getLogger(getClass().getName());

    private Texture bgTexture = null;

    @Override
    public void draw(GL2 gl) {
        if (bgTexture == null) {
            lazyLoadTexture(gl);
        }

        bgTexture.enable(gl);
        bgTexture.bind(gl);
        Shapes.drawRectangle(gl, -10, -10, 10, 10);
        bgTexture.disable(gl);
    }

    private void lazyLoadTexture(GL2 gl) {
        try {
            InputStream stream = new FileInputStream("FabricPlain0073_S.jpg");
            bgTexture = TextureIO.newTexture(stream, false, TextureIO.JPG);
            bgTexture
                    .setTexParameterf(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            bgTexture
                    .setTexParameterf(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        } catch (GLException | IOException e) {
            log.severe("Error loading texture: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "background";
    }

}
