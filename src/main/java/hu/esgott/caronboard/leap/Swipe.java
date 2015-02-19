package hu.esgott.caronboard.leap;

import hu.esgott.caronboard.CommandQueue;

import java.util.logging.Logger;

import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;

public class Swipe implements GestureWrapper {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final float MAX_ANGLE_DIFF = 1.0f;

    private SwipeGesture gesture;
    private boolean executed = false;
    private int fingers;
    private CommandQueue queue = CommandQueue.getInstance();

    public Swipe(Gesture gesture, int fingers) {
        this.fingers = fingers;
        update(gesture);
    }

    public void update(Gesture gesture) {
        this.gesture = new SwipeGesture(gesture);
        HandList hands = gesture.hands();
        if (hands.count() == 1) {
            Hand hand = hands.get(0);
            fingers = hand.fingers().extended().count();
        }
    }

    public boolean executed() {
        return executed;
    }

    public void setExecuted() {
        executed = true;
    }

    @Override
    public String toString() {
        String str = "Swipe";
        if (left()) {
            str += " left";
        }
        if (right()) {
            str += " right";
        }
        if (up()) {
            str += " up";
        }
        if (down()) {
            str += " down";
        }
        if (str.length() == 5) {
            str += " unknown";
        }
        str += " with fingers " + fingers;
        AudioFeedback.play(AudioFeedback.A.CORRECT);
        return str + " id: " + gesture.id();
    }

    public boolean left() {
        return similarDirections(gesture.direction(), Vector.left());
    }

    public boolean right() {
        return similarDirections(gesture.direction(), Vector.right());
    }

    public boolean up() {
        return similarDirections(gesture.direction(), Vector.up());
    }

    public boolean down() {
        return similarDirections(gesture.direction(), Vector.down());
    }

    private boolean similarDirections(Vector v1, Vector v2) {
        return v1.angleTo(v2) < MAX_ANGLE_DIFF;
    }

    public boolean sameGesture(Swipe other) {
        if (sameHands(other)) {
            if (this.left()) {
                return other.left();
            }
            if (this.right()) {
                return other.right();
            }
        }
        return false;
    }

    private boolean sameHands(Swipe other) {
        HandList thisHands = this.gesture.hands();
        HandList otherHands = other.gesture.hands();
        if (otherHands.count() == 1 && thisHands.count() == 1) {
            return otherHands.get(0).equals(thisHands.get(0));
        } else {
            log.severe("multiple hands in swipe gesture");
            return false;
        }
    }

    @Override
    public int getGestureId() {
        return gesture.id();
    }

    public void execute() {
        if (fingers == 1) {
            if (left()) {
                addToQueue(true);
                return;
            } else if (right()) {
                addToQueue(false);
                return;
            }
        }
        log.info("Discarded gesture " + this);
    }

    private void addToQueue(boolean left) {
        log.info("Executing gesture " + this);
        if (left) {
            queue.notifyGui(CommandQueue.CommandId.SELECT_PREVIOUS_ELEMENT);
        } else {
            queue.notifyGui(CommandQueue.CommandId.SELECT_NEXT_ELEMENT);
        }
    }

}
