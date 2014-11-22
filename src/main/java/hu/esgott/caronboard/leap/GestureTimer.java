package hu.esgott.caronboard.leap;

import java.util.Timer;
import java.util.TimerTask;

public class GestureTimer {

    private int frameUntilCancel;
    private int frameLeft;
    private long timeUntilStart;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private Runnable task;
    private boolean running = false;
    private Runnable onStart;
    private Runnable onStop;

    public GestureTimer(float secondsUntilStart, int frameUntilCancel,
            Runnable task, Runnable onStart, Runnable onStop) {
        this.frameUntilCancel = frameUntilCancel;
        frameLeft = frameUntilCancel;
        timeUntilStart = Math.round(secondsUntilStart * 1000);
        this.task = task;
        this.onStart = onStart;
        this.onStop = onStop;
    }

    public void start() {
        if (!running) {
            running = true;
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    task.run();
                }
            };
            timer.schedule(timerTask, timeUntilStart);
            if (onStart != null) {
                onStart.run();
            }
        }
        frameLeft = frameUntilCancel;
    }

    public void stop() {
        if (running && (frameLeft == 0)) {
            timerTask.cancel();
            running = false;
            if (onStop != null) {
                onStop.run();
            }
        }
        if (frameLeft > 0) {
            frameLeft--;
        }
    }

    public void dispose() {
        timer.cancel();
    }

}
