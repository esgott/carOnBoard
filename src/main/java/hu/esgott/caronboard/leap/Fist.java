package hu.esgott.caronboard.leap;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.RecorderCommand;

import java.util.logging.Logger;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;

public class Fist {

    private final Logger log = Logger.getLogger(getClass().getName());

    private GestureTimer timer;
    private final CommandQueue queue = CommandQueue.getInstance();

    public Fist() {
        Runnable task = () -> {
            queue.notifyRecorder(RecorderCommand.START_RECORDING);
        };
        Runnable onStop = () -> {
            queue.notifyRecorder(RecorderCommand.STOP_RECORDING);
        };
        timer = new GestureTimer(0.5f, 30, task, null, onStop);
    }

    public void update(HandList hands) {
        if (hands.count() == 1) {
            Hand hand = hands.get(0);
            if ((hand.fingers().extended().count() == 0)
                    && (hand.palmVelocity().magnitude() < FrameWrapper.MAX_STEADY_PALM_VELOCITY)) {
                timer.start();
                return;
            }
            log.fine("fingers: " + hand.fingers().extended().count()
                    + " velocity: " + hand.palmVelocity().magnitude());
        }
        timer.stop();
    }

    public void dispose() {
        timer.dispose();
    }

}
