package hu.esgott.caronboard.speech;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;
import hu.esgott.caronboard.CommandQueue.RecorderCommand;

import java.util.logging.Logger;

public class Recorder implements Runnable {

    private final Logger log = Logger.getLogger(getClass().getName());

    private RecognizerServerConnection recognizerConnection;
    private RecorderThread recorderThread;
    private Thread thread;
    private final CommandQueue queue = CommandQueue.getInstance();

    public Recorder(RecognizerServerConnection recognizerConnection) {
        this.recognizerConnection = recognizerConnection;
    }

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            RecorderCommand command = queue.nextRecorderCommand();
            switch (command) {
            case START_RECORDING:
                record();
                break;
            case STOP_RECORDING:
                stopRunningRecording();
                break;
            case KILL:
                running = false;
                stopRunningRecording();
                break;
            default:
                log.warning("Unrecognized recorder command");
            }
        }
        log.info("Recorder command thread finished");
    }

    public void record() {
        if (!recording()) {
            queue.notifyGui(GuiCommand.RECORDING_ON);
            recorderThread = new RecorderThread(recognizerConnection);
            thread = new Thread(recorderThread);
            thread.start();
        }
    }

    private void stopRunningRecording() {
        if (recorderThread != null) {
            recorderThread.stop();
            try {
                log.fine("Waiting for recorder thread  to finish");
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recorderThread = null;
        }
        queue.notifyGui(GuiCommand.RECORDING_OFF);
    }

    public boolean recording() {
        return recorderThread != null;
    }

}
