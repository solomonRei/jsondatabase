package client.core;

import com.google.gson.JsonParser;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/** Client core class. */
public class ClientCore implements AutoCloseable {
  private Socket socket;
  private DataInputStream input;
  private DataOutputStream output;
  private final String address;
  private final int port;

  public ClientCore(final String address, final int port) {
    this.address = address;
    this.port = port;
  }

  /**
   * Connects to the server.
   *
   * @throws IOException if an I/O error occurs
   */
  public void connect() throws IOException {
    this.socket = new Socket(InetAddress.getByName(address), port);
    this.input = new DataInputStream(socket.getInputStream());
    this.output = new DataOutputStream(socket.getOutputStream());
  }

  /**
   * Sends a request to the server and prints the response.
   *
   * @param requestMessage the request message to send
   * @throws IOException if an I/O error occurs
   */
  public void sendRequest(final String requestMessage) throws IOException {
    if (socket == null || output == null || input == null) {
      connect();
    }

    System.out.println("Client started!");
    output.writeUTF(requestMessage);
    System.out.println("Sent: " + requestMessage);
    String response = input.readUTF();
    System.out.println("Received: " + new JsonParser().parse(response).toString());
  }

  /**
   * Closes the connection to the server.
   *
   * @throws IOException if an I/O error occurs
   */
  @Override
  public void close() throws IOException {
    try {
      if (output != null) {
        output.close();
      }
      if (input != null) {
        input.close();
      }
      if (socket != null && !socket.isClosed()) {
        socket.close();
      }
    } finally {
      socket = null;
      input = null;
      output = null;
    }
  }
}
