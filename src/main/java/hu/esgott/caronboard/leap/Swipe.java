package hu.esgott.caronboard.leap;

import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;

public class Swipe implements GestureWrapper {

    private static final float MAX_ANGLE_DIFF = 1.0f;

    private SwipeGesture gesture;
    private boolean printed = false;
    private int fingers;

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

    public boolean toPrint() {
        return !printed;
    }

    public void setPrinted() {
        printed = true;
    }

    @Override
    public String toString() {
        String str = "Swipe";
        SpeechPlayer.play(SpeechPlayer.A.SWIPE);
        if (left()) {
            str += " left";
            SpeechPlayer.play(SpeechPlayer.A.LEFT);
        }
        if (right()) {
            str += " right";
            SpeechPlayer.play(SpeechPlayer.A.RIGHT);
        }
        if (up()) {
            str += " up";
            SpeechPlayer.play(SpeechPlayer.A.UP);
        }
        if (down()) {
            str += " down";
            SpeechPlayer.play(SpeechPlayer.A.DOWN);
        }
        if (str.length() == 5) {
            str += " unknown";
        }
        str += " with fingers " + fingers;
        SpeechPlayer.play(SpeechPlayer.num(fingers));
        SpeechPlayer.play(SpeechPlayer.A.FINGERS);
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
            System.err.println("multiple hands in swipe gesture");
            return false;
        }
    }

    @Override
    public int getGestureId() {
        return gesture.id();
    }

}