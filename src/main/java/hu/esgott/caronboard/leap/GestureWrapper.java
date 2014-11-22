package hu.esgott.caronboard.leap;

import com.leapmotion.leap.Gesture;

public interface GestureWrapper {

    void update(Gesture gesture);

    int getGestureId();

    boolean toPrint();

    void setPrinted();

}
