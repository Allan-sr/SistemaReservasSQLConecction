package una.sistemareservas.servidor;

import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.modelo.Usuario;
import una.sistemareservas.negocio.*;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ServidorSocket {

    // Candado: evita que dos hilos reserven el mismo recurso a la vez
    private static final Object CANDADO_RESERVAS = new Object();

    private final int puerto;

    public ServidorSocket(int puerto) {
        this.puerto = puerto;
    }

    public void iniciar() throws IOException {

        try (ServerSocket servidor = new ServerSocket(puerto)) {

            System.out.println("Servidor iniciado en el puerto " + puerto);

            while (true) {

                Socket cliente = servidor.accept();

                Thread hilo = new Thread(() -> atenderCliente(cliente));
                hilo.start();
            }
        }
    }

    private void atenderCliente(Socket cliente) {

        try (
                Socket conexion = cliente;
                ObjectOutputStream salida =
                        new ObjectOutputStream(conexion.getOutputStream());
                ObjectInputStream entrada =
                        new ObjectInputStream(conexion.getInputStream())
        ) {

            salida.flush();

            System.out.println(
                    "Cliente conectado: " + conexion.getInetAddress()
            );

            while (!conexion.isClosed()) {

                Object objeto = entrada.readObject();

                if (!(objeto instanceof Peticion)) {
                    salida.writeObject(
                            new Respuesta(false, null, "Petición inválida")
                    );
                    salida.flush();
                    continue;
                }

                Peticion peticion = (Peticion) objeto;

                System.out.println(
                        "Operación recibida: " + peticion.getOperacion()
                );

                Respuesta respuesta = procesarPeticion(peticion);

                salida.writeObject(respuesta);
                salida.flush();
                salida.reset();
            }

        } catch (EOFException e) {
            System.out.println("Cliente desconectado.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error con cliente: " + e.getMessage());
        }
    }


    private Respuesta procesarPeticion(Peticion peticion) {

        try {

            if (Operaciones.PING.equals(peticion.getOperacion())) {

                return new Respuesta(
                        true,
                        "PONG",
                        "Comunicación exitosa"
                );
            }

            if (Operaciones.CATEGORIA_LISTAR.equals(peticion.getOperacion())) {

                CategoriaService servicio = new CategoriaService();

                return new Respuesta(
                        true,
                        servicio.listar(),
                        "Categorías obtenidas correctamente"
                );
            }

            if (Operaciones.USUARIO_AUTENTICAR.equals(peticion.getOperacion())) {

                // Las casillas de la comanda, en el orden acordado: id, clave
                Object[] datos = (Object[]) peticion.getDatos();
                String id = (String) datos[0];
                String clave = (String) datos[1];

                UsuarioService servicio = new UsuarioService();
                Usuario usuario = servicio.autenticar(id, clave);

                // exito = true significa "se pudo procesar", aunque la clave sea mala
                return new Respuesta(
                        true,
                        usuario,
                        usuario != null
                                ? "Autenticación exitosa"
                                : "Usuario o contraseña incorrectos"
                );
            }

            if (Operaciones.RESERVA_CREAR.equals(peticion.getOperacion())) {

                Object[] datos = (Object[]) peticion.getDatos();
                String funcionarioId = (String) datos[0];
                String actividad = (String) datos[1];
                LocalDate fecha = (LocalDate) datos[2];
                LocalTime horaInicio = (LocalTime) datos[3];
                LocalTime horaFin = (LocalTime) datos[4];
                @SuppressWarnings("unchecked")
                List<String> categoriaIds = (List<String>) datos[5];

                ResultadoReserva resultado;
                synchronized (CANDADO_RESERVAS) {
                    resultado = new ReservaService().crear(
                            funcionarioId, actividad, fecha, horaInicio, horaFin, categoriaIds);
                }

                return new Respuesta(true, resultado, "Reserva procesada");
            }

            if (Operaciones.RESERVA_CANCELAR.equals(peticion.getOperacion())) {

                Object[] datos = (Object[]) peticion.getDatos();
                String funcionarioId = (String) datos[0];
                String id = (String) datos[1];

                ResultadoReserva resultado;
                synchronized (CANDADO_RESERVAS) {
                    resultado = new ReservaService().cancelar(funcionarioId, id);
                }

                return new Respuesta(true, resultado, "Cancelación procesada");
            }

            if (Operaciones.RESERVA_LISTAR_MIAS.equals(peticion.getOperacion())) {

                Object[] datos = (Object[]) peticion.getDatos();
                String funcionarioId = (String) datos[0];

                List<Reserva> reservas = new ReservaService().listarMisReservas(funcionarioId);

                return new Respuesta(true, reservas, "Reservas obtenidas");
            }

            if (Operaciones.RESERVA_LISTAR.equals(peticion.getOperacion())) {

                List<Reserva> reservas = new ReservaService().listar();

                return new Respuesta(true, reservas, "Reservas obtenidas");
            }

            if (Operaciones.RESERVA_EXTRAER_IA.equals(peticion.getOperacion())) {

                Object[] datos = (Object[]) peticion.getDatos();
                String frase = (String) datos[0];

                ReservaExtraidaDTO dto = new ReservaService().extraerDatosReserva(frase);

                return new Respuesta(true, dto, "Datos extraídos correctamente");
            }

            return new Respuesta(
                    false,
                    null,
                    "Operación no reconocida: " + peticion.getOperacion()
            );

        } catch (Exception e) {

            return new Respuesta(
                    false,
                    null,
                    "Error al procesar la petición: " + e.getMessage()
            );
        }
    }

}