
package udp;

public class App {

    public static void main(String[] args) {
        try {
            Client client = new Client("127.0.0.1", 5000);
            client.connect();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
