package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    private final InetAddress serverAddress;
    private final int serverPort;
    private final DatagramSocket clientSocket;

    public Client(String serverIp, int serverPort) throws Exception {
        this.serverAddress = InetAddress.getByName(serverIp);
        this.serverPort = serverPort;
        this.clientSocket = new DatagramSocket();
    }

    public void sendMessage(String message) throws Exception {
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
        clientSocket.send(sendPacket);
    }

    public String receiveMessage() throws Exception {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }

    public void close() {
        clientSocket.close();
    }

    public void connect() throws Exception {
        sendMessage("hello serveur RX302");
        System.out.println("Serveur RX302 ready :  " + receiveMessage());
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 9876;

        try {
            System.out.println("Starting client connecting to " + serverAddress + ":" + port);
            Client client = new Client(serverAddress, port);

            client.connect();

            String message = "Hello from client";
            System.out.println("Sending: " + message);
            client.sendMessage(message);

            String response = client.receiveMessage();
            System.out.println("Received: " + response);

            client.close();
            System.out.println("Client closed");

        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
