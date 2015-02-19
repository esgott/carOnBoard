package hu.esgott.caronboard.leap;

import com.leapmotion.leap.Gesture;

public interface GestureWrapper {

    void update(Gesture gesture);

    int getGestureId();

    boolean executed();

    void setExecuted();

    void execute();

}
