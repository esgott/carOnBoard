package hu.esgott.caronboard.devices;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioFeedback {

    public enum A {
        BTN_BEEP, CORRECT, CLICK, VOLUME, TICK
    }

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final int MAX_LEVEL = 10;
    private static final int MIN_LEVEL = 0;

    private static final AudioFeedback instance = new AudioFeedback();
    private final BlockingQueue<A> queue = new LinkedBlockingQueue<>();
    private boolean running = true;
    private final Map<A, Clip> samples = new HashMap<>();
    private final Lock lock = new ReentrantLock();
    private int volume = MAX_LEVEL;

    private AudioFeedback() {
        try {
            addClip(A.BTN_BEEP, "resources/audiofeedback/btn_beep.wav");
            addClip(A.CORRECT, "resources/audiofeedback/correct.wav");
            addClip(A.CLICK, "resources/audiofeedback/click.wav");
            addClip(A.VOLUME, "resources/audiofeedback/volume.wav");
            addClip(A.TICK, "resources/audiofeedback/tick.wav");
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
            lock.lock();
            clip.setFramePosition(0);
            clip.start();
        } finally {
            lock.unlock();
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
            lock.lock();
            playing = clip.isRunning();
        } finally {
            lock.unlock();
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

    public void increaseVolume() {
        try {
            lock.lock();
            if (volume >= MAX_LEVEL) {
                return;
            }
            volume++;
            setVolume(volume);
        } finally {
            lock.unlock();
        }
    }

    public void decreaseVolume() {
        try {
            lock.lock();
            if (volume <= MIN_LEVEL) {
                return;
            }
            volume--;
            setVolume(volume);
        } finally {
            lock.unlock();
        }
    }

    private void setVolume(int volume) {
        double gain = volume / 10.0D;
        float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
        samples.forEach((key, clip) -> {
            FloatControl gainControl = (FloatControl) clip
                    .getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(dB);
        });
        log.info("Feedback volume set to " + volume + " (gain=" + gain + " dB="
                + dB + ")");
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
