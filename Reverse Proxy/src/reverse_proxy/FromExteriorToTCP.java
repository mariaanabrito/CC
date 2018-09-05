package reverse_proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe que é responsável por fazer a comunicação com o cliente para o servidor tcp.
 */
public class FromExteriorToTCP extends Thread {
    Socket exterior;
    Socket tcp;

    public FromExteriorToTCP(Socket exterior, Socket tcp) {
        this.exterior = exterior;
        this.tcp = tcp;
    }
    
    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(exterior.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            
            OutputStreamWriter osr = new OutputStreamWriter(tcp.getOutputStream());
            PrintWriter pw = new PrintWriter(osr, true);
            
            String read;
            // Enquanto o socket do cliente estiver aberto redireccionar para o servidor tcp.
            while((read = br.readLine()) != null && !exterior.isClosed()) {
                pw.println(read);
            }
            // Avisar o servidor tcp de que terminou a conexão atual.
            tcp.close();
        } catch(IOException e) {
        }
    }
    
}
