/**
 *
 *  @author Kohun Yaroslav S15258
 *
 */

package zad1;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        Server server1 = new Server("Server1");
        Client client1 = new Client("Client1");
        Client client2 = new Client("Client2");

        server1.start();
        Thread.sleep(200);
        client1.start();
        client2.start();
    }
}
