package hu.esgott.caronboard;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class CommandQueue {

    public enum CommandId {
        SELECT_NEXT_ELEMENT, SELECT_PREVIOUS_ELEMENT
    }

    private static CommandQueue instanse = new CommandQueue();

    private final Logger log = Logger.getLogger(getClass().getName());

    private BlockingQueue<CommandId> guiQueue = new LinkedBlockingQueue<>();;

    public static CommandQueue getInstance() {
        return instanse;
    }

    public void notifyGui(CommandId id) {
        boolean success = guiQueue.offer(id);
        if (success) {
            log.info("New command inserted: " + id);
        } else {
            log.severe("Command queue full, failed to insert command " + id);
        }
        int size = guiQueue.size();
        if (size > 5) {
            log.warning("Commmand queue size is " + size);
        }
    }

    public CommandId nextGuiCommand() {
        CommandId nextCommand = guiQueue.poll();
        if (nextCommand != null) {
            log.info("Processing command " + nextCommand);
        }
        return nextCommand;
    }

}
