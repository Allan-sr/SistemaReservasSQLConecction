package una.sistemareservas.proxy;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.contratos.IUsuarioService;

/**
 * Central de servicios del Frontend: guarda la única conexión al servidor
 * y entrega los proxies a quien los pida. Se arma una sola vez en el Main.
 */
public class Servicios {
    private static ClienteSocket cliente;
    private static IUsuarioService usuarios;

    private Servicios(){

    }

    public static void iniciar(ClienteSocket clienteSocket){
        cliente=clienteSocket;
        usuarios = new UsuarioServiceProxy(clienteSocket);
    }

    public static IUsuarioService usuarios(){
        return usuarios;
    }

    public static ClienteSocket getCliente(){
        return cliente;
    }
}
