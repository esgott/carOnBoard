package hu.esgott.caronboard.gl.object;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.awt.TextRenderer;

public class Text extends DrawableObject {

    private static final float SCALE = 0.001f;

    private String text;
    private TextRenderer textRenderer;

    public Text(String text, int size) {
        this.text = text;
        textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, size));
    }

    @Override
    public void draw(final GL2 gl) {
        gl.glTranslatef(X(), Y(), 0);
        gl.glColor3f(0.5f, 0.5f, 0.5f);

        textRenderer.begin3DRendering();
        textRenderer.draw3D(text, 0, 0, 0, SCALE);
        textRenderer.end3DRendering();
    }

    @Override
    public String getName() {
        return text;
    }

    public float height() {
        Rectangle2D rect = textRenderer.getBounds(text);
        return (float) rect.getHeight() * SCALE;
    }

}
