package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private int port;
    private DatagramSocket socket;
    private boolean running;

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        try {
            socket = new DatagramSocket(port);
            running = true;

            byte[] buffer = new byte[1024];

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                System.out.println("Message reçu de [" + clientAddress + ":" + clientPort + "] " + message);

                String response = "Réponse serveur : " + message;
                byte[] responseData = response.getBytes();

                DatagramPacket responsePacket = new DatagramPacket(
                        responseData, responseData.length, clientAddress, clientPort);

                socket.send(responsePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    public static void main(String[] args) {
        int port = 9876;

        System.out.println("Starting UDP server on port " + port);
        Server server = new Server(port);
        server.run();
    }
}