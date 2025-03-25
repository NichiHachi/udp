package udp;

public class ClientMain {
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