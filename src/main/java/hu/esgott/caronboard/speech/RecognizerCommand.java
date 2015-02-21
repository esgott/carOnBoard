package hu.esgott.caronboard.speech;

import java.nio.ByteBuffer;

public class RecognizerCommand {

    public interface ResponseCallback {
        void call(String response);
    }

    private String command;
    private String parameters;
    private ByteBuffer binaryData;
    private boolean waitForResponse;
    private ResponseCallback callback;

    public RecognizerCommand(ServerCommand serverCommand, String parameters,
            boolean waitForResponse) {
        command = serverCommand.getCommand();
        this.parameters = " " + parameters;
        this.waitForResponse = waitForResponse;
    }

    public RecognizerCommand(ServerCommand serverCommand,
            ByteBuffer binaryData, boolean waitForResponse) {
        this(serverCommand, "", waitForResponse);
        binaryData.flip();
        this.binaryData = binaryData;
    }

    public void setCallback(ResponseCallback callback) {
        this.callback = callback;
    }

    public boolean binary() {
        return binaryData != null;
    }

    public String getCommand() {
        return command;
    }

    public String getCommandWithParameters() {
        return command + parameters;
    }

    public ByteBuffer getBinaryData() {
        return binaryData;
    }

    public boolean waitForResponse() {
        return waitForResponse;
    }

    public void call(String response) {
        if (callback != null) {
            callback.call(response);
        }
    }

}
