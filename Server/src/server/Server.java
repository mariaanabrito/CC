package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server { 
            
     public static void main(String[] args)
    {
         try {
            InetAddress addr = InetAddress.getByName("10.1.1.1");
            
            Monitors monitors = new Monitors();
            
            ProbingSender ps = new ProbingSender(monitors);
            ps.start();
            
            byte [] receiveData;
            DatagramPacket receivePacket;
            DatagramSocket ds = new DatagramSocket(5555);

            while(true)
            {
                // Recebemos resposta de probing
                receiveData = new byte[1024];
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                ds.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                
                // Calculamos tempo de chegada do pacote
                LocalTime l = LocalTime.now();
                
                String[] components = sentence.split(" ");
                InetAddress udp, tcp;
                int numtcp;
                
                // Faz-se parse da mensagem PDU
                udp = InetAddress.getByName(components[0]);
                tcp = InetAddress.getByName(components[1]);
                numtcp = Integer.parseInt(components[2]);
                
                if(monitors.containsTCP(tcp) == true)
                {
                    // Se existir, faz-se update
                    monitors.update(tcp, numtcp, l.getNano());
                }
                else
                {
                    /* Se não existir, adiciona-se e os parâmetros ficam a zero, pois considera-se
                    que não lhe foi enviado nenhum pedido de probing
                    */
                    InfoMonitor info = new InfoMonitor(udp, 0, 0, 0, 0, 0, 0);
                    monitors.add(tcp, info);
                }
                
            }
         } catch (IOException ex) {
             Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
         }
         
         

    }
        
}
