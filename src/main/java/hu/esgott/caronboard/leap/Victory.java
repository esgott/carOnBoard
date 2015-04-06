package hu.esgott.caronboard.leap;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;
import hu.esgott.caronboard.devices.AudioFeedback;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Vector;

public class Victory {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final float Y_TRESHOLD = 35.0f;
    private static final float XZ_TRESHOLD = 10.0f;
    private static final long VOL_TIME = 750;
    public static final float MIN_ANGLE = 0.25f;

    private GestureTimer timer;
    private boolean executing;
    private Vector startPoint;
    private Vector lastPoint;
    private Timer upTimer = null;
    private Timer downTimer = null;
    private Timer centralTimer = null;
    private final CommandQueue queue = CommandQueue.getInstance();
    private final AudioFeedback audioFeedback = AudioFeedback.getInstance();

    public Victory() {
        Runnable task = () -> {
            if (!audioFeedback.containsVolume()) {
                audioFeedback.play(AudioFeedback.A.HANGERO);
            }
            log.info("Victory");
            executing = true;
            startPoint = lastPoint;
            queue.notifyGui(GuiCommand.VOLUME_ACTIVE);
        };
        Runnable onStop = () -> {
            if (executing) {
                executing = false;
                startPoint = null;
                disposeAllTimers();
                log.info("No victory");
            }
        };
        timer = new GestureTimer(0.5f, 75, task, null, onStop);
    }

    public void update(HandList hands) {
        if (hands.count() == 1 && approximatelyStartPosition()) {
            Hand hand = hands.get(0);
            lastPoint = hand.palmPosition();
            FingerList extendedFingers = hand.fingers().extended();
            if (extendedFingers.count() == 2) {
                if (finersInAngle(extendedFingers)) {
                    timer.start();
                    if (executing) {
                        execute();
                    }
                    return;
                }
            }
        }
        timer.stop();
    }

    private boolean approximatelyStartPosition() {
        if (startPoint != null && lastPoint != null) {
            boolean xOk = Math.abs(startPoint.getX() - lastPoint.getX()) < XZ_TRESHOLD;
            boolean zOk = Math.abs(startPoint.getZ() - lastPoint.getZ()) < XZ_TRESHOLD;
            return xOk && zOk;
        } else {
            log.fine("Assuming approximatley startpoint");
            return true;
        }
    }

    private boolean finersInAngle(FingerList fingers) {
        Finger leftFinger = fingers.get(0);
        Finger rightFinger = fingers.get(1);
        float angle = leftFinger.direction().angleTo(rightFinger.direction());
        return angle > MIN_ANGLE;
    }

    private void execute() {
        float lastY = lastPoint.getY();
        float startY = startPoint.getY();
        if (lastY < startY - Y_TRESHOLD) {
            disposeUpTimer();
            disposeCentralTimer();
            scheduleDown(() -> {
                queue.notifyGui(GuiCommand.VOLUME_DEC);
            });
        } else if (lastY > startY + Y_TRESHOLD) {
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
            log.info("Disposing timer " + timer);
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
            log.info("Up timer created " + upTimer);
        }
    }

    private void scheduleDown(Runnable task) {
        if (downTimer == null) {
            downTimer = schedule(task);
            log.info("Down timer created " + downTimer);
        }
    }

    private void scheduleCentral(Runnable task) {
        if (centralTimer == null) {
            centralTimer = schedule(task);
            log.info("Central timer created " + centralTimer);
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
