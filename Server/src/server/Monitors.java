package cc;

import java.net.InetAddress;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;


public class Monitors {
    
    private Map<InetAddress,ReentrantLock> locks; // Controlo da zona partilhada
    private Map<InetAddress, InfoMonitor> monitors; // Key - IP do servidor TCP; Value - Estrutura InfoMonitor
    
    public Monitors()
    {
        locks = new TreeMap<>();
        monitors = new TreeMap<>();
    }
    
    public boolean containsTCP(InetAddress tcp)
    { // Verifica se o end. TCP existe
        boolean res;
        try{
            for(InetAddress addr: monitors.keySet())
                locks.get(addr).lock();
        
            res = monitors.containsKey(tcp);
        }
        finally{
            for(InetAddress addr: monitors.keySet())
                locks.get(addr).unlock();
        }
        return res;
    }
    
    public InfoMonitor getInfoMonitor(InetAddress tcp)
    { 
        InfoMonitor res;
        
        locks.get(tcp).lock();
        try {
            res = monitors.get(tcp);
        } finally {
            locks.get(tcp).unlock();
        }
        
        return res;
    }
    
    public Map<InetAddress, InfoMonitor> getMonitors()
    { // Retorna map de monitores
        Map<InetAddress, InfoMonitor> res;
        
        try{
            for(InetAddress addr: monitors.keySet())
                locks.get(addr).lock();
            
            res = monitors;
        }
        finally{
            for(InetAddress addr: monitors.keySet())
                locks.get(addr).unlock();
        }
        
        return res;
    }
   
    public void add(InetAddress tcp, InfoMonitor info)
    { // Adiciona-se end. TCP e info do monitor
        try{
            for(InetAddress addr: monitors.keySet())
                locks.get(addr).lock();
        
            monitors.put(tcp, info);
        }
        finally{
            for(InetAddress addr: monitors.keySet())
                locks.get(addr).unlock();
        }
    }
    
    public void update(InetAddress tcp, int numtcp, int time_arrived)
    { // Faz-se update da info do end. TCP passado como parâmetro
        locks.get(tcp).lock();
        try {
            monitors.get(tcp).setNum_tcp(numtcp);
            monitors.get(tcp).setTime_arrived(time_arrived);
        } finally {
            locks.get(tcp).unlock();
        }
    }
        
}
