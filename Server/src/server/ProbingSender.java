package cc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    Thread que manda pedidos de probing periódicos. É criada no Server
*/

public class ProbingSender extends Thread{
    
    private Monitors monitors;
    
    public ProbingSender(Monitors m)
    {
        monitors = m;
    }
    
    public void run()
    {
        String message = "probing"; // Não interessa conteúdo desta mensagem
        DatagramPacket sendPacket;
        byte[] sendData;
        DatagramSocket ds;
        
        while(true)
        {
            for(InfoMonitor info: monitors.getMonitors().values())
            {
                try {
                    ds = new DatagramSocket(5555, info.getUdp()); //Envia-se probing a um monitor específico
                    sendData = new byte[1024];
                    sendData = message.getBytes();
                    
                    sendPacket = new DatagramPacket(sendData, sendData.length, info.getUdp(), 5555);
                    ds.send(sendPacket);
                    
                    LocalTime l = LocalTime.now(); // Tempo de envio de pacote

                    info.setTime_sent(l.getNano()); // Guarda-se tempo (em nanossegundos) do envio
               
                } catch (SocketException ex) {
                    Logger.getLogger(ProbingSender.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ProbingSender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            try {
                sleep(5000); // Manda-se probing de 5 em 5 segundos
            } catch (InterruptedException ex) {
                Logger.getLogger(ProbingSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    }
    
}
