package hu.esgott.caronboard;

import hu.esgott.caronboard.gl.Canvas;
import hu.esgott.caronboard.leap.LeapListener;
import hu.esgott.caronboard.leap.SpeechPlayer;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.leapmotion.leap.Controller;

// TODO command queue, amibe a GUI utasitasokat lehet tenni (kesobb kulon queue
//      lejatszashoz es egyeb reszegysegekhez), queue-ba mindeki regisztralja
//      sajat actionjeit (Screenek egyelore)
// TODO Leap osszekotese
// TODO wav-ok takaritasa, audio feedback keszites
// TODO hangefelismeres integralasa
// TODO nyelvtan keszitese
// TODO hangfelismeres osszekotese
// TODO medialejatszo backend
// TODO hangero szabolyozas (V gesztus)
// TODO texturazas

final class Main {

    private static LeapListener listener = new LeapListener();
    private static Controller controller;
    private static SpeechPlayer player;
    private static boolean withGui = true;
    private static Logger logger;

    private Main() {
        throw new AssertionError("Shall not initialize this");
    }

    public static void main(final String[] arguments) {
        initLogger();
        processArguments(arguments);
        startLeap();

        if (withGui) {
            logger.info("Starting GUI");
            runGUI();
            logger.info("GUI exited");
        } else {
            logger.info("Press Enter to exit");
            new Scanner(System.in).nextLine();
        }
        stopLeap();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("static-access")
    private static void processArguments(final String[] args) {
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("with-gui").create());
        CommandLineParser parser = new PosixParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("with-gui")) {
                withGui = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void startLeap() {
        logger.info("Starting Leap");
        controller = new Controller();
        controller.addListener(listener);
        player = new SpeechPlayer();
        logger.info("Leap started");
    }

    private static void runGUI() {
        Canvas canvas = new Canvas();
        MainWindow mainWindow = new MainWindow();
        mainWindow.addCanvas(canvas);
        mainWindow.display();
    }

    private static void stopLeap() {
        logger.info("Shutting down Leap");
        controller.removeListener(listener);
        listener.dispose();
        player.dispose();
        logger.info("Leap stopped");
    }

}
