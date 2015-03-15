package hu.esgott.caronboard.leap;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;
import hu.esgott.caronboard.devices.AudioFeedback;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;

public class FrameWrapper {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final int MIN_DIFF_BETWEEN_GESTURES = 750;
    private static final long MIN_STEADY_TIME = 500;
    public static final float MAX_STEADY_PALM_VELOCITY = 25.0f;

    public Frame f;
    private Swipe ongoingSwipe;
    private Circle ongoingCircle;
    private long lastGestureTime;
    private long lastSteadyTime = 0;
    private Victory victory = new Victory();
    private Fist fist = new Fist();
    private boolean wasSteadyEnough = false;
    private CommandQueue queue = CommandQueue.getInstance();
    private final AudioFeedback audioFeedback = AudioFeedback.getInstance();

    @SuppressWarnings("incomplete-switch")
    public void update(Frame frame) {
        f = frame;
        Gesture firstSwipe = null;
        Gesture firstCircle = null;
        int swipeFingers = 0;
        long currentTime = System.currentTimeMillis();

        if (!steadyEnough(currentTime)) {
            return;
        }

        if (currentTime - lastGestureTime > MIN_DIFF_BETWEEN_GESTURES) {
            for (Gesture gesture : f.gestures()) {
                switch (gesture.type()) {
                case TYPE_SWIPE:
                    if (firstSwipe == null) {
                        firstSwipe = gesture;
                    }
                    swipeFingers++;
                    break;
                case TYPE_CIRCLE:
                    if (firstCircle == null) {
                        firstCircle = gesture;
                    }
                    break;
                }
            }
        }

        updateSwipe(firstSwipe, swipeFingers, currentTime);
        updateCircle(firstCircle, currentTime);
        victory.update(f.hands());
        fist.update(f.hands());
    }

    private boolean steadyEnough(long currentTime) {
        if (steady()) {
            if (lastSteadyTime == 0) {
                lastSteadyTime = currentTime;
            }
            if (wasSteadyEnough) {
                return true;
            }
            if ((currentTime - lastSteadyTime) > MIN_STEADY_TIME) {
                wasSteadyEnough = true;
                audioFeedback.play(AudioFeedback.A.CLICK);
                queue.notifyGui(GuiCommand.SELECTION_ON);
                return true;
            }
        } else {
            if (wasSteadyEnough) {
                queue.notifyGui(GuiCommand.SELECTION_OFF);
                wasSteadyEnough = false;
            }
            lastSteadyTime = 0;
        }
        return false;
    }

    private boolean steady() {
        int handCount = f.hands().count();
        if (handCount == 1) {
            if (wasSteadyEnough) {
                log.fine("One hand and already steady");
                return true;
            } else {
                Hand hand = f.hand(0);
                float velocity = hand.palmVelocity().magnitude();
                if (velocity < MAX_STEADY_PALM_VELOCITY) {
                    log.fine("Steady");
                    return true;
                }
                log.fine("One hand, palm too fast");
            }
        }
        log.finer("No hand or too many hands");
        return false;
    }

    private void updateSwipe(Gesture gesture, int fingers, long currentTime) {
        if (gesture == null) {
            ongoingSwipe = null;
        } else {
            if (ongoingSwipe == null) {
                lastGestureTime = currentTime;
                ongoingSwipe = new Swipe(gesture, fingers);
            } else {
                ongoingSwipe.update(gesture);
            }
        }
    }

    private void updateCircle(Gesture gesture, long currentTime) {
        if (gesture == null) {
            ongoingCircle = null;
        } else {
            if (ongoingCircle == null) {
                lastGestureTime = currentTime;
                ongoingCircle = new Circle(gesture);
            } else {
                ongoingCircle.update(gesture);
            }
        }
    }

    public Stream<GestureWrapper> gestures() {
        List<GestureWrapper> gestureList = new ArrayList<>();
        if (ongoingSwipe != null) {
            gestureList.add(ongoingSwipe);
        }
        if (ongoingCircle != null) {
            gestureList.add(ongoingCircle);
        }
        return gestureList.stream();
    }

    void dispose() {
        victory.dispose();
        fist.dispose();
    }

}
