package reverse_proxy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/**
 * Classe responsável por fazer probing request periódico a cada um dos servidores UDP.
 */
public class LogicMonitoringThread extends Thread {
    private Table t;
    DatagramSocket ds;
    
    LogicMonitoringThread(DatagramSocket ds, Table t) {
        this.ds = ds;
        this.t = t;
    }
    
    @Override
    public void run() {
        String message;
        DatagramPacket sendPacket;
        byte[] sendData;
        
        while(true) {
            try {
                List<InetAddress> l = t.getAddresses();

                for(InetAddress address: l) {
                    // Para cada monitor enviará o número de sequência do pacote.
                    int toSend = t.getLastPacketSent(address) + 1; 
                    message = String.valueOf(toSend);
                    sendData = new byte[1024];
                    sendData = message.getBytes();
                    sendPacket = new DatagramPacket(sendData, sendData.length, address, t.getUDP_Port(address));
                    t.sentPacket(address);
                    ds.send(sendPacket);
                }
                Thread.sleep(2000); // Manda-se probing a cada 2 segundos.
            } catch(IOException | InterruptedException e) {
                System.err.println("An error ocurred at LogicMonitoringThread.");
            }
        }
    }
}
