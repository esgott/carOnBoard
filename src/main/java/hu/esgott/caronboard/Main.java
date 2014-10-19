package hu.esgott.caronboard;

import hu.esgott.caronboard.gl.Renderer;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

final class Main {

    private static final int WINDOW_SIZE = 300;

    private Main() {
        throw new AssertionError("Shall not initialize this");
    }

    public static void main(final String[] arguments) {
        GLProfile.initSingleton();
        GLProfile profile = GLProfile.getDefault();
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(new Renderer());

        Frame frame = new Frame("GL");
        frame.setSize(WINDOW_SIZE, WINDOW_SIZE);
        frame.add(canvas);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                System.exit(0);
            }
        });
    }

}
