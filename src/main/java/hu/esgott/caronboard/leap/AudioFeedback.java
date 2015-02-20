package hu.esgott.caronboard.leap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioFeedback {

    public enum A {
        BTN_BEEP, CORRECT, CLICK
    }

    private static BlockingQueue<A> queue = new LinkedBlockingQueue<>();
    private boolean running = true;
    private Map<A, Clip> samples = new HashMap<>();

    public AudioFeedback() {
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
                    clip.setFramePosition(0);
                    clip.start();
                    waitUntilStarted(clip);
                    waitUntilPlayed(clip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitUntilStarted(Clip clip) throws InterruptedException {
        Thread.sleep(10);
    }

    private void waitUntilPlayed(Clip clip) throws InterruptedException {
        while (clip.isRunning()) {
            Thread.sleep(100);
        }
    }

    public static void play(A next) {
        try {
            queue.put(next);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        running = false;
        samples.values().forEach(clip -> {
            clip.close();
        });
    }

}
