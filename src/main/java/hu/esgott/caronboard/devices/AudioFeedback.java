package hu.esgott.caronboard.devices;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioFeedback {

    public enum A {
        BTN_BEEP, CORRECT, CLICK, VOLUME, TICK, DANI, FEELING, HIGHWAY, JAZZY, LAYLA, LIVIN, MEDIA, PIECE, RADIO, STRANGE, SULTANS, SWEETCHILD, TOTALCAR
    }

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final AudioFeedback instance = new AudioFeedback();
    private final BlockingQueue<A> queue = new LinkedBlockingQueue<>();
    private boolean running = true;
    private final Map<A, Media> samples = new HashMap<>();
    private final VolumeLevel volume = new VolumeLevel();
    private MediaPlayer player = null;

    private AudioFeedback() {
        try {
            addClip(A.BTN_BEEP, "audiofeedback/btn_beep.wav");
            addClip(A.CORRECT, "audiofeedback/correct.wav");
            addClip(A.CLICK, "audiofeedback/click.wav");
            addClip(A.VOLUME, "audiofeedback/volume.wav");
            addClip(A.TICK, "audiofeedback/tick.wav");
            addClip(A.DANI, "tts/dani.wav");
            addClip(A.FEELING, "tts/feeling.wav");
            addClip(A.HIGHWAY, "tts/highway.wav");
            addClip(A.JAZZY, "tts/jazzy.wav");
            addClip(A.LAYLA, "tts/layla.wav");
            addClip(A.LIVIN, "tts/livin.wav");
            addClip(A.MEDIA, "tts/media.wav");
            addClip(A.PIECE, "tts/piece.wav");
            addClip(A.RADIO, "tts/radio.wav");
            addClip(A.STRANGE, "tts/strange.wav");
            addClip(A.SULTANS, "tts/sultans.wav");
            addClip(A.SWEETCHILD, "tts/sweetchild.wav");
            addClip(A.TOTALCAR, "tts/totalcar.wav");
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
        Media media = new Media(new File("resources/" + fileName).toURI()
                .toString());
        samples.put(key, media);
    }

    private void threadMain() {
        try {
            while (running) {
                A next = queue.poll(100, TimeUnit.MILLISECONDS);
                if (next != null) {
                    Media media = samples.get(next);
                    playMedia(media);
                    waitUntilPlayed(media);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playMedia(Media media) {
        if (ttsClip(media)) {
            CommandQueue.getInstance().notifyGui(GuiCommand.TTS_ON);
        }
        log.info("Create player for media " + media);
        player = new MediaPlayer(media);
        setVolume();
        player.setOnEndOfMedia(() -> {
            if (player != null) {
                player.dispose();
                player = null;
            }
            if (ttsClip(media)) {
                CommandQueue.getInstance().notifyGui(GuiCommand.TTS_OFF);
            }
            log.info("Clip stopped");
        });
        player.play();
        log.info("Clip started");
    }

    private void waitUntilPlayed(Media media) throws InterruptedException {
        while (player != null) {
            Thread.sleep(100);
        }
    }

    private boolean ttsClip(Media media) {
        return media.getSource().contains("/tts/");
    }

    public void play(A next) {
        try {
            queue.put(next);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void increaseVolume() {
        volume.increase();
        setVolume();
    }

    public void decreaseVolume() {
        volume.decrease();
        setVolume();
    }

    private void setVolume() {
        if (player != null) {
            player.setVolume(volume.getVolume());
            log.info("Feedback volume set to " + volume);
        }
    }

    public static void dispose() {
        instance.stop();
    }

    private void stop() {
        running = false;
        if (player != null) {
            player.dispose();
            player = null;
        }
    }

}
