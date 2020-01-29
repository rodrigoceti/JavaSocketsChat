import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient {
  static final int MAX_THREADS = 10;
  static final String HOST = "172.20.10.3";
  static JFrame frame;
  static JPanel panel;
  static JScrollPane scrollPane;
  static JTextArea chatBox = new JTextArea();
  static JTextField messageBox;
  static Client client;
  static Action action;

  public static void main(String[] args) {
    client = new Client();
    client.start();
    action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        client.sendMessage(messageBox.getText());
        messageBox.setText("");
      }
    };
    initUI();
  }

  public static void initUI() {
    frame = new JFrame("Cliente sockets");
    frame.setSize(new Dimension(500, 500));
    panel = new JPanel(new BorderLayout());
    messageBox = new JTextField();
    scrollPane = new JScrollPane(chatBox);
    chatBox.setEditable(false);
    messageBox.setBackground(Color.LIGHT_GRAY);
    messageBox.addActionListener(action);
    panel.add(scrollPane);
    panel.add(messageBox, BorderLayout.SOUTH);
    frame.add(panel);
    frame.setVisible(true);
  }
}

class Client extends Thread {
  protected Socket socket;
  protected DataOutputStream output;
  protected DataInputStream input;
  private int id;

  Client() {
    try {
      socket = new Socket(ChatClient.HOST, 1234);
      output = new DataOutputStream(socket.getOutputStream());
      output.writeUTF("Connected...");
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public void sendMessage(String msg) {
    try {
      output.writeUTF(msg);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  @Override
  public void run() {
    try {
      while (true) {
        input = new DataInputStream(socket.getInputStream());
        String res = input.readUTF();
        System.out.println(" Servidor devuelve saludo: " + res);
        ChatClient.chatBox.append("\n" + res);
        ChatClient.chatBox.setCaretPosition(ChatClient.chatBox.getText().length());
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}