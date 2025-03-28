package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    private final InetAddress serverAddress;
    private final int serverPort;
    private final DatagramSocket clientSocket;
    private String username;
    private Thread messageListener;
    private volatile boolean listening = false;

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

    public void startMessageListener() {
        listening = true;
        messageListener = new Thread(() -> {
            try {
                while (listening) {
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);
                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    if (message.startsWith("FROM:")) {
                        String[] parts = message.split(":", 3);
                        if (parts.length == 3) {
                            String sender = parts[1];
                            String content = parts[2];
                            System.out.println("\n[" + sender + "] " + content);
                            System.out.print("> ");
                        }
                    } else if (message.startsWith("USERS:")) {
                        String[] users = message.substring(6).split(",");
                        System.out.println("\nConnected users:");
                        for (String user : users) {
                            if (!user.isEmpty()) {
                                System.out.println("- " + user);
                            }
                        }
                        System.out.print("> ");
                    } else {
                        System.out.println(message);
                        System.out.print("> ");
                    }
                }
            } catch (IOException e) {
                if (listening) {
                    System.err.println("Error receiving message: " + e.getMessage());
                }
            }
        });
        messageListener.setDaemon(true);
        messageListener.start();
    }

    public void stopMessageListener() {
        listening = false;
        if (messageListener != null) {
            messageListener.interrupt();
        }
    }

    public void close() {
        stopMessageListener();
        clientSocket.close();
    }

    public void connect(String username) throws Exception {
        this.username = username;
        sendMessage("CONNECT:" + username);
        String response = receiveMessage();
        System.out.println(response);
    }

    public void listUsers() throws Exception {
        sendMessage("LIST_USERS");
    }

    public void sendPrivateMessage(String targetUser, String message) throws Exception {
        sendMessage("MSG:" + targetUser + ":" + message);
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 1234;
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Connecting to server " + serverAddress + ":" + port);
            Client client = new Client(serverAddress, port);

            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            client.connect(username);

            System.out.println("\nAvailable commands: list, msg, exit, help");

            client.startMessageListener();

            boolean running = true;
            while (running) {
                System.out.print("> ");
                String input = scanner.nextLine();

                if (input.equals("list")) {
                    client.listUsers();
                } else if (input.equals("help")) {
                    System.out.println("Available commands: list, msg, exit, help");
                } else if (input.equals("msg")) {
                    System.out.print("Username to message: ");
                    String target = scanner.nextLine();
                    System.out.print("Message: ");
                    String message = scanner.nextLine();
                    client.sendPrivateMessage(target, message);
                } else if (input.equals("exit")) {
                    client.sendMessage("EXIT");
                    running = false;
                } else {
                    System.out.println("Unknown command. Type 'help' for available commands.");
                }
            }

            client.close();
            scanner.close();
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}