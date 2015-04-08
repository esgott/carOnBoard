package hu.esgott.caronboard.gl;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;
import hu.esgott.caronboard.CommandQueue.RecorderCommand;
import hu.esgott.caronboard.MainWindow;
import hu.esgott.caronboard.gl.object.Background;
import hu.esgott.caronboard.gl.object.DrawableObject;
import hu.esgott.caronboard.gl.object.MediaScreen;
import hu.esgott.caronboard.gl.object.RecordingActive;
import hu.esgott.caronboard.gl.object.VentillationScreen;
import hu.esgott.caronboard.gl.object.VolumeBar;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class Renderer implements GLEventListener, KeyListener {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final float SCREEN_SIZE = 4.0f;

    private final GLU glu = new GLU();
    private final Textures textures = new Textures();
    private final Background background = new Background(textures);
    private final MediaScreen mediaScreen = new MediaScreen(textures);
    private DrawableObject activeScreen = mediaScreen;
    private final VentillationScreen ventillationScreen = new VentillationScreen(
            mediaScreen, textures);
    private final RecordingActive recordingActive = new RecordingActive();
    private final VolumeBar volumeBar = new VolumeBar(3.0f, 0.4f);
    private final List<DrawableObject> objects = new ArrayList<>();
    private final Camera camera = new Camera();
    private final CommandQueue queue = CommandQueue.getInstance();

    public Renderer() {
        objects.add(background);
        objects.add(mediaScreen);
        objects.add(ventillationScreen);
        objects.add(recordingActive);
        objects.add(volumeBar);
        volumeBar.move(-1.5f, 0.0f);
    }

    @Override
    public void display(final GLAutoDrawable drawable) {
        long time = System.currentTimeMillis();
        update(time);
        GL2 gl = drawable.getGL().getGL2();
        render(gl);
    }

    private void update(final long time) {
        processNextCommmand();
        camera.update(time);
        objects.stream().forEach(object -> object.update(time));
    }

    private void processNextCommmand() {
        GuiCommand nextCommand = queue.nextGuiCommand();
        if (nextCommand == null) {
            return;
        }
        switch (nextCommand) {
        case SELECT_NEXT_SCREEN:
            nextScreen();
            break;
        case SELECT_PREVIOUS_SCREEN:
            previousScreen();
            break;
        case SELECT_RIGHT:
            selectNext();
            break;
        case SELECT_LEFT:
            selectPrevious();
            break;
        case STEP_FORWARD:
            forwardAction();
            break;
        case STEP_BACKWARD:
            backwardAction();
            break;
        case SELECTION_ON:
            selectionOn();
            break;
        case SELECTION_OFF:
            selectionOff();
            break;
        case RECORDING_ON:
            recordingActive.setVisible(true);
            mediaScreen.setRecordingState(true);
            break;
        case RECORDING_OFF:
            recordingActive.setVisible(false);
            mediaScreen.setRecordingState(false);
            break;
        case CONSUME_MATCH:
            mediaScreen.processMatch(queue.nextMatch());
            break;
        case VOLUME_INC:
            increaseVolume();
            break;
        case VOLUME_DEC:
            decreaseVolume();
            break;
        case VOLUME_ACTIVE:
            volumeActive();
            break;
        case TTS_ON:
            mediaScreen.setTtsState(true);
            break;
        case TTS_OFF:
            mediaScreen.setTtsState(false);
            break;
        case TRACK_CHANGED:
            ventillationScreen.syncTrackList();
            break;
        default:
            log.warning("Unknown command");
        }
    }

    private void render(final GL2 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glPushMatrix();
        camera.move(gl);

        objects.stream().forEach(object -> {
            gl.glPushMatrix();
            object.draw(gl);
            gl.glPopMatrix();
        });

        gl.glPopMatrix();
    }

    @Override
    public void dispose(final GLAutoDrawable drawable) {
        mediaScreen.dispose();
    }

    @Override
    public void init(final GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.setSwapInterval(1);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        textures.loadTextures(drawable.getGL().getGL2());
    }

    @Override
    public void reshape(final GLAutoDrawable drawable, final int x,
            final int y, final int width, final int height) {
        final float nonZeroHeight;
        if (height == 0) {
            nonZeroHeight = 1;
        } else {
            nonZeroHeight = height;
        }
        GL2 gl = drawable.getGL().getGL2();
        float aspect = (float) width / nonZeroHeight;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (width > height) {
            glu.gluOrtho2D(-1.0 * aspect, 1.0 * aspect, -1.0, 1.0);
        } else {
            glu.gluOrtho2D(-1.0, 1.0, -1.0 / aspect, 1.0 / aspect);
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
        case KeyEvent.VK_Q:
            log.info("Shutting down GUI after Q button");
            MainWindow.exit();
            break;
        case KeyEvent.VK_LEFT:
            camera.right();
            break;
        case KeyEvent.VK_RIGHT:
            camera.left();
            break;
        case KeyEvent.VK_UP:
            camera.down();
            break;
        case KeyEvent.VK_DOWN:
            camera.up();
            break;
        case KeyEvent.VK_L:
            selectNext();
            break;
        case KeyEvent.VK_K:
            selectPrevious();
            break;
        case KeyEvent.VK_P:
            forwardAction();
            break;
        case KeyEvent.VK_O:
            backwardAction();
            break;
        case KeyEvent.VK_R:
            queue.notifyRecorder(RecorderCommand.START_RECORDING);
            break;
        case KeyEvent.VK_T:
            queue.notifyRecorder(RecorderCommand.STOP_RECORDING);
            break;
        case KeyEvent.VK_X:
            mediaScreen.playPause();
            break;
        case KeyEvent.VK_A:
            increaseVolume();
            break;
        case KeyEvent.VK_Z:
            decreaseVolume();
            break;
        case KeyEvent.VK_N:
            previousScreen();
            break;
        case KeyEvent.VK_M:
            nextScreen();
            break;
        default:
            log.info("Not expected key");
        }
    }

    private void increaseVolume() {
        volumeBar.increaseVolume();
        mediaScreen.increaseVolume();
    }

    private void decreaseVolume() {
        volumeBar.decreaseVolume();
        mediaScreen.decreaseVolume();
    }

    private void volumeActive() {
        volumeBar.active();
    }

    public void selectNext() {
        activeScreen.selectNext();
    }

    public void selectPrevious() {
        activeScreen.selectPrevious();
    }

    public void forwardAction() {
        activeScreen.forwardAction();
    }

    public void backwardAction() {
        activeScreen.backwardAction();
    }

    public void selectionOn() {
        objects.forEach(item -> {
            item.selectionOn();
        });
    }

    public void selectionOff() {
        objects.forEach(item -> {
            item.selectionOff();
        });
    }

    public void nextScreen() {
        if (activeScreen != mediaScreen) {
            activeScreen = mediaScreen;
            camera.left(SCREEN_SIZE);
            log.info("Screen changed to MEDIA");
        }
    }

    public void previousScreen() {
        if (activeScreen != ventillationScreen) {
            activeScreen = ventillationScreen;
            camera.right(SCREEN_SIZE);
            log.info("Screen changed to VENTILLATION");
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

}
