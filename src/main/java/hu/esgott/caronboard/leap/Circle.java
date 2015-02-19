package hu.esgott.caronboard.leap;

import hu.esgott.caronboard.CommandQueue;

import java.util.logging.Logger;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Gesture;

public class Circle implements GestureWrapper {

    private final Logger log = Logger.getLogger(getClass().getName());

    private CircleGesture gesture;
    private float lastProgress;
    private boolean executed = false;
    private CommandQueue queue = CommandQueue.getInstance();

    public Circle(Gesture gesture) {
        update(gesture);
    }

    public void update(Gesture gesture) {
        if (this.gesture != null) {
            lastProgress = this.gesture.progress();
        }
        this.gesture = new CircleGesture(gesture);
    }

    @Override
    public boolean executed() {
        if (executed) {
            return fullCircleCompleted();
        }
        return false;
    }

    private boolean fullCircleCompleted() {
        return ((int) gesture.progress() - (int) lastProgress) > 0;
    }

    @Override
    public void setExecuted() {
        executed = true;
    }

    @Override
    public String toString() {
        SpeechPlayer.play(SpeechPlayer.A.BTN_BEEP);
        String clockwiseness = clockwise() ? "Clockwise" : "Counterclockwise";
        return clockwiseness + " circle " + gesture.id();
    }

    @Override
    public int getGestureId() {
        return gesture.id();
    }

    public void execute() {
        log.info("Executing gesture " + this);
        if (clockwise()) {
            queue.notifyGui(CommandQueue.CommandId.STEP_FORWARD);
        } else {
            queue.notifyGui(CommandQueue.CommandId.STEP_BACKWARD);
        }
    }

    private boolean clockwise() {
        return gesture.pointable().direction().angleTo(gesture.normal()) <= (Math.PI / 2);
    }

}
