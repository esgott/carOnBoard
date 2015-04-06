package hu.esgott.caronboard;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class CommandQueue {

    public enum GuiCommand {
        SELECT_NEXT_ELEMENT, SELECT_PREVIOUS_ELEMENT, STEP_FORWARD, STEP_BACKWARD, SELECTION_ON, SELECTION_OFF, RECORDING_ON, RECORDING_OFF, CONSUME_MATCH, VOLUME_INC, VOLUME_DEC, VOLUME_ACTIVE, TTS_ON, TTS_OFF, TRACK_CHANGED
    }

    public enum RecorderCommand {
        START_RECORDING, STOP_RECORDING, KILL
    }

    private static CommandQueue instanse = new CommandQueue();

    private final Logger log = Logger.getLogger(getClass().getName());

    private final BlockingQueue<GuiCommand> guiQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<RecorderCommand> recorderQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> matchQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Object> fistStopQueue = new LinkedBlockingQueue<>();

    public static CommandQueue getInstance() {
        return instanse;
    }

    public void notifyGui(GuiCommand id) {
        log.info("Incoming GUI command");
        addToQueue(guiQueue, id, log);
    }

    public void notifyRecorder(RecorderCommand id) {
        log.info("Incoming Recorder command");
        addToQueue(recorderQueue, id, log);
    }

    public void addMatch(String match) {
        log.info("Incoming match");
        addToQueue(matchQueue, match, log);
        fistStopQueue.offer(new Object());
    }

    private static <T> void addToQueue(BlockingQueue<T> queue, T id, Logger log) {
        boolean success = queue.offer(id);
        if (success) {
            log.info("New command inserted: " + id);
        } else {
            log.severe("Command queue full, failed to insert command " + id);
        }
        int size = queue.size();
        if (size > 5) {
            log.warning("Commmand queue size is " + size);
        }
    }

    public GuiCommand nextGuiCommand() {
        GuiCommand nextCommand = guiQueue.poll();
        if (nextCommand != null) {
            log.info("Processing command " + nextCommand);
        }
        return nextCommand;
    }

    public RecorderCommand nextRecorderCommand() {
        try {
            RecorderCommand nextCommand = recorderQueue.take();
            log.info("Processing command " + nextCommand);
            return nextCommand;
        } catch (InterruptedException e) {
            log.warning("Waiting interrupted");
        }
        return null;
    }

    public String nextMatch() {
        String nextMatch = matchQueue.poll();
        log.info("Processing match " + nextMatch);
        return nextMatch;
    }

    public boolean matchHappened() {
        return fistStopQueue.poll() != null;
    }
}
