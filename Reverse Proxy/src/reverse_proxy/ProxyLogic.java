package reverse_proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe responsável por aceitar conexões de clientes.
 */
public class ProxyLogic extends Thread {
    Table table;
    
    public ProxyLogic(Table t) {
        table = t;
    }
    
    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(80);
            Socket s;
            
            // Ciclo responsável por aceitar conexões.
            while((s = ss.accept()) != null) {
                // Ao aceitar conexão de um novo cliente deve-se obter o melhor servidor TCP.
                Socket tcp = new Socket(table.getTCPServer(), 80);
                
                FromExteriorToTCP fettcp = new FromExteriorToTCP(s, tcp);
                fettcp.start();
                FromTCPToExterior ftcpte = new FromTCPToExterior(tcp, s);
                ftcpte.start();
            }
        } catch (IOException e) {
            System.err.println("An error ocurred at ProxyLogic.");
        }
    }
}
