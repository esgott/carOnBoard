package hu.esgott.caronboard;

import hu.esgott.caronboard.gl.Canvas;
import hu.esgott.caronboard.leap.AudioFeedback;
import hu.esgott.caronboard.leap.LeapListener;
import hu.esgott.caronboard.speech.RecognizerServerConnection;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.leapmotion.leap.Controller;

// TODO hangefelismeres integralasa
// TODO nyelvtan keszitese
// TODO hangfelismeres osszekotese
// TODO medialejatszo backend
// TODO hangero szabolyozas (V gesztus)
// TODO tekeres
// TODO texturazas
// TODO EventHandler lecsatolasa Rendererbol

final class Main {

    private static LeapListener listener;
    private static Controller controller;
    private static AudioFeedback audioFeedback;
    private static RecognizerServerConnection recognizer;
    private static Logger logger;

    private Main() {
        throw new AssertionError("Shall not initialize this");
    }

    public static void main(final String[] arguments) {
        initLogger();
        startServices();
        runGUI();
        stopServices();
        logger.info("Bye");
    }

    private static void initLogger() {
        logger = Logger.getLogger("");
        try {
            FileHandler file = new FileHandler("log.txt");
            logger.addHandler(file);
            SimpleFormatter formatter = new SimpleFormatter();
            file.setFormatter(formatter);
            logger.info("Application started");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void startServices() {
        logger.info("Starting Services");
        controller = new Controller();
        listener = new LeapListener();
        controller.addListener(listener);
        audioFeedback = new AudioFeedback();
        logger.info("Leap started");
        recognizer = new RecognizerServerConnection();
        logger.info("Speech recognition started");
    }

    private static void runGUI() {
        logger.info("Starting GUI");
        Canvas canvas = new Canvas();
        MainWindow mainWindow = new MainWindow();
        mainWindow.addCanvas(canvas);
        mainWindow.display();
        logger.info("GUI exited");
    }

    private static void stopServices() {
        logger.info("Shutting down services");
        controller.removeListener(listener);
        listener.dispose();
        audioFeedback.dispose();
        logger.info("Leap stopped");
        recognizer.dispose();
        logger.info("Speech recognition stopped");
    }

}
