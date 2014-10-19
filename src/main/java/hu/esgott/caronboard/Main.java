package hu.esgott.caronboard;

import hu.esgott.caronboard.gl.Canvas;

final class Main {

    private Main() {
        throw new AssertionError("Shall not initialize this");
    }

    public static void main(final String[] arguments) {
        Canvas canvas = new Canvas();
        MainWindow mainWindow = new MainWindow();
        mainWindow.addGlCanvas(canvas.getCanvas());
    }

}
