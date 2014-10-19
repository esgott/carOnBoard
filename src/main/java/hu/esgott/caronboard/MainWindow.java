package hu.esgott.caronboard;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.awt.GLCanvas;

public class MainWindow {

    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;

    private Frame frame = new Frame("CarOnBoard");

    public MainWindow() {
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                System.exit(0);
            }
        });
    }

    public void addGlCanvas(final GLCanvas canvas) {
        frame.add(canvas);
    }

    public void display() {
        frame.setVisible(true);
    }

}
