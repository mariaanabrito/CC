package reverse_proxy;

import java.net.InetAddress;
import java.time.LocalTime;

/**
 * Classe responsável por armazenar toda a informação do estado de um servidor TCP.
 */
public class Information {
    private final int timeout = 4;              // tempo considerador timeout em segundos
    private final InetAddress udp_ip;           // endereço do monitor UDP que coincide com o do servidor TCP
    private final int udp_port;                 // porta do monitor UDP
    private final int tcp_port;                 // porta do servidor TCP
    private int number_tcp;                     // número de conexões TCP
    private float sum_rtt;                      // soma total de todos os rtt de cada pedido-resposta
    private LocalTime time_sent, time_arrived;  // hora de pacote enviado, hora de pacote recebido
    private int sent_packets, received_packets; // número de pacotes enviados, número de pacotes recebidos
    private int last_received;                  // número de sequência de último pacote recebido
    
    public Information(InetAddress udp_ip, int udp_port, int tcp_port) {
        this.udp_ip = udp_ip;
        this.udp_port = udp_port;
        this.tcp_port = tcp_port;
        number_tcp = 0;
        time_sent = LocalTime.now();
        time_arrived = time_sent.plusSeconds(timeout);
        sent_packets = 0;
        sum_rtt = 0;
        received_packets = 0;
        last_received = 0;
    }
    
    /**
     * @return número de conexões TCP 
     */
    public int getNumberTCP() {
        return number_tcp;
    }
    
    /**
     * Sempre que um pacote for recebido toda a informação será atualizada através deste método.
     * 
     * @param sequence_number número de sequência do pacote
     * @param number_tcp      número de conexões TCP
     */
    public void receivedPacket(int sequence_number, int number_tcp) {
        // Tempo de chegada do pacote.
        LocalTime now = LocalTime.now();
        // Se o número de sequência do pacote coincidir com o enviado
        // é verificado se o pacote chegou antes do timeout.
        // Em caso afirmativo, o tempo de chegada é atualizado e é incrementado o número de pacotes recebidos.
        if(sequence_number == sent_packets) {
            if(now.isBefore(time_arrived)) {
                setTimeArrived();
                received_packets++;
            }
        }
        // É necessário verificar se é posterior ao último pacote recebido.
        if(sequence_number >= last_received) {
            last_received = sequence_number;
            // Em caso afirmativo então a informação sobre o número de conexões TCP é a mais atual.
            setNumberTCP(number_tcp);
            
        }
        // Prints sobre o estado atual da tabela para efeitos de debugging.
        System.out.println("address: " + udp_ip);
        System.out.println("sent at: " + time_sent + " | received at: " + time_arrived);
        System.out.println("sent packets: " + sent_packets + " | received packets:" + received_packets);
        System.out.println("last sequence received: " + last_received + " | number of tcp: " + number_tcp);
        System.out.println("evaluation: " + getEvaluation() + " | rtt: " + getRoundTripTime()/100000 + " ms\n");    
    }
    
    /**
     * Quando um pacote é recebido por um probing periódico então apenas se atualiza o número de TCP.
     * 
     * @param number_tcp 
     */
    public void receivedPacket(int number_tcp) {
        setNumberTCP(number_tcp);
    }
    
    /**
     * Sempre que um pacote é enviado, o número de pacotes enviados é incrementado
     * e o tempo de envio atualizado.
     */
    public void sentPacket() {
        sent_packets++;
        setTimeSent();
    }
    
    /**
     * Método privado que atualiza o número de TCP.
     * 
     * @param n 
     */
    private void setNumberTCP(int n) {
        this.number_tcp = n;
    }
    
    /**
     * Método privado que atualiza o tempo de envio.    
     */
    private void setTimeSent() {
        time_sent = LocalTime.now();
        // O tempo máximo de chegada permitido é antes de se atingir o timeout.
        time_arrived = time_sent.plusSeconds(timeout);
    }
    
    /**
     * Atualizar o tempo de chegada de um pacote que não atingiu o timeout.
     */
    private void setTimeArrived() {
        time_arrived = LocalTime.now();
        // Sempre que um pacote chega, a soma dos rtt deve ser atualizada.
        sum_rtt += (time_arrived.getNano() - time_sent.getNano());
    }
    
    /**
     * O RTT é uma média e portanto é a soma dos rtt de cada pedido-resposta a dividir
     * pelo número total de pacotes recebidos.
     * 
     * @return rtt
     */
    public float getRoundTripTime() {
        return sum_rtt / received_packets;
    }
    
    /**
     * 
     * @return porta do monitor UDP 
     */
    public int getUDP_Port() {
        return udp_port;
    }
    
    /**
     * 
     * @return porta do servidor TCP 
     */
    public int getTCP_Port() {
        return tcp_port;
    }
    
    /**
     * 
     * @return rácio de pacotes perdidos entre 0 e 1. 
     */
    public float getRatioPacketLoss() {
        return 1 - (received_packets / sent_packets);
    }
    
    /**
     * 
     * @return avaliação da qualidade do servidor TCP. 
     */
    public float getEvaluation() {
        return (float) (getRatioPacketLoss() * 100 + getRoundTripTime() / 10000 + getNumberTCP()); 
    }
    
    /**
     * 
     * @return último pacote recebido 
     */
    int getLastSentPacket() {
        return sent_packets;
    }
    
    /**
     * 
     * @return endereço do monitor UDP e do servidor TCP 
     */
    InetAddress getAddress() {
        return udp_ip;
    }
}
