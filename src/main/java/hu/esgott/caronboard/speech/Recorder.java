package hu.esgott.caronboard.speech;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recorder {

    private final Logger log = Logger.getLogger(getClass().getName());

    private RecognizerServerConnection recognizerConnection;
    private RecorderThread recorderThread;
    private Thread thread;

    public Recorder(RecognizerServerConnection recognizerConnection) {
        this.recognizerConnection = recognizerConnection;
    }

    public void record() {
        if (!running()) {
            // TODO display recording
            recorderThread = new RecorderThread(recognizerConnection, this);
            thread = new Thread(recorderThread);
            thread.start();
        }
    }

    public synchronized void stop() {
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

    public void matchFound(String response) {
        stop();
        String matchedString = parseResponse(response);
        // TODO handle match
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

    public boolean running() {
        return recorderThread != null;
    }

}
