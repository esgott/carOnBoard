package hu.esgott.caronboard.leap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;

public class FrameWrapper {

    private static final int MIN_DIFF_BETWEEN_GESTURES = 750;

    public Frame f;
    private Swipe ongoingSwipe;
    private Circle ongoingCircle;
    private long lastGestureTime;
    private Victory victory = new Victory();

    @SuppressWarnings("incomplete-switch")
    public void update(Frame frame) {
        f = frame;
        Gesture firstSwipe = null;
        Gesture firstCircle = null;
        int swipeFingers = 0;
        long currentTime = System.currentTimeMillis();

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
    }

}
