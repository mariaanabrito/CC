package reverse_proxy;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe responsável por armazenar toda a informação de todos os monitores UDP.
 * Está preparada para ser utilizada num contexto multithreaded.
 */
public class Table {
    
    private final ReentrantLock l;
    private Map<InetAddress, ReentrantLock> rl;
    private Map<InetAddress, Information> table;
    
    public Table() {
        l = new ReentrantLock();
        rl = new HashMap<>();
        table = new HashMap<>();
    }
    
    /**
     * 
     * @return uma lista com todas as informações de todos os monitores UDP 
     */
    public List<Information> getInformations() {
        List<Information> r = new LinkedList();
        l.lock();
        try {
            r.addAll(table.values());
        } finally {
            l.unlock();
        }
        return r;
    }
    
    /**
     * Obtém a informação sobre um monitor UDP específico.
     * 
     * @param udp_ip monitor UDP
     * @return       informação sobre o mesmo
     */
    public Information getInformation(InetAddress udp_ip) { 
        Information res;
        
        rl.get(udp_ip).lock();
        try {
            res = table.get(udp_ip);
        } finally {
            rl.get(udp_ip).unlock();
        }
        return res;
    }
    
    /**
     * Adiciona um novo monitor UDP à tabela.
     * 
     * @param udp_ip endereço do monitor UDP
     * @param i      objeto Information correspondente
     */
    public void put(InetAddress udp_ip, Information i) { 
        try {
            l.lock();
            ReentrantLock lock = new ReentrantLock();
            rl.put(udp_ip, lock);
            table.put(udp_ip, i);
        } finally {
            l.unlock();
        }
    }
    
    /**
     * Método que trata de atualizar a informação na tabela aquando a chegada de um pacote.
     * 
     * @param udp_ip
     * @param number_tcp
     * @param sequence_number 
     */
    public void receivedPacket(InetAddress udp_ip, int number_tcp, int sequence_number) {
        rl.get(udp_ip).lock();
        try {
            table.get(udp_ip).receivedPacket(sequence_number, number_tcp);
        } finally {
            rl.get(udp_ip).unlock();
        }
    }
    
    /**
     * Método que trata de atualizar a informação da tabela aquando a chegada
     * de uma mensagem periódica por parte do monitor UDP.
     * 
     * @param udp_ip
     * @param number_tcp 
     */
    public void receivedPacket(InetAddress udp_ip, int number_tcp) {
        rl.get(udp_ip).lock();
        try {
            table.get(udp_ip).receivedPacket(number_tcp);
        } finally {
            rl.get(udp_ip).unlock();
        }
    }
    
    /**
     * Sempre que um pacote é enviado, este método deve ser chamado.
     * Responsável por atualizar o tempo de envio de um pacote.
     * 
     * @param udp_ip 
     */
    public void sentPacket(InetAddress udp_ip) {
        rl.get(udp_ip).lock();
        try {
            table.get(udp_ip).sentPacket();
        } finally {
            rl.get(udp_ip).unlock();
        }
    }
    
    /**
     * 
     * @return todos os endereços de todos os monitores UDP 
     */
    List<InetAddress> getAddresses() {
        List<InetAddress> list = new LinkedList();
        l.lock();
        try {
            list.addAll(table.keySet());
        } finally {
            l.unlock();
        }
        return list;
    }
    
    /**
     * Para um monitor UDP obtém o último pacote enviado.
     * 
     * @param ip
     * @return 
     */
    int getLastPacketSent(InetAddress ip) {
        int r;
        rl.get(ip).lock();
        try {
            r = table.get(ip).getLastSentPacket();
        } finally {
            rl.get(ip).unlock();
        }
        return r;
    }
    
    /**
     * Obtém a porta de um monitor UDP.
     * 
     * @param ip
     * @return 
     */
    int getUDP_Port(InetAddress ip) {
        int r;
        rl.get(ip).lock();
        try {
            r = table.get(ip).getUDP_Port();
        } finally {
            rl.get(ip).unlock();
        }
        return r;
    }
    
    /**
     * Método responsável por determinar o melhor servidor TCP.
     * 
     * @return o melhor servidor TCP
     */
    public InetAddress getTCPServer() {
        InetAddress r = null;
        float pontuation = 999999;
        l.lock();
        try {
            // Percorre uma vez todos os servidores TCP.
            for(InetAddress address : table.keySet()) {
                rl.get(address).lock();
                try {
                    if(table.get(address).getEvaluation() < pontuation) {
                        pontuation = table.get(address).getEvaluation();
                        r = table.get(address).getAddress();
                    }
                } finally {
                    rl.get(address).unlock();
                }
            }
        } finally {
            l.unlock();
        }
        return r;
    }
}
