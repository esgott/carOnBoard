package hu.esgott.caronboard;

import hu.esgott.caronboard.gl.Canvas;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

public class MainWindow {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;

    private static Object lock = new Object();

    private static Frame frame = new Frame("CarOnBoard");
    private static Canvas canvas;

    public MainWindow() {
        System.setProperty("sun.awt.noerasebackground", "true");

        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                log.info("Shutting down GUI after exit button");
                exit();
            }
        });
    }

    public static void exit() {
        synchronized (lock) {
            canvas.stop();
            frame.setVisible(false);
            frame.dispose();
            lock.notify();
        }
    }

    public void addCanvas(final Canvas canvas) {
        MainWindow.canvas = canvas;
        frame.add(canvas.getCanvas());
    }

    public void display() {
        frame.setVisible(true);
        frame.requestFocus();
        if (canvas != null) {
            canvas.getCanvas().requestFocusInWindow();
        }
        log.info("GUI started");
        synchronized (lock) {
            while (frame.isVisible())
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

}
