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
        controller.config().setFloat("Gesture.Circle.MinArc", 10.0f);
        controller.config().save();
    }

    @Override
    public void onFrame(Controller controller) {
        frame.update(controller.frame());

        boolean unprintedGesturesExists = frame.gestures().anyMatch(
                gesture -> gesture.toPrint());

        if (unprintedGesturesExists) {
            log.info("--- Frame " + frame.f.id() + " ---");
        }

        frame.gestures().filter(gesture -> gesture.toPrint())
                .forEach(gesture -> {
                    log.info(gesture.toString());
                    gesture.setPrinted();
                });
    }

    public void dispose() {
        frame.dispose();
    }

}
