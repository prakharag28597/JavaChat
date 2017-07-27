
package server;
import javax.swing.JFrame;

/**
 *
 * @author prakharag
 */
public class ServerTest {
    public static void main(String args[]){
        Server my_local_server=new Server();
        my_local_server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        my_local_server.runServer();
    }
}
