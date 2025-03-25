package udp;

public class ServerMain {
    public static void main(String[] args) {
        int port = 9876;

        System.out.println("Starting UDP server on port " + port);
        Server server = new Server(port);
        server.run();
    }
}