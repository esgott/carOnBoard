package hu.esgott.caronboard.gl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Textures {

    public enum ID {
        PLASTIC, METAL, WOOD
    }

    private final Logger log = Logger.getLogger(getClass().getName());

    private Map<ID, Texture> textures = new HashMap<>();

    public void loadTextures(GL2 gl) {
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE,
                GL2.GL_REPLACE);
        load("FabricPlain0073_S.jpg", TextureIO.JPG, ID.PLASTIC);
        setRepeat(ID.PLASTIC, gl);
        load("MetalBare0191_17_S.jpg", TextureIO.JPG, ID.METAL);
        setRepeat(ID.METAL, gl);
        load("WoodFine0019_S.jpg", TextureIO.JPG, ID.WOOD);
        setRepeat(ID.WOOD, gl);
    }

    private void load(final String file, final String suffix, final ID id) {
        try {
            InputStream stream = new FileInputStream(file);
            Texture texture = TextureIO.newTexture(stream, false, suffix);
            textures.put(id, texture);
            log.info("Texture loaded " + file + " to " + id);
        } catch (GLException | IOException e) {
            log.severe("Error loading texture " + file + ": " + e.getMessage());
        }
    }

    private void setRepeat(ID id, GL2 gl) {
        Texture texture = textures.get(id);
        if (texture != null) {
            texture.setTexParameterf(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            texture.setTexParameterf(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
            log.info("Texture " + id + " set as repeat");
        } else {
            log.warning("No texture found for id");
        }
    }

    public void enableTexture(ID id, GL2 gl) {
        Texture texture = textures.get(id);
        if (texture != null) {
            texture.enable(gl);
            texture.bind(gl);
        }
    }

    public void disableTexture(ID id, GL2 gl) {
        Texture texture = textures.get(id);
        if (texture != null) {
            texture.disable(gl);
        }
    }

}
