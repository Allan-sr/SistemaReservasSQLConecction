package una.sistemareservas.util;

import una.sistemareservas.datos.CategoriaDAO;
import una.sistemareservas.datos.RecursoDAO;
import una.sistemareservas.datos.ReservaDAO;
import una.sistemareservas.datos.UsuarioDAO;
import una.sistemareservas.modelo.Administrador;
import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.EstadoReserva;
import una.sistemareservas.modelo.Funcionario;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;
import una.sistemareservas.modelo.Rol;
import una.sistemareservas.modelo.Usuario;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga datos de prueba para poder verificar el funcionamiento
 * completo del sistema.
 *
 * Los datos solamente se agregan si no existen.
 *
 * Esto permite ejecutar el proyecto varias veces sin duplicar
 * usuarios, categorías, recursos o reservas.
 */
public class DatosIniciales {

    private static final String ARCHIVO_RECURSOS = "recursos.xml";
    private static final String ARCHIVO_RESERVAS = "reservas.xml";

    private DatosIniciales() {

    }

    public static void inicializar() {

        System.out.println("==========================================");
        System.out.println("   INICIALIZANDO DATOS DE PRUEBA");
        System.out.println("==========================================");

        inicializarUsuarios();
        inicializarCategorias();
        inicializarRecursos();
        inicializarReservas();

        System.out.println("Datos de prueba listos.");
        System.out.println("==========================================");
    }

    /**
     * Crea usuarios de prueba si todavía no existen.
     */
    private static void inicializarUsuarios() {

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        List<Usuario> usuarios = usuarioDAO.cargar();

        agregarAdministradorSiNoExiste(
                usuarios,
                "admin",
                "admin"
        );

        agregarFuncionarioSiNoExiste(
                usuarios,
                "1001",
                "Funcionario de prueba",
                "8888-8888"
        );

        agregarFuncionarioSiNoExiste(
                usuarios,
                "1002",
                "Maria Rodriguez",
                "8888-1002"
        );

        agregarFuncionarioSiNoExiste(
                usuarios,
                "1003",
                "Carlos Hernandez",
                "8888-1003"
        );

        usuarioDAO.guardar(usuarios);

        System.out.println("Usuarios verificados: " + usuarios.size());
    }

    private static void agregarAdministradorSiNoExiste(
            List<Usuario> usuarios,
            String id,
            String clave) {

        for (Usuario usuario : usuarios) {
            if (usuario.getId().equalsIgnoreCase(id)) {
                return;
            }
        }

        usuarios.add(
                new Administrador(
                        id,
                        clave,
                        Rol.ADMINISTRADOR
                )
        );
    }

    private static void agregarFuncionarioSiNoExiste(
            List<Usuario> usuarios,
            String id,
            String nombre,
            String telefono) {

        for (Usuario usuario : usuarios) {
            if (usuario.getId().equalsIgnoreCase(id)) {
                return;
            }
        }

        /*
         * Según el funcionamiento del proyecto,
         * la contraseña inicial del funcionario es su ID.
         */
        usuarios.add(
                new Funcionario(
                        id,
                        id,
                        Rol.FUNCIONARIO,
                        nombre,
                        telefono
                )
        );
    }

    /**
     * Crea las categorías necesarias para las pruebas.
     */
    private static void inicializarCategorias() {

        CategoriaDAO categoriaDAO = new CategoriaDAO();

        List<Categoria> categorias = categoriaDAO.cargar();

        agregarCategoriaSiNoExiste(
                categorias,
                "Sala de reuniones"
        );

        agregarCategoriaSiNoExiste(
                categorias,
                "Laptop"
        );

        agregarCategoriaSiNoExiste(
                categorias,
                "Proyector"
        );

        agregarCategoriaSiNoExiste(
                categorias,
                "Sala de capacitacion"
        );

        agregarCategoriaSiNoExiste(
                categorias,
                "Computadora de escritorio"
        );

        categoriaDAO.guardar(categorias);

        System.out.println("Categorias verificadas: " + categorias.size());
    }

