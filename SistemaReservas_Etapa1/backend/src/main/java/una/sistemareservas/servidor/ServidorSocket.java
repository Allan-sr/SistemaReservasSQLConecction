package una.sistemareservas.servidor;

import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.modelo.Usuario;
import una.sistemareservas.negocio.*;
import una.sistemareservas.protocolo.Operaciones;
import una.sistemareservas.protocolo.Peticion;
import una.sistemareservas.protocolo.Respuesta;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.negocio.CalendarioActividadesService;

import una.sistemareservas.negocio.CalendarioRecursosService;
import una.sistemareservas.modelo.Recurso;

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


            if (Operaciones.CATEGORIA_BUSCAR.equals(peticion.getOperacion())) {

                String descripcion = (String) peticion.getDatos();

                CategoriaService servicio = new CategoriaService();

                return new Respuesta(
                        true,
                        servicio.buscar(descripcion),
                        "Búsqueda realizada correctamente"
                );
            }

            if (Operaciones.CATEGORIA_AGREGAR.equals(peticion.getOperacion())) {

                String descripcion = (String) peticion.getDatos();

                CategoriaService servicio = new CategoriaService();

                String error = servicio.agregar(descripcion);

                return new Respuesta(
                        true,
                        error,
                        error == null
                                ? "Categoría agregada correctamente"
                                : error
                );
            }

            if (Operaciones.CATEGORIA_MODIFICAR.equals(peticion.getOperacion())) {

                String[] datos = (String[]) peticion.getDatos();

                CategoriaService servicio = new CategoriaService();

                String error = servicio.modificar(datos[0], datos[1]);

                return new Respuesta(
                        true,
                        error,
                        error == null
                                ? "Categoría modificada correctamente"
                                : error
                );
            }

            if (Operaciones.CATEGORIA_ELIMINAR.equals(peticion.getOperacion())) {

                String id = (String) peticion.getDatos();

                CategoriaService servicio = new CategoriaService();

                String error = servicio.eliminar(id);

                return new Respuesta(
                        true,
                        error,
                        error == null
                                ? "Categoría eliminada correctamente"
                                : error
                );
            }


            if (Operaciones.FUNCIONARIO_LISTAR.equals(peticion.getOperacion())) {

                FuncionarioService servicio = new FuncionarioService();

                return new Respuesta(true, servicio.listar(),
                        "Funcionarios obtenidos correctamente");
            }

            if (Operaciones.FUNCIONARIO_BUSCAR.equals(peticion.getOperacion())) {

                String[] datos = (String[]) peticion.getDatos();

                FuncionarioService servicio = new FuncionarioService();

                return new Respuesta(true,
                        servicio.buscar(datos[0], datos[1]),
                        "Búsqueda realizada correctamente");
            }

            if (Operaciones.FUNCIONARIO_BUSCAR_ID.equals(peticion.getOperacion())) {

                String id = (String) peticion.getDatos();

                FuncionarioService servicio = new FuncionarioService();

                return new Respuesta(true, servicio.buscarPorId(id),
                        "Consulta realizada correctamente");
            }

            if (Operaciones.FUNCIONARIO_AGREGAR.equals(peticion.getOperacion())) {

                String[] datos = (String[]) peticion.getDatos();

                FuncionarioService servicio = new FuncionarioService();

                String error = servicio.agregar(datos[0], datos[1], datos[2]);

                return new Respuesta(true, error,
                        error == null ? "Funcionario agregado" : error);
            }

            if (Operaciones.FUNCIONARIO_MODIFICAR.equals(peticion.getOperacion())) {

                String[] datos = (String[]) peticion.getDatos();

                FuncionarioService servicio = new FuncionarioService();

                String error = servicio.modificar(datos[0], datos[1], datos[2]);

                return new Respuesta(true, error,
                        error == null ? "Funcionario modificado" : error);
            }

            if (Operaciones.FUNCIONARIO_ELIMINAR.equals(peticion.getOperacion())) {

                String id = (String) peticion.getDatos();

                FuncionarioService servicio = new FuncionarioService();

                String error = servicio.eliminar(id);

                return new Respuesta(true, error,
                        error == null ? "Funcionario eliminado" : error);
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

            if (Operaciones.USUARIO_CAMBIAR_CLAVE.equals(peticion.getOperacion())) {

                Object[] datos = (Object[]) peticion.getDatos();
                String id = (String) datos[0];
                String claveActual = (String) datos[1];
                String nuevaClave = (String) datos[2];

                boolean cambiada = new UsuarioService().cambiarClave(id, claveActual, nuevaClave);

                return new Respuesta(true, cambiada, "Cambio de clave procesado");
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

                if (resultado.esExitosa()) {
                    Reserva r = resultado.getReserva();

                    String nombre = r.getFuncionario() != null
                            ? r.getFuncionario().getNombre()
                            : r.getFuncionarioId();

                    GestorNotificaciones.getInstancia().notificarATodosMenos(
                            funcionarioId,
                            new una.sistemareservas.negocio.NotificacionReserva(
                                    r.getId(),
                                    r.getActividad(),
                                    r.getFuncionarioId(),
                                    nombre,
                                    r.getFecha().toString(),
                                    r.getHoraInicio() + " - " + r.getHoraFin()
                            )
                    );
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


            if (Operaciones.RECURSO_LISTAR.equals(peticion.getOperacion())) {
                return new Respuesta(true,
                        new RecursoService().listar(),
                        "Recursos obtenidos");
            }

            if (Operaciones.RECURSO_LISTAR_CATEGORIAS.equals(peticion.getOperacion())) {
                return new Respuesta(true,
                        new RecursoService().listarCategorias(),
                        "Categorías obtenidas");
            }

            if (Operaciones.RECURSO_BUSCAR.equals(peticion.getOperacion())) {
                String[] datos = (String[]) peticion.getDatos();

                return new Respuesta(true,
                        new RecursoService().buscar(datos[0], datos[1]),
                        "Búsqueda realizada");
            }

            if (Operaciones.RECURSO_BUSCAR_ID.equals(peticion.getOperacion())) {
                String id = (String) peticion.getDatos();

                return new Respuesta(true,
                        new RecursoService().buscarPorId(id),
                        "Consulta realizada");
            }

            if (Operaciones.RECURSO_AGREGAR.equals(peticion.getOperacion())) {
                String[] datos = (String[]) peticion.getDatos();

                String error = new RecursoService()
                        .agregar(datos[0], datos[1], datos[2]);

                return new Respuesta(true, error,
                        error == null ? "Recurso agregado" : error);
            }

            if (Operaciones.RECURSO_MODIFICAR.equals(peticion.getOperacion())) {
                String[] datos = (String[]) peticion.getDatos();

                String error = new RecursoService()
                        .modificar(datos[0], datos[1], datos[2]);

                return new Respuesta(true, error,
                        error == null ? "Recurso modificado" : error);
            }

            if (Operaciones.RECURSO_ELIMINAR.equals(peticion.getOperacion())) {
                String id = (String) peticion.getDatos();

                String error = new RecursoService().eliminar(id);

                return new Respuesta(true, error,
                        error == null ? "Recurso eliminado" : error);
            }


            if (Operaciones.CALENDARIO_RECURSOS_CATEGORIAS
                    .equals(peticion.getOperacion())) {

                CalendarioRecursosService servicio =
                        new CalendarioRecursosService();

                return new Respuesta(
                        true,
                        servicio.listarCategorias(),
                        "Categorías obtenidas correctamente"
                );
            }

            if (Operaciones.CALENDARIO_RECURSOS_LISTAR
                    .equals(peticion.getOperacion())) {

                String categoriaId = (String) peticion.getDatos();

                CalendarioRecursosService servicio =
                        new CalendarioRecursosService();

                return new Respuesta(
                        true,
                        servicio.listarRecursosPorCategoria(categoriaId),
                        "Recursos obtenidos correctamente"
                );
            }

            if (Operaciones.CALENDARIO_RECURSOS_HORAS
                    .equals(peticion.getOperacion())) {

                CalendarioRecursosService servicio =
                        new CalendarioRecursosService();

                return new Respuesta(
                        true,
                        servicio.obtenerHorasDelDia(),
                        "Horas obtenidas correctamente"
                );
            }

            if (Operaciones.CALENDARIO_RECURSOS_RESERVA
                    .equals(peticion.getOperacion())) {

                Object[] datos = (Object[]) peticion.getDatos();

                Recurso recurso = (Recurso) datos[0];
                LocalDate fecha = (LocalDate) datos[1];
                LocalTime hora = (LocalTime) datos[2];

                CalendarioRecursosService servicio =
                        new CalendarioRecursosService();

                return new Respuesta(
                        true,
                        servicio.obtenerReservaDelRecurso(
                                recurso,
                                fecha,
                                hora
                        ),
                        "Consulta realizada correctamente"
                );
            }

            if (Operaciones.CALENDARIO_ACTIVIDADES_SEMANA
                    .equals(peticion.getOperacion())) {

                LocalDate fecha = (LocalDate) peticion.getDatos();

                CalendarioActividadesService servicio =
                        new CalendarioActividadesService();

                return new Respuesta(
                        true,
                        servicio.listarReservasSemana(fecha),
                        "Reservas semanales obtenidas correctamente"
                );
            }

            if (Operaciones.CALENDARIO_ACTIVIDADES_LUNES
                    .equals(peticion.getOperacion())) {

                LocalDate fecha = (LocalDate) peticion.getDatos();

                CalendarioActividadesService servicio =
                        new CalendarioActividadesService();

                return new Respuesta(
                        true,
                        servicio.obtenerLunes(fecha),
                        "Lunes obtenido correctamente"
                );
            }

            return new Respuesta(
                    false,
                    null,
                    "Operación no reconocida: " + peticion.getOperacion()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new Respuesta(
                    false,
                    null,
                    "Error al procesar la petición: " + e.getMessage()
            );
        }
    }

}