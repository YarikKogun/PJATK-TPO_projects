package zad1;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class Chat extends JFrame implements Runnable {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JTextArea chat;
    private JTextArea message;
    private Destination destination;
    Session session;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        EventQueue.invokeLater(() -> {
            try {
                String nickname = "defaultNick";
                if (args.length != 0)
                    nickname = args[0];

                Chat client = new Chat(nickname);
                client.setVisible(true);

                Thread thread = new Thread(client);
                thread.start();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                exit(1);
            }
        });
    }

    public Chat(String nickname) throws NamingException, JMSException {
        super("JMS Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 284, 462);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        setLocationRelativeTo(null);

        chat = new JTextArea();
        chat.setEditable(false);
        chat.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        chat.setLineWrap(true);
        contentPane.add(chat);

        JScrollPane scrollChat = new JScrollPane(chat);
        scrollChat.setBounds(12, 12, 250, 350);
        contentPane.add(scrollChat);

        message = new JTextArea();
        message.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        message.setLineWrap(true);
        contentPane.add(message);

        JScrollPane scrollMessage = new JScrollPane(message);
        scrollMessage.setBounds(12, 370, 180, 50);
        contentPane.add(scrollMessage);

        JButton button = new JButton("send");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {

                MessageProducer sender;
                try {
                    if (message.getText().length() == 0)
                        throw new Exception("enter message!");

                    sender = session.createProducer(destination);

                    TextMessage message = session.createTextMessage(nickname + ": " + Chat.this.message.getText());
                    sender.send(message);

                    Chat.this.message.setText("");

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }

            }
        });
        button.setBounds(200, 370, 60, 50);
        contentPane.add(button);

    }

    @Override
    public void run() {

        try {
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
            properties.put(Context.PROVIDER_URL, "tcp://localhost:3035/");

            Context context = new InitialContext(properties);

            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");

            Connection connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = (Destination) context.lookup("topic1");

            MessageConsumer receiver = session.createConsumer(destination);
            connection.start();

            TextMessage message;

            while ((message = (TextMessage) receiver.receive()) != null) {
                chat.append(message.getText() + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Oops! Something do wrong-> " + e.getMessage());
            exit(1);
        }
    }

    private static void exit(int i) {
        System.exit(i);
    }
}