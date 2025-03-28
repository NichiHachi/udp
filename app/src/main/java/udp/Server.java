package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private int port;
    private DatagramSocket socket;
    private boolean running;
    private Map<String, ClientInfo> clients = new ConcurrentHashMap<>();

    private static class ClientInfo {
        InetAddress address;
        int port;
        String username;

        ClientInfo(InetAddress address, int port, String username) {
            this.address = address;
            this.port = port;
            this.username = username;
        }
    }

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {
            socket = new DatagramSocket(port);
            running = true;
            System.out.println("Server started on port " + port);
            processIncomingPackets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processIncomingPackets() {
        byte[] buffer = new byte[1024];

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                String clientId = clientAddress.getHostAddress() + ":" + clientPort;

                handleMessage(message, clientAddress, clientPort, clientId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleConnection(String message, InetAddress clientAddress, int clientPort, String clientId) throws IOException {
        String username = message.substring(8).trim();
        ClientInfo client = new ClientInfo(clientAddress, clientPort, username);
        clients.put(clientId, client);

        System.out.println("[" + clientId + "] Connected as " + username);
        sendResponse("Connection established", clientAddress, clientPort);
    }

    private void forwardMessage(String message, String senderId) throws IOException {
        String[] parts = message.split(":", 3);
        if (parts.length != 3) return;

        String targetUsername = parts[1];
        String content = parts[2];
        ClientInfo sender = clients.get(senderId);

        if ("all".equals(targetUsername)) {
            System.out.println("[" + sender.username + " -> all] " + content);
            for (ClientInfo client : clients.values()) {
                if (!client.username.equals(sender.username)) {
                    sendResponse("FROM:" + sender.username + ":" + content,
                            client.address, client.port);
                }
            }
            sendResponse("Message sent to all users", sender.address, sender.port);
            return;
        }

        for (ClientInfo client : clients.values()) {
            if (client.username.equals(targetUsername)) {
                System.out.println("[" + sender.username + " -> " + targetUsername + "] " + content);

                sendResponse("FROM:" + sender.username + ":" + content,
                        client.address, client.port);

                sendResponse("Message sent to " + targetUsername,
                        sender.address, sender.port);
                return;
            }
        }

        sendResponse("User not found: " + targetUsername, sender.address, sender.port);
    }

    private void sendUserList(InetAddress clientAddress, int clientPort) throws IOException {
        StringBuilder userList = new StringBuilder();

        for (ClientInfo client : clients.values()) {
            userList.append(client.username).append(",");
        }

        String clientId = clientAddress.getHostAddress() + ":" + clientPort;
        ClientInfo requestingClient = clients.get(clientId);
        String username = requestingClient != null ? requestingClient.username : "unknown";

        String response = "USERS:" + userList.toString();
        System.out.println("[" + username + "] list");

        sendResponse(response, clientAddress, clientPort);
    }

    private void handleMessage(String message, InetAddress clientAddress, int clientPort, String clientId) throws IOException {
        if (message.startsWith("CONNECT:")) {
            handleConnection(message, clientAddress, clientPort, clientId);
        } else if (message.equals("LIST_USERS")) {
            sendUserList(clientAddress, clientPort);
        } else if (message.startsWith("MSG:")) {
            forwardMessage(message, clientId);
        } else if (message.equals("EXIT")) {
            ClientInfo exitingClient = clients.get(clientId);
            if (exitingClient != null) {
                System.out.println("[" + exitingClient.username + "] exit");
                clients.remove(clientId);
            }
        } else {
            sendResponse("Unknown command", clientAddress, clientPort);
        }
    }

    private void sendResponse(String message, InetAddress address, int port) throws IOException {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }

    public void stop() {
        running = false;
        if (socket != null) {
            socket.close();
        }
    }

    public static void main(String[] args) {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 9876.");
            }
        }

        Server server = new Server(port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.stop();
        }));

        System.out.println("Starting UDP Server on port " + port);
        server.start();
    }
}