    private static Categoria agregarCategoriaSiNoExiste(
            List<Categoria> categorias,
            String descripcion) {

        for (Categoria categoria : categorias) {

            if (categoria.getDescripcion()
                    .equalsIgnoreCase(descripcion)) {

                return categoria;
            }
        }

        /*
         * El ID NO se genera manualmente.
         *
         * Se utiliza el mismo mecanismo del CategoriaDAO
         * para mantener la regla de IDs CAT-000001, etc.
         */
        CategoriaDAO categoriaDAO = new CategoriaDAO();

        categoriaDAO.agregar(descripcion);

        /*
         * Volvemos a cargar para obtener la categoría
         * con el ID que generó el DAO.
         */
        List<Categoria> actualizadas = categoriaDAO.cargar();

        for (Categoria categoria : actualizadas) {

            if (categoria.getDescripcion()
                    .equalsIgnoreCase(descripcion)) {

                categorias.clear();
                categorias.addAll(actualizadas);

                return categoria;
            }
        }

        return null;
    }

    /**
     * Crea los recursos necesarios para las pruebas.
     */
    private static void inicializarRecursos() {

        CategoriaDAO categoriaDAO = new CategoriaDAO();
        RecursoDAO recursoDAO = new RecursoDAO();

        List<Categoria> categorias = categoriaDAO.cargar();
        List<Recurso> recursos = recursoDAO.cargar();

        Categoria salaReuniones =
                buscarCategoria(categorias, "Sala de reuniones");

        Categoria laptop =
                buscarCategoria(categorias, "Laptop");

        Categoria proyector =
                buscarCategoria(categorias, "Proyector");

        Categoria salaCapacitacion =
                buscarCategoria(categorias, "Sala de capacitacion");

        Categoria computadora =
                buscarCategoria(categorias, "Computadora de escritorio");

        /*
         * Salas de reuniones.
         */
        agregarRecursoSiNoExiste(
                recursos,
                "SALA-001",
                salaReuniones,
                "Sala de reuniones 1"
        );

        agregarRecursoSiNoExiste(
                recursos,
                "SALA-002",
                salaReuniones,
                "Sala de reuniones 2"
        );

        /*
         * Laptops.
         */
        agregarRecursoSiNoExiste(
                recursos,
                "LAP-001",
                laptop,
                "Laptop Dell 1"
        );

        agregarRecursoSiNoExiste(
                recursos,
                "LAP-002",
                laptop,
                "Laptop Dell 2"
        );

        agregarRecursoSiNoExiste(
                recursos,
                "LAP-003",
                laptop,
                "Laptop Lenovo 1"
        );

        /*
         * Proyectores.
         */
        agregarRecursoSiNoExiste(
                recursos,
                "PROY-001",
                proyector,
                "Proyector Epson 1"
        );

        agregarRecursoSiNoExiste(
                recursos,
                "PROY-002",
                proyector,
                "Proyector Epson 2"
        );

        /*
         * Sala de capacitación.
         */
        agregarRecursoSiNoExiste(
                recursos,
                "SALA-CAP-001",
                salaCapacitacion,
                "Sala de capacitacion principal"
        );

        /*
         * Computadoras.
         */
        agregarRecursoSiNoExiste(
                recursos,
                "PC-001",
                computadora,
                "Computadora Dell 1"
        );

        agregarRecursoSiNoExiste(
                recursos,
                "PC-002",
                computadora,
                "Computadora Dell 2"
        );

        recursoDAO.guardar(recursos);

        System.out.println("Recursos verificados: " + recursos.size());
    }

    private static Categoria buscarCategoria(
            List<Categoria> categorias,
            String descripcion) {

        for (Categoria categoria : categorias) {

            if (categoria.getDescripcion()
                    .equalsIgnoreCase(descripcion)) {

                return categoria;
            }
        }

        return null;
    }

    private static void agregarRecursoSiNoExiste(
            List<Recurso> recursos,
            String id,
            Categoria categoria,
            String descripcion) {

        if (categoria == null) {
            System.out.println(
                    "No se pudo crear el recurso "
                            + id
                            + ": categoria no encontrada."
            );
            return;
        }

        for (Recurso recurso : recursos) {

            if (recurso.getId().equalsIgnoreCase(id)) {
                return;
            }
        }

        recursos.add(
                new Recurso(
                        id,
                        categoria,
                        descripcion
                )
        );
    }

