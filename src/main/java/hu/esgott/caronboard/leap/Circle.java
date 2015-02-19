package hu.esgott.caronboard.leap;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Gesture;

public class Circle implements GestureWrapper {

    private CircleGesture gesture;
    private float lastProgress;
    private boolean executed = false;

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
        SpeechPlayer.play(SpeechPlayer.A.CIRCLE);
        return "Circle " + gesture.id();
    }

    @Override
    public int getGestureId() {
        return gesture.id();
    }

    public void execute() {
        // TODO
    }

}
