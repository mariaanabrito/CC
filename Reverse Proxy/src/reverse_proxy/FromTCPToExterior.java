package reverse_proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe responsável por fazer a comunicação do servidor tcp para o cliente.
 */
public class FromTCPToExterior extends Thread {
    private final Socket tcp;
    private final Socket exterior;

    public FromTCPToExterior(Socket tcp, Socket exterior) {
       this.tcp = tcp;
       this.exterior = exterior;
    }
    
    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(tcp.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            OutputStreamWriter osr = new OutputStreamWriter(exterior.getOutputStream());
            PrintWriter pw = new PrintWriter(osr, true);

            String read;
            // Enquanto o socket do cliente estiver aberto ler e redirecionar para o mesmo a mensagem.
            while((read = br.readLine()) != null && !exterior.isClosed()) {
                pw.println(read);
            }
            // Terminar ligação.
            tcp.close();
        } catch(IOException e) {
        }
    }
}
