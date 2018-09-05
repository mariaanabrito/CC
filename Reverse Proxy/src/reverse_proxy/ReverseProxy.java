package reverse_proxy;

/**
 * Esta classe inicializa o servidor principal da Reverse Proxy.
 * 
 * @author Admin
 */
public class ReverseProxy {
    
    public static void main(String args[]) {
        // A tabela é um objeto partilhado sendo enviado como parâmetro para a
        // lógica de monitorização e a lógica de proxy.
        Table table = new Table();
        LogicMonitoring lm = new LogicMonitoring(table);
        lm.start();
        ProxyLogic pl = new ProxyLogic(table);
        pl.start();
    }
}
