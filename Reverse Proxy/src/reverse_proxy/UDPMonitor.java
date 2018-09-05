package reverse_proxy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Classe responsável por monitorizar um servidor TCP. 
 */
public class UDPMonitor {
    
    public static void main(String args[]) {
        
        int udp_port = 5555;
        int server_port = 5555;
        int tcp_port = 80;
        try {
            // Deve receber como parâmetro o endereço do servidor.
            InetAddress server_ip = InetAddress.getByName(args[0]);
        
            DatagramSocket ds = new DatagramSocket(udp_port);
            
            Counter counter = new Counter(0);
        
            DatagramPacket receivePacket;
            byte [] receiveData;
            DatagramPacket sendPacket;
            byte [] sendData;
            String message;
            
            // Lança o servidor.
            TCPServer tcps = new TCPServer(counter);
            tcps.start();
            
            // O PDU inicial é apenas uma mensagem a indicar que se trata de uma inicialização mais a porta do servidor TCP.
            message = "init " + tcp_port;
            sendData = new byte[1024];
            sendData = message.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, server_ip, server_port);
            ds.send(sendPacket);
            
            // Lança a thread de probing periódico.
            AutomaticThread at = new AutomaticThread(ds, server_ip, server_port, counter);
            at.start();
            
            String receivedSequence;
            
            while(true) {
                receiveData = new byte[1024];
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                ds.receive(receivePacket);
                
                receivedSequence = new String(receivePacket.getData());
                message = "reply " + counter.get() + " " + receivedSequence;
                 
                sendData = new byte[1024];
                sendData = message.getBytes();
                
                sendPacket = new DatagramPacket(sendData, sendData.length, server_ip, server_port);
                ds.send(sendPacket);
            }
        } catch (IOException e) {
            System.err.println("An error ocurred at UDPMonitor.");
        }
    }
}
