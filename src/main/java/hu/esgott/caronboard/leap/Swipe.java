package hu.esgott.caronboard.leap;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;

import java.util.logging.Logger;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
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
        if (fingers == 1 || stickedFingers()) {
            if (left() || up()) {
                addNextElementToQueue();
                return;
            }
            if (right() || down()) {
                addPreviousElementToQueue();
                return;
            }
        } else if (fingers > 3) {
            if (left()) {
                addNextScreenToQueue();
                return;
            }
            if (right()) {
                addPreviousScreenToQueue();
                return;
            }
        }
        log.info("Discarded gesture " + this);
    }

    private boolean stickedFingers() {
        HandList hands = gesture.hands();
        if (hands.count() == 1) {
            Hand hand = hands.get(0);
            FingerList extendedFingers = hand.fingers().extended();
            if (extendedFingers.count() == 2) {
                Finger leftFinger = extendedFingers.get(0);
                Finger rightFinger = extendedFingers.get(1);
                float angle = leftFinger.direction().angleTo(
                        rightFinger.direction());
                return angle <= Victory.MIN_ANGLE;
            }
        }
        return false;
    }

    private void addNextElementToQueue() {
        addToQueue(GuiCommand.SELECT_NEXT_ELEMENT);
    }

    private void addPreviousElementToQueue() {
        addToQueue(GuiCommand.SELECT_PREVIOUS_ELEMENT);
    }

    private void addNextScreenToQueue() {
        addToQueue(GuiCommand.SELECT_NEXT_SCREEN);
    }

    private void addPreviousScreenToQueue() {
        addToQueue(GuiCommand.SELECT_PREVIOUS_SCREEN);
    }

    private void addToQueue(GuiCommand command) {
        log.info("Executing gesture " + this);
        queue.notifyGui(command);
    }

}
