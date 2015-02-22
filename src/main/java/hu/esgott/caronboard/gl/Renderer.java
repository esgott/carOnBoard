package hu.esgott.caronboard.gl;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;
import hu.esgott.caronboard.CommandQueue.RecorderCommand;
import hu.esgott.caronboard.MainWindow;
import hu.esgott.caronboard.gl.object.DrawableObject;
import hu.esgott.caronboard.gl.object.MediaScreen;
import hu.esgott.caronboard.gl.object.RecordingActive;

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

    private final GLU glu = new GLU();
    private final MediaScreen mediaScreen = new MediaScreen();
    private final RecordingActive recordingActive = new RecordingActive();
    private final List<DrawableObject> objects = new ArrayList<>();
    private final Camera camera = new Camera();
    private final CommandQueue queue = CommandQueue.getInstance();

    public Renderer() {
        objects.add(mediaScreen);
        objects.add(recordingActive);
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
        objects.stream().forEach(object -> object.update(time));
    }

    private void processNextCommmand() {
        GuiCommand nextCommand = queue.nextGuiCommand();
        if (nextCommand == null) {
            return;
        }
        switch (nextCommand) {
        case SELECT_NEXT_ELEMENT:
            selectNext();
            break;
        case SELECT_PREVIOUS_ELEMENT:
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
            break;
        case RECORDING_OFF:
            recordingActive.setVisible(false);
            break;
        case CONSUME_MATCH:
            mediaScreen.processMatch(queue.nextMatch());
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
        drawable.getGL().setSwapInterval(1);
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
            camera.left();
            break;
        case KeyEvent.VK_RIGHT:
            camera.right();
            break;
        case KeyEvent.VK_UP:
            camera.up();
            break;
        case KeyEvent.VK_DOWN:
            camera.down();
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
        default:
            log.info("Not expected key");
        }
    }

    public void selectNext() {
        mediaScreen.selectNext();
    }

    public void selectPrevious() {
        mediaScreen.selectPrevious();
    }

    public void forwardAction() {
        mediaScreen.forwardAction();
    }

    public void backwardAction() {
        mediaScreen.backwardAction();
    }

    public void selectionOn() {
        mediaScreen.selectionOn();
    }

    public void selectionOff() {
        mediaScreen.selectionOff();
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

}
