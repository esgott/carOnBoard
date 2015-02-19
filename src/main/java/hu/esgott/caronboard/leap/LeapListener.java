package hu.esgott.caronboard.leap;

import java.util.logging.Logger;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Listener;

public class LeapListener extends Listener {

    private final Logger log = Logger.getLogger(getClass().getName());

    private FrameWrapper frame = new FrameWrapper();

    @Override
    public void onConnect(Controller controller) {
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.config().setFloat("Gesture.Swipe.MinLength", 25.0f);
        controller.config().setFloat("Gesture.Circle.MinArc", 5.0f);
        controller.config().save();
    }

    @Override
    public void onFrame(Controller controller) {
        frame.update(controller.frame());

        boolean unexecutedGesturesExists = frame.gestures().anyMatch(
                gesture -> !gesture.executed());

        if (unexecutedGesturesExists) {
            log.info("--- Frame " + frame.f.id() + " ---");
        }

        frame.gestures().filter(gesture -> !gesture.executed())
                .forEach(gesture -> {
                    gesture.execute();
                    gesture.setExecuted();
                });
    }

    public void dispose() {
        frame.dispose();
    }

}
