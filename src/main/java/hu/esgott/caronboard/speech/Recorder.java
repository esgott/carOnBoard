package hu.esgott.caronboard.speech;

import hu.esgott.caronboard.CommandQueue;
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
                stop();
                break;
            case KILL:
                running = false;
                break;
            default:
                log.warning("Unrecognized recorder command");
            }
        }
    }

    public void record() {
        if (!recording()) {
            // TODO display recording
            recorderThread = new RecorderThread(recognizerConnection);
            thread = new Thread(recorderThread);
            thread.start();
        }
    }

    public void stop() {
        stopRunningRecording();
        // TODO hide recording
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
    }

    public boolean recording() {
        return recorderThread != null;
    }

}
