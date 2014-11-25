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

final class Main {

    private static LeapListener listener = new LeapListener();
    private static boolean withGui = false;

    private Main() {
        throw new AssertionError("Shall not initialize this");
    }

    public static void main(final String[] arguments) {
        initLogger();
        processArguments(arguments);
        Controller controller = new Controller();
        controller.addListener(listener);
        SpeechPlayer player = new SpeechPlayer();

        if (withGui) {
            Canvas canvas = new Canvas();
            MainWindow mainWindow = new MainWindow();
            mainWindow.addGlCanvas(canvas.getCanvas());
            mainWindow.display();
        } else {
            new Scanner(System.in).nextLine();
        }
        controller.removeListener(listener);
        listener.dispose();
        player.dispose();
    }

    private static void initLogger() {
        Logger logger = Logger.getLogger("");
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

}
