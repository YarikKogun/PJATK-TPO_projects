package zad1;

import javax.swing.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * Created by yaroslavkohun on 4/23/18.
 */
class Rcv extends Thread {

    private SocketChannel socketChannel = null;
    public boolean isRunning = true;
    private Client client = null;

    public Rcv(String string, SocketChannel socketChannel, Client client) {
        super(string);
        this.socketChannel = socketChannel;
        this.client = client;
        try {
            this.socketChannel.configureBlocking(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void run() {

        int countBytes = 0;
        ByteBuffer buf = ByteBuffer.allocate(2048);
        String msg = "";
        try {

            while (isRunning) {

                try {

                    while ((countBytes = socketChannel.read(buf)) > 0) {
                        buf.flip();
                        msg += StandardCharsets.UTF_8.decode(buf).toString();
                        buf.flip();
                    }
                } catch (IOException e) {
                    socketChannel.close();
                    isRunning = false;
                    JOptionPane.showMessageDialog(null, "SERVER ERROR!");
                }

                if (msg.startsWith("@LOG")) {
                    client.log = Boolean.valueOf(msg.substring(4));
                } else if (msg.startsWith("@MSG")) {
                    client.addToChat(msg.substring(4));
                } else if (msg.startsWith("@REG")){
                    client.reg = Boolean.valueOf(msg.substring(4));
                }

                msg = "";
                countBytes = 0;
                buf.clear();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}

