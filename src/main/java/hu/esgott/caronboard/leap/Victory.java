package hu.esgott.caronboard.leap;

import java.util.logging.Logger;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;

public class Victory {

    private static final float SCALE = 0.1f;

    private final Logger log = Logger.getLogger(getClass().getName());
    private GestureTimer timer;
    private boolean executing;
    private float startHeight;
    private float lastHeight;
    private int level;

    public Victory() {
        Runnable task = () -> {
            SpeechPlayer.play(SpeechPlayer.A.BTN_BEEP);
            log.info("Victory");
            executing = true;
            startHeight = lastHeight;
        };
        Runnable onStop = () -> {
            if (executing) {
                executing = false;
                log.info("No victory");
                SpeechPlayer.play(SpeechPlayer.A.BTN_BEEP);
            }
        };
        timer = new GestureTimer(0.5f, 30, task, null, onStop);
    }

    public void update(HandList hands) {
        if (hands.count() == 1) {
            Hand hand = hands.get(0);
            lastHeight = hand.palmPosition().getY();
            if (hand.fingers().extended().count() == 2) {
                timer.start();
                if (executing) {
                    setLevel(hand);
                }
                return;
            }
        }
        timer.stop();
    }

    private void setLevel(Hand hand) {
        int newLevel = Math.round((lastHeight - startHeight) * SCALE);
        if (newLevel != level) {
            log.info("level: " + newLevel);
            level = newLevel;
        }
    }

    public void dispose() {
        timer.dispose();
    }

}
