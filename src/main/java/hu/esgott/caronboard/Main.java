package hu.esgott.caronboard;

import hu.esgott.caronboard.gl.Canvas;
import hu.esgott.caronboard.leap.LeapListener;

import com.leapmotion.leap.Controller;

final class Main {

    private Main() {
        throw new AssertionError("Shall not initialize this");
    }

    public static void main(final String[] arguments) {
        Controller controller = new Controller();
        controller.addListener(new LeapListener());

        Canvas canvas = new Canvas();
        MainWindow mainWindow = new MainWindow();
        mainWindow.addGlCanvas(canvas.getCanvas());
        mainWindow.display();
    }

}
