package reverse_proxy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe responsável por tratar de um pedido individual com um cliente.
 */
public class TCPServerThread extends Thread {
    private final Socket socket;
    private final Counter counter;
    
    TCPServerThread(Socket s, Counter c) {
        socket = s;
        counter = c;
    }
    
    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            OutputStreamWriter osr = new OutputStreamWriter(socket.getOutputStream());
            PrintWriter pw = new PrintWriter(osr, true);
            
            try {
                FileReader fr = new FileReader(br.readLine());
                BufferedReader brf = new BufferedReader(fr);
                for(String read; (read = brf.readLine()) != null && br.readLine() != null;) {
                    pw.println(read);
                }
                pw.println("EOF");
            }catch(FileNotFoundException e) {
                pw.println("File not found.");
                pw.println("EOF");
            } finally {
                socket.close();
                // Ao terminar a conexão com o cliente, o contador é decrementado.
                counter.decrement();
            }
        } catch(IOException e) {
        }
    }
}
