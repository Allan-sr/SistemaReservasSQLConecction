package una.sistemareservas.proxy;

import una.sistemareservas.cliente.ClienteSocket;
import una.sistemareservas.contratos.ICategoriaService;
import una.sistemareservas.contratos.IReservaService;
import una.sistemareservas.contratos.IUsuarioService;

/**
 * Central de servicios del Frontend: guarda la única conexión al servidor
 * y entrega los proxies a quien los pida. Se arma una sola vez en el Main.
 */
public class Servicios {

    private static ClienteSocket cliente;
    private static IUsuarioService usuarios;
    private static IReservaService reservas;
    private static ICategoriaService categorias;

    private Servicios() {
    }

    public static void iniciar(ClienteSocket clienteSocket) {
        cliente = clienteSocket;
        usuarios = new UsuarioServiceProxy(clienteSocket);
        reservas = new ReservaServiceProxy(clienteSocket);
        categorias = new CategoriaServiceProxy(clienteSocket);
    }

    public static IUsuarioService usuarios() {
        return usuarios;
    }

    public static IReservaService reservas() {
        return reservas;
    }

    public static ICategoriaService categorias() {
        return categorias;
    }

    public static ClienteSocket getCliente() {
        return cliente;
    }
}