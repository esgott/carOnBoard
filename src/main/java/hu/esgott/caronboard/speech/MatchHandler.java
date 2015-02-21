package hu.esgott.caronboard.speech;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.GuiCommand;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchHandler {

    private final Logger log = Logger.getLogger(getClass().getName());

    private final CommandQueue queue = CommandQueue.getInstance();

    public void handleMatch(String response) {
        String matchedString = parseResponse(response);
        log.info("Found match: " + matchedString);
        doActionForMatch(matchedString);
    }

    private String parseResponse(String response) {
        String[] lines = response.split("\n");
        String matchLine = lines[1];
        // third column
        Pattern pattern = Pattern.compile("\\s*\\d+\\s+\\d+\\s+(\\S+)\\s+#.*");
        Matcher matcher = pattern.matcher(matchLine);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            log.warning("Response not understood: " + response);
            return "";
        }
    }

    private void doActionForMatch(String match) {
        switch (match) {
        case "szellozes":
            queue.notifyGui(GuiCommand.SELECT_NEXT_ELEMENT);
        }
    }

}
