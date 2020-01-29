import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
  public static final int PORT = 1234;
  static ArrayList<Socket> sockets;

  public static void main(String[] args) {
    ServerSocket server;
    System.out.println("Empezando servidor...");
    try {
      server = new ServerSocket(PORT);
      sockets = new ArrayList<Socket>();
      int sessionID = 0;
      while (true) {
        Socket socket;
        socket = server.accept();
        System.out.println("Nueva conexion entrante: " + socket);
        ((Server) new Server(socket, sessionID)).start();
        sessionID++;
        sockets.add(socket);
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}

class Server extends Thread {
  Socket socket;
  DataOutputStream output;
  DataInputStream input;
  int sessionID;

  Server(Socket socket, int id) {
    this.socket = socket;
    this.sessionID = id;
  }

  public void disconnect() {
    try {
      socket.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  @Override
  public void run() {
    String action = "";
    try {
      while (action != "bye") {
        input = new DataInputStream(socket.getInputStream());
        action = input.readUTF();
        System.out.println("El cliente con session [" + sessionID + "] dice " + action);
        for (Socket sock : ChatServer.sockets) {
          DataOutputStream output = new DataOutputStream(sock.getOutputStream());
          output.writeUTF("Cliente [" + sessionID + "]: " + action);
        }
      }
    } catch (Exception e) {
      ChatServer.sockets.remove(socket);
    }
    disconnect();
  }
}
