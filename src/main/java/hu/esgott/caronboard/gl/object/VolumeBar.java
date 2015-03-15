package hu.esgott.caronboard.gl.object;

import hu.esgott.caronboard.devices.AudioFeedback;
import hu.esgott.caronboard.devices.VolumeLevel;

import java.util.logging.Logger;

import javax.media.opengl.GL2;

public class VolumeBar extends DrawableObject {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final float FRAME_THICKNESS = 0.15f;
    private static final long HIDE_TIME = 1000;

    private final float width;
    private final float height;
    private boolean display = false;
    private long volumeTime;
    private final VolumeLevel volume = new VolumeLevel();
    private final AudioFeedback audioFeedback = AudioFeedback.getInstance();

    public VolumeBar(final float width, final float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(final long time) {
        if (display && (volumeTime + HIDE_TIME) < time) {
            display = false;
            log.info("Volume bar hidden");
        }
    }

    @Override
    public void draw(GL2 gl) {
        if (!display) {
            return;
        }
        gl.glPushMatrix();
        gl.glTranslatef(X(), Y(), 0);
        gl.glColor3f(0.0f, 0.0f, 0.2f);
        Shapes.drawRectangle(gl, 0, 0, width, height);
        gl.glColor3f(0.0f, 0.05f, 0.4f);
        Shapes.drawRectangle(gl, FRAME_THICKNESS, FRAME_THICKNESS,
                FRAME_THICKNESS + barWidth(), height - FRAME_THICKNESS);
        gl.glPopMatrix();
    }

    private float barWidth() {
        return (width - (2 * FRAME_THICKNESS)) * (float) volume.getVolume();
    }

    @Override
    public String getName() {
        return "Volume bar";
    }

    public void increaseVolume() {
        volume.increase();
        audioFeedback.increaseVolume();
        audioFeedback.play(volume.getClip());
        active();
    }

    public void decreaseVolume() {
        volume.decrease();
        audioFeedback.decreaseVolume();
        audioFeedback.play(volume.getClip());
        active();
    }

    public void active() {
        display = true;
        volumeTime = System.currentTimeMillis();
        log.info("Volume bar active");
    }

}
