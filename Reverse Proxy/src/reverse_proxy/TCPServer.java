package reverse_proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe servidor TCP que recebe conexões e dedica a estas uma thread individual.
 */
public class TCPServer extends Thread {
    private Counter counter;
    
    TCPServer(Counter c) {
        counter = c;
    }
    
    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(80);
            Socket s;
            while((s=ss.accept()) != null) {
                // Ao receber uma nova conexão o contador é incrementado.
                counter.increment();
                TCPServerThread tcpst = new TCPServerThread(s, counter);
                tcpst.start();
            }
        } catch(IOException e) {
            System.err.println("An error ocurred at TCPServer");
        }
    }
}
