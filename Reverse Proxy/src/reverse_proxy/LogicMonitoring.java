package reverse_proxy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Classe responsável por monitorizar todos os monitores UDP.
 * Esta toma ações quando recebe um pacote de qualquer monitor UDP.
 */
public class LogicMonitoring extends Thread { 
    private Table table;
    
    public LogicMonitoring(Table t) {
        table = t;
    }
    
    @Override
    public void run() {   
            int my_port = 5555;
            
            byte[] receiveData;
            DatagramPacket receivePacket;
            try {
                DatagramSocket ds = new DatagramSocket(my_port);
                String message;
                String fields[];
                
                LogicMonitoringThread lmt = new LogicMonitoringThread(ds, table);
                lmt.start();

                while(true) {
                    receiveData = new byte[1024];
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    ds.receive(receivePacket);
                    message = new String(receivePacket.getData());
                    fields = message.split(" ");
                    String s;
                    switch (fields[0]) {
                        // Quando recebe uma mensagem init deve adicionar uma nova entrada na tabela.
                        case "init":
                            Information i = new Information(receivePacket.getAddress(), receivePacket.getPort(), Integer.parseInt(fields[1].trim()));
                            table.put(receivePacket.getAddress(), i);
                            System.out.println(message);
                            break;
                        // Como se trata de uma resposta a pedido de probing deve utilizar o método apropriado de Table.
                        case "reply":
                            table.receivedPacket(receivePacket.getAddress(), Integer.parseInt(fields[1].trim()), Integer.parseInt(fields[2].trim()));
                            break;
                        // Como se trata de probing periódico do monitor UDP apenas apresenta o número de conexões TCP.
                        case "automatic":
                            table.receivedPacket(receivePacket.getAddress(), Integer.parseInt(fields[1].trim()));
                            break;
                        default:
                            break;
                    }
                }
        } catch(IOException | NumberFormatException e) {
            System.out.println("An error ocurred at LogicMonitoring.");
        }
    }
}