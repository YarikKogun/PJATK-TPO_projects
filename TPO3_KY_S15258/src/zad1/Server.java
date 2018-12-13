/**
 * @author Kohun Yaroslav S15258
 */

package zad1;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server extends Thread {
    private static String IP = "localhost";
    private static int PORT = 8080;

    private HashMap<String, String> usersData = new HashMap<>();
    private HashMap<SocketChannel, String> channelsData = new HashMap<>();

    public static void main(String[] args) {
        Server server = new Server("Server's Thread");
        server.start();
    }

    public Server(String string) {
        super(string);
    }

    public void run() {
        Selector selector = null;
        ServerSocketChannel serverSocketChannel = null;

        try {
            {
                BufferedReader bufferedReader = new BufferedReader(new FileReader("usersLoginData.txt"));
                String user;

                while ((user = bufferedReader.readLine()) != null) {
                    String[] userData = user.split("\\s");
                    usersData.put(userData[0], userData[1]);
                }
                bufferedReader.close();
            }

            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress socketAddress = new InetSocketAddress(IP, PORT);
            System.out.println(socketAddress.getAddress());
            serverSocketChannel.bind(socketAddress);

            serverSocketChannel.configureBlocking(false);

            int ops = serverSocketChannel.validOps();
            @SuppressWarnings("unused")
            SelectionKey selectKy = serverSocketChannel.register(selector, ops, null);

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {

                selector.select();
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = selectionKeySet.iterator();

                while (selectionKeyIterator.hasNext()) {

                    SelectionKey key = selectionKeyIterator.next();

                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        channelsData.put(socketChannel, "NULL");

                    } else if (key.isReadable()) {

                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

                        try {
                            socketChannel.read(byteBuffer);
                        } catch (IOException ex) {
                            socketChannel.close();
                            if (channelsData.containsKey(socketChannel)) {
                                if (!channelsData.get(socketChannel).equals("NULL")) {
                                    Date dateNow = new Date();
                                    SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss| ");
                                    sendToAll("@MSG" + formatForDateNow.format(dateNow) + channelsData.get(socketChannel) + " quit the chat", channelsData.get(socketChannel));
                                }
                                channelsData.remove(socketChannel);
                            }
                        }

                        String message = new String(byteBuffer.array()).trim();

                        if (message.startsWith("@LOG")) {
                            boolean login = searchUser(message.substring(4));
                            send("@LOG" + login, socketChannel);

                            if (login) {
                                channelsData.replace(socketChannel, "NULL", message.substring(4, message.indexOf(' ')));
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                }
                                Date dateNow = new Date();
                                SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss| ");
                                sendToAll("@MSG" + formatForDateNow.format(dateNow) + message.substring(4, message.indexOf(' ')) + " joined the chat",
                                        channelsData.get(socketChannel));
                            }

                        } else if (message.startsWith("@MSG")) {
                            Date dateNow = new Date();
                            SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss| ");
                            sendToAll("@MSG" + formatForDateNow.format(dateNow) + channelsData.get(socketChannel) + ": " + message.substring(4), "");

                        } else if (message.startsWith("@OFF")) {
                            Date dateNow = new Date();
                            SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm:ss| ");
                            sendToAll("@MSG" + formatForDateNow.format(dateNow) + channelsData.get(socketChannel) + " quit the chat.", channelsData.get(socketChannel));
                            channelsData.replace(socketChannel, channelsData.get(socketChannel), "NULL");
                        } else if (message.startsWith("@REG")) {
                            String writerStr = message.substring(4);
                            String[] regData = writerStr.split("\\s");

                            if (!usersData.containsKey(regData[0])) {
                                usersData.put(regData[0], regData[1]);
                                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("usersLoginData.txt", true));
                                bufferedWriter.append("\n" + writerStr);
                                bufferedWriter.flush();
                                bufferedWriter.close();
                                send("@REG" + true, socketChannel);
                            } else {
                                send("@REG" + false, socketChannel);
                            }

                        }
                    }
                    selectionKeyIterator.remove();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean searchUser(String user) {
        if (user.length() == 0 || user.indexOf(' ') == -1)
            return false;

        String[] userData = user.split("\\s");
        String userName = userData[0];
        String userPass = userData[1];

        if (channelsData.containsValue(userName))
            return false;

        for (Map.Entry<String, String> entry : usersData.entrySet()) {
            if (entry.getKey().equals(userName) && entry.getValue().equals(userPass))
                return true;
        }
        return false;
    }
    /*
    private static void send(String string, SocketChannel socketChannel) throws IOException {
        byte[] message = new String(string).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        socketChannel.write(buffer);
    }*/
    private void send(String data, SocketChannel socketChannel) throws IOException {
        if (data.length() > 5) {

            if (data.startsWith("@MSG") && data.getBytes().length > 2043) {
                data = data.substring(4);
                int i = 0;
                int bytes = 2043;
                while (data.getBytes().length > bytes) {

                    String s = "@MSG";
                    while (s.getBytes().length <= 2043) {
                        s += data.charAt(i);
                        i++;
                    }
                    bytes += s.getBytes().length;
                    byte[] message = s.getBytes();
                    ByteBuffer buf = ByteBuffer.wrap(message);
                    socketChannel.write(buf);
                }
                byte[] message = ("@MSG" + data.substring(i)).getBytes();
                ByteBuffer buf = ByteBuffer.wrap(message);
                socketChannel.write(buf);
            } else {

                byte[] message = new String(data).getBytes();
                ByteBuffer buf = ByteBuffer.wrap(message);
                socketChannel.write(buf);
            }
        }
    }
    private void sendToAll(String msg, String uname) {
        channelsData.forEach((x, y) -> {
            if (!y.equals("NULL") && !y.equals(uname))
                try {
                    send(msg + "\n", x);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
    }
}
