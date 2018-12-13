/**
 *
 *  @author Kohun Yaroslav S15258
 *
 */

package zad1;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

public class Client extends Thread {

    private static String IP = "localhost";
    private static int PORT = 8080;

    private JPanel contentPane;
    private SocketChannel socketChannel;
    public boolean log = false;
    public boolean reg = false;
    private JTextArea chat = null;
    private Client client = null;

    public static void main(String[] args) {
        Client client = new Client("Client's Thread");
        client.start();
    }

    Client(String name) {
        super(name);
        client = this;
    }

    public void run() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            JOptionPane.showMessageDialog(null, "SERVER ERROR! code:7 \n" + e);
        }

        try {
            Connect();
        } catch (InterruptedException | IOException e) {
            JOptionPane.showMessageDialog(null, "SERVER ERROR! code:1");
        }
        Login();
    }


    private void Login() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {

                    JFrame frame = new JFrame("Login to chat");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setBounds(100, 100, 280, 380);
                    contentPane = new JPanel();
                    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
                    frame.setContentPane(contentPane);
                    contentPane.setLayout(null);

                    JTextArea username = new JTextArea();
                    username.setFont(new Font("Monospaced", Font.PLAIN, 20));
                    username.setTabSize(0);

                    username.addKeyListener(new KeyAdapter() {
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                                if (e.getModifiers() > 0) {
                                    username.transferFocusBackward();
                                } else {
                                    username.transferFocus();
                                }
                                e.consume();
                            }
                        }
                    });

                    contentPane.add(username);

                    JPasswordField password = new JPasswordField();
                    password.setEchoChar('*');
                    password.setFont(new Font("Monospaced", Font.PLAIN, 20));
                    contentPane.add(password);

                    JScrollPane sc1 = new JScrollPane(username);
                    sc1.setBounds(50, 50, 180, 40);
                    contentPane.add(sc1);

                    JScrollPane sc2 = new JScrollPane(password);
                    sc2.setBounds(50, 130, 180, 40);
                    contentPane.add(sc2);

                    JLabel usernameLabel = new JLabel("Username:");
                    usernameLabel.setBounds(50, 34, 70, 16);
                    contentPane.add(usernameLabel);

                    JLabel passwordLabel = new JLabel("Password:");
                    passwordLabel.setBounds(50, 114, 70, 16);
                    contentPane.add(passwordLabel);

                    JButton login = new JButton("Login");
                    login.setBounds(85, 210, 100, 30);
                    contentPane.add(login);

                    JLabel newUsernameLabel = new JLabel("Create new account?");
                    newUsernameLabel.setBounds(72, 240, 150, 16);
                    contentPane.add(newUsernameLabel);

                    JButton register = new JButton("Register");
                    register.setBounds(85, 256, 100, 30);
                    contentPane.add(register);

                    JCheckBox show = new JCheckBox("Show password");
                    show.setBounds(50, 175, 180, 25);
                    show.addItemListener(new ItemListener() {

                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED)
                                password.setEchoChar((char) 0);
                            else
                                password.setEchoChar('*');
                        }
                    });
                    contentPane.add(show);

                    login.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            try {
                                if (socketChannel.isConnected()) {
                                    send("@LOG" + username.getText() + " " + String.valueOf(password.getPassword()));
                                    Rcv thread = new Rcv("@LOG", socketChannel, client);
                                    thread.start();
                                    Thread.sleep(200);
                                    if (log) {
                                        frame.dispose();
                                        Chat();
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Invalid login/password or already logged in!");
                                    }
                                    thread.isRunning = false;
                                    thread.interrupt();
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "SERVER ERROR!");
                                try {
                                    Connect();
                                } catch (InterruptedException | IOException e1) {
                                }
                            }
                        }
                    });

                    register.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.dispose();
                            Register();
                        }
                    });

                    JLabel user1 = new JLabel("   Login: user1, Password: user1");
                    user1.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
                    user1.setBounds(30, 300, 250, 20);
                    contentPane.add(user1);

                    JLabel user2 = new JLabel("   Login: user2, Password: user2");
                    user2.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
                    user2.setBounds(30, 320, 250, 20);
                    contentPane.add(user2);

                    frame.setLocationRelativeTo(null);
                    frame.setResizable(false);
                    frame.setVisible(true);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
        });
    }

    private void Register() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {

                    JFrame frame = new JFrame("Registration");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setBounds(100, 100, 280, 270);
                    contentPane = new JPanel();
                    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
                    frame.setContentPane(contentPane);
                    contentPane.setLayout(null);

                    JTextArea username = new JTextArea();
                    username.setFont(new Font("Monospaced", Font.PLAIN, 20));
                    username.setTabSize(0);

                    username.addKeyListener(new KeyAdapter() {
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                                if (e.getModifiers() > 0) {
                                    username.transferFocusBackward();
                                } else {
                                    username.transferFocus();
                                }
                                e.consume();
                            }
                        }
                    });

                    contentPane.add(username);

                    JPasswordField password = new JPasswordField();
                    password.setEchoChar('*');
                    password.setFont(new Font("Monospaced", Font.PLAIN, 20));
                    contentPane.add(password);

                    JScrollPane sc1 = new JScrollPane(username);
                    sc1.setBounds(50, 50, 180, 40);
                    contentPane.add(sc1);

                    JScrollPane sc2 = new JScrollPane(password);
                    sc2.setBounds(50, 130, 180, 40);
                    contentPane.add(sc2);

                    JLabel usernameLabel = new JLabel("Username:");
                    usernameLabel.setBounds(50, 34, 70, 16);
                    contentPane.add(usernameLabel);

                    JLabel passwordLabel = new JLabel("Password:");
                    passwordLabel.setBounds(50, 114, 70, 16);
                    contentPane.add(passwordLabel);

                    JButton register = new JButton("Register");
                    register.setBounds(43, 210, 100, 30);
                    contentPane.add(register);

                    JButton toLogin = new JButton("Login...");
                    toLogin.setBounds(138, 210, 100, 30);
                    contentPane.add(toLogin);

                    JCheckBox show = new JCheckBox("Show password");
                    show.setBounds(50, 175, 180, 25);
                    show.addItemListener(new ItemListener() {

                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED)
                                password.setEchoChar((char) 0);
                            else
                                password.setEchoChar('*');
                        }
                    });
                    contentPane.add(show);

                    toLogin.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.dispose();
                            Login();
                        }
                    });

                    register.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {

                            try {
                                if (socketChannel.isConnected()) {
                                    send("@REG" + username.getText() + " " + String.valueOf(password.getPassword()));
                                    Rcv thread = new Rcv("@REG", socketChannel, client);
                                    thread.start();
                                    Thread.sleep(200);
                                    thread.isRunning = false;
                                    thread.interrupt();
                                    if (reg) {
                                        frame.dispose();
                                        Login();
                                        JOptionPane.showMessageDialog(null, "New account has been created successfully.");
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Account is already exist or wrong data!");
                                    }
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "SERVER ERROR! code:2");
                                try {
                                    Connect();
                                } catch (InterruptedException | IOException e1) {
                                }
                            }

                        }
                    });

                    frame.setLocationRelativeTo(null);
                    frame.setResizable(false);
                    frame.setVisible(true);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "SERVER ERROR! code:6 \n" + e.getMessage());
                }
            }
        });
    }

    private void Chat() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {

                    JFrame frame = new JFrame("Chat");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setBounds(100, 100, 300, 460);
                    contentPane = new JPanel();
                    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
                    frame.setContentPane(contentPane);
                    contentPane.setLayout(null);

                    chat = new JTextArea();
                    chat.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    chat.setLineWrap(true);
                    chat.setEditable(false);
                    contentPane.add(chat);

                    JTextArea message = new JTextArea();
                    message.setFont(new Font("Monospaced", Font.PLAIN, 12));

                    message.setLineWrap(true);

                    Rcv thread = new Rcv("@MSG", socketChannel, client);
                    thread.start();

                    JButton send = new JButton("Send");
                    send.setBounds(187, 330, 93, 35);
                    send.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            try {
                                send("@MSG" + message.getText());
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(null, "SERVER ERROR! code:5 \n" + e.getMessage());
                            }
                            message.setText("");
                        }
                    });
                    contentPane.add(send);

                    JButton clearButton = new JButton("Clear");
                    clearButton.setBounds(187, 365, 93, 35);
                    clearButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            message.setText("");
                        }
                    });
                    contentPane.add(clearButton);

                    message.addKeyListener(new KeyListener() {

                        @Override
                        public void keyPressed(KeyEvent arg0) {
                        }

                        @Override
                        public void keyReleased(KeyEvent arg0) {
                            if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                                if (arg0.isShiftDown()) {
                                    message.append("\n");
                                } else {
                                    try {
                                        send("@MSG" + message.getText());
                                    } catch (IOException e) {
                                        JOptionPane.showMessageDialog(null, "SERVER ERROR! code:4 \n" + e.getMessage());
                                    }
                                    message.setText("");
                                }
                            }
                        }

                        @Override
                        public void keyTyped(KeyEvent arg0) {
                        }

                    });

                    JScrollPane scrollPane = new JScrollPane(message);
                    scrollPane.setBounds(12, 330, 168, 70);
                    contentPane.add(scrollPane);

                    JScrollPane scrollPane1 = new JScrollPane(chat);
                    scrollPane1.setBounds(12, 20, 268, 300);
                    contentPane.add(scrollPane1);

                    JMenuBar jmb = new JMenuBar();

                    JMenu prop = new JMenu("Service");

                    JMenuItem clear = new JMenuItem("Clear chat");
                    clear.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            chat.setText("");
                        }
                    });

                    JMenuItem logoff = new JMenuItem("Log out");
                    logoff.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            try {
                                send("@OFF00");
                                Thread.sleep(200);
                                log = false;
                                thread.isRunning = false;
                                thread.interrupt();
                            } catch (IOException | InterruptedException e) {
                                System.out.println(e);
                            }
                            frame.dispose();
                            Login();
                        }
                    });

                    prop.add(clear);
                    prop.add(logoff);
                    jmb.add(prop);
                    frame.setJMenuBar(jmb);

                    frame.setVisible(true);
                    frame.setLocationRelativeTo(null);
                    frame.setResizable(false);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "SERVER ERROR! code:3 \n" + e.getMessage());
                }
            }
        });
    }

    private void Connect() throws InterruptedException, IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(IP, PORT);
        socketChannel = SocketChannel.open(inetSocketAddress);

    }
/*
    private void send(String s) throws IOException {
        if (s.length() > 5) {
            byte[] message = new String(s).getBytes();
            ByteBuffer buf = ByteBuffer.wrap(message);
            socketChannel.write(buf);
        }
    }
*/
    private void send(String data) throws IOException {
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
    public void addToChat(String msg) {
        if (chat != null) {
            chat.append(msg);
            chat.setCaretPosition(chat.getDocument().getLength());
        }
    }
}
