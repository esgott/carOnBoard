package hu.esgott.caronboard.gl.object;

import java.awt.Font;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.awt.TextRenderer;

public class Text extends DrawableObject {

    private String text;
    private TextRenderer textRenderer;

    public Text(String text) {
        this.text = text;
        textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
    }

    @Override
    public void update(long time) {
    }

    @Override
    public void draw(final GL2 gl) {
        gl.glTranslatef(X(), Y(), 0);
        gl.glColor3f(0.5f, 0.5f, 0.5f);

        textRenderer.begin3DRendering();
        textRenderer.draw3D(text, X(), Y(), 0, 0.005f);
        textRenderer.end3DRendering();
    }

    @Override
    public String getName() {
        return text;
    }

}
