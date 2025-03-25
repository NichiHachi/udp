package udp;

public class Main {
    public static void main(String[] args) {
        final int port = 9876;
        Thread serverThread = new Thread(() -> {
            System.out.println("Starting UDP server on port " + port);
            Server server = new Server(port);
            server.run();
        });
        serverThread.setDaemon(true);
        serverThread.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Starting client...");
            Client client = new Client("localhost", port);

            client.connect();

            client.sendMessage("Hello from client");
            System.out.println(client.receiveMessage());

            client.close();
            System.out.println("Client test completed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}