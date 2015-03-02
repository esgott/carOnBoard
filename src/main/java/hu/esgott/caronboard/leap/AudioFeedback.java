package hu.esgott.caronboard.leap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioFeedback {

    public enum A {
        BTN_BEEP, CORRECT, CLICK
    }

    private static final AudioFeedback instance = new AudioFeedback();
    private static final BlockingQueue<A> queue = new LinkedBlockingQueue<>();
    private boolean running = true;
    private final Map<A, Clip> samples = new HashMap<>();
    private final Lock clipsLock = new ReentrantLock();

    private AudioFeedback() {
        try {
            addClip(A.BTN_BEEP, "btn_beep.wav");
            addClip(A.CORRECT, "correct.wav");
            addClip(A.CLICK, "click.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runnable r = () -> threadMain();
        Thread thread = new Thread(r);
        thread.start();
    }

    public static AudioFeedback getInstance() {
        return instance;
    }

    private void addClip(A key, String fileName) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(
                    fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            samples.put(key, clip);
        } catch (LineUnavailableException | UnsupportedAudioFileException
                | IOException e) {
            e.printStackTrace();
        }
    }

    private void threadMain() {
        try {
            while (running) {
                A next = queue.poll(100, TimeUnit.MILLISECONDS);
                if (next != null) {
                    Clip clip = samples.get(next);
                    resetClip(clip);
                    waitUntilStarted(clip);
                    waitUntilPlayed(clip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetClip(Clip clip) {
        try {
            clipsLock.lock();
            clip.setFramePosition(0);
            clip.start();
        } finally {
            clipsLock.unlock();
        }
    }

    private void waitUntilStarted(Clip clip) throws InterruptedException {
        Thread.sleep(10);
    }

    private void waitUntilPlayed(Clip clip) throws InterruptedException {
        while (playing(clip)) {
            Thread.sleep(100);
        }
    }

    private boolean playing(Clip clip) {
        boolean playing = false;
        try {
            clipsLock.lock();
            playing = clip.isRunning();
        } finally {
            clipsLock.unlock();
        }
        return playing;
    }

    public void play(A next) {
        try {
            queue.put(next);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void dispose() {
        instance.stop();
    }

    private void stop() {
        running = false;
        samples.values().forEach(clip -> {
            clip.close();
        });
    }

}
