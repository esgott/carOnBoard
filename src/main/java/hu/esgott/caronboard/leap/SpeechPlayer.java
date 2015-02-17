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

public class SpeechPlayer {

    public enum A {
        ONE, TWO, THREE, FOUR, FIVE, SWIPE, LEFT, RIGHT, UP, DOWN, WITH, FINGERS, CIRCLE, VICTORY, VICTORY_OFF
    }

    private static BlockingQueue<A> queue = new LinkedBlockingQueue<>();
    private boolean running = true;
    private Map<A, Clip> samples = new HashMap<>();

    public SpeechPlayer() {
        try {
            addClip(A.ONE, "one.wav");
            addClip(A.TWO, "two.wav");
            addClip(A.THREE, "three.wav");
            addClip(A.FOUR, "four.wav");
            addClip(A.FIVE, "five.wav");
            addClip(A.SWIPE, "swipe.wav");
            addClip(A.LEFT, "left.wav");
            addClip(A.RIGHT, "right.wav");
            addClip(A.UP, "up.wav");
            addClip(A.DOWN, "down.wav");
            addClip(A.WITH, "with.wav");
            addClip(A.FINGERS, "fingers.wav");
            addClip(A.CIRCLE, "circle.wav");
            addClip(A.VICTORY, "victory.wav");
            addClip(A.VICTORY_OFF, "victory_off.wav");
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

    public static A num(int number) {
        switch (number) {
        case 1:
            return A.ONE;
        case 2:
            return A.TWO;
        case 3:
            return A.THREE;
        case 4:
            return A.FOUR;
        default:
            return A.FIVE;
        }
    }

    public void dispose() {
        running = false;
        samples.values().forEach(clip -> {
            clip.close();
        });
    }

}