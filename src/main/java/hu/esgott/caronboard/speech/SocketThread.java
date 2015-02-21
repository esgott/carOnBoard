package hu.esgott.caronboard.speech;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SocketThread implements Runnable {

    private final Logger log = Logger.getLogger(getClass().getName());

    private BlockingQueue<RecognizerCommand> queue = new LinkedBlockingQueue<>();
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean running = true;

    public SocketThread() {
    }

    public void sendCommand(RecognizerCommand command) {
        try {
            queue.put(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            connect();
            log.info("Socket connected");
            while (running) {
                sendNextCommand();
            }
            log.info("Socket thread stopped");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    log.info("Closing socket");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void connect() throws IOException {
        socket = new Socket("152.66.246.33", 2605);
        socket.setSoTimeout(5000);
        inputStream = new BufferedInputStream(socket.getInputStream());
        outputStream = new BufferedOutputStream(socket.getOutputStream());
    }

    public void sendNextCommand() throws IOException, InterruptedException {
        RecognizerCommand command = queue.poll(500, TimeUnit.MILLISECONDS);
        if (command != null) {
            if (command.binary()) {
                sendTextData(command.getCommand());
                sendBinaryData(command.getBinaryData());
            } else {
                sendTextData(command.getCommandWithParameters());
            }
            if (command.waitForResponse()) {
                receive(command);
            }
        }
    }

    private void sendTextData(String text) throws IOException {
        byte[] data = text.getBytes();
        sendSize(data.length);
        outputStream.write(data);
        outputStream.flush();
        log.fine(text + " command sent");
    }

    private void sendSize(int length) throws IOException {
        ByteBuffer lengthBytes = packLength(length);
        for (int i = 0; i < 4; i++) {
            outputStream.write(lengthBytes.get(i));
        }
    }

    private ByteBuffer packLength(int length) {
        ByteBuffer lengthBytes = ByteBuffer.allocate(4);
        lengthBytes.order(ByteOrder.LITTLE_ENDIAN);
        lengthBytes.putInt(length);
        return lengthBytes;
    }

    private void sendBinaryData(ByteBuffer buffer) throws IOException {
        sendSize(buffer.limit() / 2);
        for (int i = 0; i < buffer.limit(); i++) {
            outputStream.write(buffer.get(i));
        }
        outputStream.flush();
        log.fine(buffer.limit() + " binary data sent ");
    }

    private void receive(RecognizerCommand command) throws IOException {
        int length = receiveSize();
        log.fine("receiving " + length + " bytes of data");
        String responseString = receiveResponse(length);
        log.fine(responseString + " received");
        command.call(responseString);
    }

    private int receiveSize() throws IOException {
        ByteBuffer size = ByteBuffer.allocate(4);
        size.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 4; i++) {
            byte nextByte = (byte) inputStream.read();
            if (nextByte >= 0) {
                size.put(nextByte);
            } else {
                log.warning("read returned " + nextByte);
                stop();
                return 0;
            }
        }
        return size.getInt(0);
    }

    private String receiveResponse(int size) throws IOException {
        byte[] response = new byte[size];
        int read = 0;
        while (running && read == 0) {
            try {
                read = inputStream.read(response);
            } catch (SocketTimeoutException e) {
                log.warning("receive timed out");
            }
        }
        return new String(response);
    }

    public void emptyQueue() {
        queue.clear();
    }

    public void stop() {
        running = false;
    }

}
