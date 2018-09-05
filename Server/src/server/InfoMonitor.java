package cc;

import java.net.InetAddress;

public class InfoMonitor {
    
    private InetAddress udp;
    private int num_tcp ;
    private int time_sent, time_arrived, num_packet;
    private float sum_rtt;
    private int packet_loss;
    
    public InfoMonitor()
    {
        num_tcp = 0;
        time_sent = 0;
        time_arrived = 0;
        num_packet = 0;
        sum_rtt = 0;
        packet_loss = 0;
    }
    
    public InfoMonitor(InetAddress udp, int numtcp, int ts, int ta, int np, float sum, int pl)
    {
        this.udp = udp;
        num_tcp = numtcp;
        time_sent = ts;
        time_arrived = ta;
        num_packet = np;
        sum_rtt = sum;
        packet_loss = pl;
    }

    public int getNum_tcp() {
        return num_tcp;
    }

    public void setNum_tcp(int num_tcp) {
        this.num_tcp = num_tcp;
    }

    public int getTime_sent() {
        return time_sent;
    }

    public void setTime_sent(int time_sent) {
        this.time_sent = time_sent;
    }

    public int getTime_arrived() {
        return time_arrived;
    }

    public void setTime_arrived(int time_arrived) {
        
        // Calcula RTT
        
        this.time_arrived = time_arrived;
        num_packet++;
        sum_rtt += time_arrived - time_sent;
    }

    public int getNum_packet() {
        return num_packet;
    }

    public void setNum_packet(int num_packet) {
        this.num_packet = num_packet;
    }

    public float getRtt() {
        return sum_rtt/num_packet;
    }

    public InetAddress getUdp() {
        return udp;
    }

    public void setUdp_ip(InetAddress udp_ip) {
        udp = udp_ip;
    }

    public float getSum_rtt() {
        return sum_rtt;
    }

    public void setSum_rtt(float sum_rtt) {
        this.sum_rtt = sum_rtt;
    }


    public int getPacket_loss() {
        return packet_loss;
    }

    public void setPacket_loss(int packet_loss) {
        this.packet_loss = packet_loss;
    }
    
    
    
}
