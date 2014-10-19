package hu.esgott.caronboard.gl;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.Animator;

public class Canvas {

    static {
        GLProfile.initSingleton();
    }

    private GLCanvas canvas;

    public Canvas() {
        GLProfile profile = GLProfile.getDefault();
        GLCapabilities capabilities = new GLCapabilities(profile);
        canvas = new GLCanvas(capabilities);
        Renderer renderer = new Renderer();
        canvas.addGLEventListener(renderer);
        Animator animator = new Animator(canvas);
        animator.start();
    }

    public GLCanvas getCanvas() {
        return canvas;
    }

}