    /**
     * Crea reservas de prueba.
     *
     * Se utilizan fechas relativas a LocalDate.now()
     * para que las reservas siempre sean futuras al
     * ejecutar el programa.
     */
    private static void inicializarReservas() {

        ReservaDAO reservaDAO = new ReservaDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        RecursoDAO recursoDAO = new RecursoDAO();

        List<Reserva> reservas = reservaDAO.cargar();

        /*
         * Si ya existen reservas, no agregamos nuevamente
         * el conjunto inicial.
         */
        if (!reservas.isEmpty()) {

            System.out.println(
                    "Reservas existentes encontradas: "
                            + reservas.size()
            );

            return;
        }

        List<Usuario> usuarios = usuarioDAO.cargar();
        List<Recurso> recursos = recursoDAO.cargar();

        Funcionario funcionario1001 =
                buscarFuncionario(usuarios, "1001");

        Funcionario funcionario1002 =
                buscarFuncionario(usuarios, "1002");

        Funcionario funcionario1003 =
                buscarFuncionario(usuarios, "1003");

        /*
         * Recursos.
         */
        Recurso sala1 =
                buscarRecurso(recursos, "SALA-001");

        Recurso sala2 =
                buscarRecurso(recursos, "SALA-002");

        Recurso laptop1 =
                buscarRecurso(recursos, "LAP-001");

        Recurso laptop2 =
                buscarRecurso(recursos, "LAP-002");

        Recurso proyector1 =
                buscarRecurso(recursos, "PROY-001");

        Recurso proyector2 =
                buscarRecurso(recursos, "PROY-002");

        Recurso salaCap =
                buscarRecurso(recursos, "SALA-CAP-001");

        Recurso pc1 =
                buscarRecurso(recursos, "PC-001");

        /*
         * Fechas relativas.
         *
         * Día 1 = mañana.
         * Día 3 = dentro de tres días.
         * Día 8 = siguiente semana.
         * Día 15 = otra semana.
         */
        LocalDate dia1 = LocalDate.now().plusDays(1);
        LocalDate dia3 = LocalDate.now().plusDays(3);
        LocalDate dia8 = LocalDate.now().plusDays(8);
        LocalDate dia15 = LocalDate.now().plusDays(15);

        /*
         * RES-000001
         *
         * Reunión de proyecto.
         */
        Reserva reserva1 = crearReserva(
                "RES-000001",
                "Reunion de proyecto",
                dia1,
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                funcionario1001
        );

        agregarRecurso(reserva1, sala1);
        agregarRecurso(reserva1, laptop1);
        agregarRecurso(reserva1, proyector1);

        reservas.add(reserva1);

        /*
         * RES-000002
         *
         * Capacitación.
         */
        Reserva reserva2 = crearReserva(
                "RES-000002",
                "Capacitacion de personal",
                dia3,
                LocalTime.of(14, 0),
                LocalTime.of(17, 0),
                funcionario1002
        );

        agregarRecurso(reserva2, salaCap);
        agregarRecurso(reserva2, proyector2);

        reservas.add(reserva2);

        /*
         * RES-000003
         *
         * Presentación.
         */
        Reserva reserva3 = crearReserva(
                "RES-000003",
                "Presentacion de proyecto",
                dia8,
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                funcionario1003
        );

        agregarRecurso(reserva3, sala2);
        agregarRecurso(reserva3, laptop2);
        agregarRecurso(reserva3, proyector1);

        reservas.add(reserva3);

        /*
         * RES-000004
         *
         * Reunión administrativa.
         */
        Reserva reserva4 = crearReserva(
                "RES-000004",
                "Reunion administrativa",
                dia15,
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                funcionario1001
        );

        agregarRecurso(reserva4, sala1);
        agregarRecurso(reserva4, pc1);

        reservas.add(reserva4);

        reservaDAO.guardar(reservas);

        System.out.println(
                "Reservas de prueba creadas: "
                        + reservas.size()
        );
    }

    private static Reserva crearReserva(
            String id,
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Funcionario funcionario) {

        return new Reserva(
                id,
                actividad,
                fecha,
                horaInicio,
                horaFin,
                funcionario
        );
    }

    private static void agregarRecurso(
            Reserva reserva,
            Recurso recurso) {

        if (reserva == null || recurso == null) {
            return;
        }

        reserva.agregarRecurso(recurso);
    }

    private static Funcionario buscarFuncionario(
            List<Usuario> usuarios,
            String id) {

        for (Usuario usuario : usuarios) {

            if (usuario instanceof Funcionario funcionario
                    && funcionario.getId()
                    .equalsIgnoreCase(id)) {

                return funcionario;
            }
        }

        return null;
    }

    private static Recurso buscarRecurso(
            List<Recurso> recursos,
            String id) {

        for (Recurso recurso : recursos) {

            if (recurso.getId().equalsIgnoreCase(id)) {
                return recurso;
            }
        }

        return null;
    }
}
