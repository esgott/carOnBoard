package hu.esgott.caronboard.leap;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;
import hu.esgott.caronboard.devices.AudioFeedback;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;

public class Victory {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final float TRESHOLD = 30.0f;
    private static final long VOL_TIME = 500;

    private GestureTimer timer;
    private boolean executing;
    private float startHeight;
    private float lastHeight;
    private Timer upTimer = null;
    private Timer downTimer = null;
    private Timer centralTimer = null;
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
                disposeAllTimers();
                log.info("No victory");
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
                    execute();
                }
                return;
            }
        }
        timer.stop();
    }

    private void execute() {
        if (lastHeight < startHeight - TRESHOLD) {
            disposeUpTimer();
            disposeCentralTimer();
            scheduleDown(() -> {
                queue.notifyGui(GuiCommand.VOLUME_DEC);
            });
        } else if (lastHeight > startHeight + TRESHOLD) {
            disposeDownTimer();
            disposeCentralTimer();
            scheduleUp(() -> {
                queue.notifyGui(GuiCommand.VOLUME_INC);
            });
        } else {
            disposeDownTimer();
            disposeUpTimer();
            scheduleCentral(() -> {
                queue.notifyGui(GuiCommand.VOLUME_ACTIVE);
            });
        }
    }

    private void disposeDownTimer() {
        disposeTimer(downTimer);
        downTimer = null;
    }

    private void disposeUpTimer() {
        disposeTimer(upTimer);
        upTimer = null;
    }

    private void disposeCentralTimer() {
        disposeTimer(centralTimer);
        centralTimer = null;
    }

    private void disposeTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void disposeAllTimers() {
        disposeDownTimer();
        disposeUpTimer();
        disposeCentralTimer();
    }

    private void scheduleUp(Runnable task) {
        if (upTimer == null) {
            upTimer = schedule(task);
        }
    }

    private void scheduleDown(Runnable task) {
        if (downTimer == null) {
            downTimer = schedule(task);
        }
    }

    private void scheduleCentral(Runnable task) {
        if (centralTimer == null) {
            centralTimer = schedule(task);
        }
    }

    private Timer schedule(Runnable task) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, VOL_TIME);
        return timer;
    }

    public void dispose() {
        timer.dispose();
        disposeAllTimers();
    }

}
