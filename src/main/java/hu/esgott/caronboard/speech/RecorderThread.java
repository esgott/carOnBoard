package hu.esgott.caronboard.speech;

import hu.esgott.caronboard.CommandQueue;
import hu.esgott.caronboard.CommandQueue.RecorderCommand;
import hu.esgott.caronboard.speech.RecognizerCommand.ResponseCallback;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class RecorderThread implements Runnable {

    private final Logger log = Logger.getLogger(getClass().getName());

    private static final float SAMPLE_RATE = 8000;
    private static final int BUFFER_SIZE = 1024;
    private static final int QUERY_FREQUENCY = 4;
    private TargetDataLine inputLine;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private RecognizerServerConnection recognizerConnection;
    private boolean stopped = false;
    private int leftUntilQuery = QUERY_FREQUENCY;
    private final CommandQueue queue = CommandQueue.getInstance();
    private final MatchHandler matchHandler = new MatchHandler();

    public RecorderThread(RecognizerServerConnection recognizerConnection) {
        this.recognizerConnection = recognizerConnection;
    }

    @Override
    public void run() {
        log.info("Recording started");
        initServer();
        openInput();

        while (!stopped) {
            recordToBuffer();
            sendRecordedData();
            sendIfQueryExpired();
        }

        closeInput();
        log.info("Recording finished");
    }

    private void initServer() {
        RecognizerCommand initCommand = new RecognizerCommand(
                ServerCommand.INIT, "", true);
        recognizerConnection.send(initCommand);
    }

    private void openInput() {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        DataLine.Info inputInfo = new DataLine.Info(TargetDataLine.class,
                format);
        try {
            inputLine = (TargetDataLine) AudioSystem.getLine(inputInfo);
            inputLine.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        inputLine.start();
    }

    private void closeInput() {
        inputLine.stop();
        inputLine.close();
    }

    private void recordToBuffer() {
        inputLine.read(buffer, 0, buffer.length);
    }

    private void sendRecordedData() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        byteBuffer.put(buffer);
        RecognizerCommand command = new RecognizerCommand(ServerCommand.WAVEIN,
                byteBuffer, false);
        recognizerConnection.send(command);
    }

    private void sendIfQueryExpired() {
        if (leftUntilQuery <= 0) {
            sendQuery();
            leftUntilQuery = QUERY_FREQUENCY;
        }
        leftUntilQuery--;
    }

    private void sendQuery() {
        RecognizerCommand command = new RecognizerCommand(ServerCommand.QUERY,
                "", true);
        command.setCallback(new ResponseCallback() {
            @Override
            public void call(String response) {
                if (response.contains("vit_end=1")) {
                    stop();
                    recognizerConnection.emptyQueue();
                    RecognizerCommand traceBackCommand = new RecognizerCommand(
                            ServerCommand.TRACEBACK, "", true);
                    traceBackCommand.setCallback(new ResponseCallback() {
                        @Override
                        public void call(String response) {
                            queue.notifyRecorder(RecorderCommand.STOP_RECORDING);
                            matchHandler.handleMatch(response);
                        }
                    });
                    recognizerConnection.send(traceBackCommand);
                }
            }
        });
        recognizerConnection.send(command);
    }

    public void stop() {
        stopped = true;
    }

}
