package reverse_proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente de teste que envia mensagens a um servidor TCP.
 */
public class TestClient {
    
    public  static void main(String args[]) {
        if(args.length == 2) {
            try {
                InetAddress server = InetAddress.getByName(args[0]);
                Socket s = new Socket(server, 80);

                InputStreamReader isr = new InputStreamReader(s.getInputStream());
                BufferedReader br = new BufferedReader(isr);

                OutputStreamWriter osr = new OutputStreamWriter(s.getOutputStream());
                PrintWriter pw = new PrintWriter(osr, true);

                String line = args[1];
                pw.println(line);
                pw.println("");
                while(!s.isClosed() && (line = br.readLine()) != null) {
                    if(line.equals("EOF")) {
                        break;
                    }
                    System.out.println(line);

                    // Envio resposta de confirmação.
                    pw.println("");
                }
            } catch(Exception e) {
                System.err.println("Invalid server address.");
            }
        } else {
            System.err.println("Wrong number of arguments.");
        }
    }
}
