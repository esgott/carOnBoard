package hu.esgott.caronboard.leap;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;
import hu.esgott.caronboard.devices.AudioFeedback;

import java.util.logging.Logger;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;

public class Victory {

    private static final float SCALE = 0.03f;

    private final Logger log = Logger.getLogger(getClass().getName());

    private GestureTimer timer;
    private boolean executing;
    private float startHeight;
    private float lastHeight;
    private int level = 0;
    private final CommandQueue queue = CommandQueue.getInstance();
    private final AudioFeedback audioFeedback = AudioFeedback.getInstance();

    public Victory() {
        Runnable task = () -> {
            if (!audioFeedback.containsVolume()) {
                audioFeedback.play(AudioFeedback.A.VOLUME);
            }
            log.info("Victory");
            executing = true;
            startHeight = lastHeight;
            queue.notifyGui(GuiCommand.VOLUME_ACTIVE);
        };
        Runnable onStop = () -> {
            if (executing) {
                executing = false;
                log.info("No victory");
                level = 0;
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
        if (newLevel < level) {
            level--;
            queue.notifyGui(GuiCommand.VOLUME_DEC);
        } else if (newLevel > level) {
            level++;
            queue.notifyGui(GuiCommand.VOLUME_INC);
        }
    }

    public void dispose() {
        timer.dispose();
    }

}
