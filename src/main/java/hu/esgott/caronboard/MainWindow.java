package hu.esgott.caronboard;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.awt.GLCanvas;

public class MainWindow {

    private static final int WINDOW_SIZE = 300;

    private Frame frame = new Frame("CarOnBoard");

    public MainWindow() {
        frame.setSize(WINDOW_SIZE, WINDOW_SIZE);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                System.exit(0);
            }
        });
    }

    public void addGlCanvas(GLCanvas canvas) {
        frame.add(canvas);
    }

}
