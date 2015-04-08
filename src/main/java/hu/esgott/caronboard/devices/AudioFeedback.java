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
        BTN_BEEP, CORRECT, CLICK, VOLUME, VOLUME_5, VOLUME_10, VOLUME_15, VOLUME_20, VOLUME_25, VOLUME_30, VOLUME_35, VOLUME_40, VOLUME_45, TICK, DANI, FEELING, HIGHWAY, JAZZY, LAYLA, LIVIN, MEDIA, PIECE, RADIO, STRANGE, SULTANS, SWEETCHILD, TOTALCAR, HANGERO, LEFT_TEMP, RIGHT_TEMP, AIRSWITCH, NAVIGATION
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
            addClip(A.VOLUME_5, "audiofeedback/volume-5.wav");
            addClip(A.VOLUME_10, "audiofeedback/volume-10.wav");
            addClip(A.VOLUME_15, "audiofeedback/volume-15.wav");
            addClip(A.VOLUME_20, "audiofeedback/volume-20.wav");
            addClip(A.VOLUME_25, "audiofeedback/volume-25.wav");
            addClip(A.VOLUME_30, "audiofeedback/volume-30.wav");
            addClip(A.VOLUME_35, "audiofeedback/volume-35.wav");
            addClip(A.VOLUME_40, "audiofeedback/volume-40.wav");
            addClip(A.VOLUME_45, "audiofeedback/volume-45.wav");
            addClip(A.TICK, "audiofeedback/tick.wav");
            addClip(A.DANI, "tts/dani.mp3");
            addClip(A.FEELING, "tts/feelin.mp3");
            addClip(A.HANGERO, "tts/hangero.mp3");
            addClip(A.HIGHWAY, "tts/highway.mp3");
            addClip(A.JAZZY, "tts/jazzy.mp3");
            addClip(A.LAYLA, "tts/layla.mp3");
            addClip(A.LIVIN, "tts/livin.mp3");
            addClip(A.MEDIA, "tts/media.mp3");
            addClip(A.PIECE, "tts/piece.mp3");
            addClip(A.RADIO, "tts/radio.mp3");
            addClip(A.STRANGE, "tts/strange.mp3");
            addClip(A.SULTANS, "tts/sultans.mp3");
            addClip(A.SWEETCHILD, "tts/sweetchild.mp3");
            addClip(A.TOTALCAR, "tts/totalcar.mp3");
            addClip(A.LEFT_TEMP, "tts/lefttemp.mp3");
            addClip(A.RIGHT_TEMP, "tts/righttemp.mp3");
            addClip(A.AIRSWITCH, "tts/airswitch.mp3");
            addClip(A.NAVIGATION, "tts/navigation.mp3");
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
                if (queue.size() > 5) {
                    log.warning("Audio feedback queue is " + queue.size());
                }
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
        try {
            player = new MediaPlayer(media);
        } catch (NullPointerException e) {
            log.severe("Error during creation of MediaPlayer. TTS will not play");
            player = null;
            return;
        }
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
        player.setOnReady(() -> {
            log.info("Clip started");
            player.play();
        });
        player.setOnError(() -> {
            log.warning("Error in MediaPlayer");
        });
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
        if (ttsEnum(next)) {
            dropTtsInQueue();
        }
        playWithoutCheck(next);
    }

    private boolean ttsEnum(A a) {
        Media media = samples.get(a);
        return ttsClip(media);
    }

    private void dropTtsInQueue() {
        queue.removeIf((item) -> ttsEnum(item));
    }

    public void playWithoutCheck(A next) {
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

    public boolean containsVolume() {
        return queue.contains(A.VOLUME);
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
