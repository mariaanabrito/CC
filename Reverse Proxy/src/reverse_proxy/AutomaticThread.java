package reverse_proxy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * AutomaticThread é responsável por enviar probing periódico à lógica de monitorização
 * sobre o estado do seu número de conexões tcp.
 */
public class AutomaticThread extends Thread{
    
    private final DatagramSocket ds;     // DatagramSocket criado em UDPMonitor
    private final InetAddress server_ip; // Endereço ip do servidor
    private final int server_port;       // Porta do servidor, por questões de generalização este recebe-a com parâmetro  
    private final Counter counter;       // Contador com o número de conexões tcp
    
    public AutomaticThread(DatagramSocket ds, InetAddress server_ip, int server_port, Counter counter) {
        this.ds = ds;
        this.server_ip = server_ip;
        this.server_port = server_port;
        this.counter = counter;
    }
    
    @Override
    public void run() {
        try  {
            byte [] sendData;
            DatagramPacket sendPacket;
            String message;
            while(true) {
                // O PDU é da forma <tipo> <estado do contador>
                // Desta forma, LogicMonitoring sabe que tipo de mensagem acabou de receber
                // assim como o estado atual desse servidor tcp ao nível de conexões
                message = "automatic " + counter.get();
                
                sendData = new byte[1024];
                sendData = message.getBytes();
                
                sendPacket = new DatagramPacket(sendData, sendData.length, server_ip, server_port);
                ds.send(sendPacket);
                // O probing será efetuado de segundo em segundo.
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error ocurred at AutomaticThread.");
        }
    }
    
}
