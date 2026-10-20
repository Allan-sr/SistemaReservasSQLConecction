package una.sistemareservas.negocio;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import una.sistemareservas.modelo.Recurso;
import una.sistemareservas.modelo.Reserva;

import una.sistemareservas.modelo.Categoria;
import una.sistemareservas.modelo.Funcionario;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class ReportePDFService {

    public static File generarComprobanteReserva(Reserva reserva) throws Exception {
        String nombreArchivo = "Comprobante_Reserva_" + reserva.getId() + ".pdf";
        File archivoPdf = new File(nombreArchivo);

        PdfWriter writer = new PdfWriter(archivoPdf);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Encabezado
        Paragraph titulo = new Paragraph("SISTEMA DE RESERVA DE RECURSOS")
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(41, 128, 185));

        Paragraph subtitulo = new Paragraph("Comprobante Oficial de Reserva")
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);

        document.add(titulo);
        document.add(subtitulo);

        // Tabla con detalles de la reserva
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        tabla.setWidth(UnitValue.createPercentValue(100));

        DateTimeFormatter fFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fHora = DateTimeFormatter.ofPattern("HH:mm");

        tabla.addCell(crearCeldaNegrita("Código de Reserva:"));
        tabla.addCell(reserva.getId());

        tabla.addCell(crearCeldaNegrita("Actividad:"));
        tabla.addCell(reserva.getActividad());

        tabla.addCell(crearCeldaNegrita("Fecha:"));
        tabla.addCell(reserva.getFecha() != null ? reserva.getFecha().format(fFecha) : "N/A");

        tabla.addCell(crearCeldaNegrita("Horario:"));
        String horario = (reserva.getHoraInicio() != null ? reserva.getHoraInicio().format(fHora) : "") +
                " - " +
                (reserva.getHoraFin() != null ? reserva.getHoraFin().format(fHora) : "");
        tabla.addCell(horario);

        tabla.addCell(crearCeldaNegrita("Estado:"));
        tabla.addCell(reserva.getEstado() != null ? reserva.getEstado().toString() : "ACTIVA");

        document.add(tabla);

        // Sección de Recursos Asignados
        Paragraph tituloRecursos = new Paragraph("Recursos Asignados")
                .setFontSize(12)
                .setMarginTop(20)
                .setMarginBottom(10);
        document.add(tituloRecursos);

        Table tablaRecursos = new Table(UnitValue.createPercentArray(new float[]{25, 40, 35}));
        tablaRecursos.setWidth(UnitValue.createPercentValue(100));

        tablaRecursos.addHeaderCell(crearCeldaNegrita("Código"));
        tablaRecursos.addHeaderCell(crearCeldaNegrita("Descripción"));
        tablaRecursos.addHeaderCell(crearCeldaNegrita("Categoría"));

        if (reserva.getRecursos() != null && !reserva.getRecursos().isEmpty()) {
            for (Recurso rec : reserva.getRecursos()) {
                tablaRecursos.addCell(rec.getId());
                tablaRecursos.addCell(rec.getDescripcion());
                tablaRecursos.addCell(rec.getCategoria() != null ? rec.getCategoria().getDescripcion() : "N/A");
            }
        } else if (reserva.getRecursoIds() != null) {
            for (String recId : reserva.getRecursoIds()) {
                tablaRecursos.addCell(recId);
                tablaRecursos.addCell("Recurso Asignado");
                tablaRecursos.addCell("N/A");
            }
        }

        document.add(tablaRecursos);

        // Pie de página
        Paragraph pie = new Paragraph("\n\n Documento generado automáticamente por el Sistema de Reserva de Recursos.")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(120, 120, 120));
        document.add(pie);

        document.close();
        return archivoPdf;
    }

    // Método auxiliar para construir celdas en negrita de forma limpia
    private static Cell crearCeldaNegrita(String texto) {
        Cell cell = new Cell();
        Paragraph p = new Paragraph(texto);
        cell.add(p);
        return cell;
    }

    public static File generarCalendarioActividades(LocalDate lunes, List<Reserva> reservas) throws Exception {
        File archivo = new File("Reporte_Calendario_Actividades.pdf");
        PdfWriter writer = new PdfWriter(archivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        document.add(new Paragraph("CALENDARIZACIÓN DE ACTIVIDADES").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Semana del " + lunes.format(formato) + " al " + lunes.plusDays(6).format(formato)));
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{
                                        25,
                                        40,
                                        35
                                }
                        )
                );
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell("Fecha");
        tabla.addHeaderCell("Actividad");
        tabla.addHeaderCell("Funcionario");
        for (Reserva reserva : reservas) {
            tabla.addCell(reserva.getFecha().format(formato));
            tabla.addCell(reserva.getActividad());
            String funcionario = reserva.getFuncionario() != null ? reserva.getFuncionario().getNombre() : reserva.getFuncionarioId();
            tabla.addCell(funcionario);
        }
        document.add(tabla);
        document.close();
        return archivo;
    }

    public static File generarEstadisticasCategorias(
            LocalDate inicio,
            LocalDate fin,
            Map<String, Integer> datos)
            throws Exception {
        File archivo = new File("Reporte_Estadisticas_Categorias.pdf");
        PdfWriter writer = new PdfWriter(archivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("ESTADÍSTICAS DE RECURSOS POR CATEGORÍA").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Período: " + inicio + " hasta " + fin));
        Table tabla = new Table(UnitValue.createPercentArray(
                                new float[]{
                                        70,
                                        30
                                }
                        )
                );
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell("Categoría");
        tabla.addHeaderCell("Cantidad");
        for (Map.Entry<String, Integer> entrada :
                datos.entrySet()) {
            tabla.addCell(entrada.getKey());
            tabla.addCell(String.valueOf(entrada.getValue()));
        }
        document.add(tabla);
        document.close();
        return archivo;
    }

    public static File generarEstadisticasActividades(
            LocalDate inicio,
            LocalDate fin,
            Map<String, Integer> datos)
            throws Exception {
        File archivo = new File("Reporte_Estadisticas_Actividades.pdf");
        PdfWriter writer = new PdfWriter(archivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("ESTADÍSTICAS DE ACTIVIDADES POR SEMANA").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Período: " + inicio + " hasta " + fin));
        Table tabla = new Table(UnitValue.createPercentArray(
                                new float[]{
                                        70,
                                        30
                                }
                        )
                );
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell("Semana");
        tabla.addHeaderCell("Actividades");
        for (Map.Entry<String, Integer> entrada :
                datos.entrySet()) {
            tabla.addCell(entrada.getKey());
            tabla.addCell(String.valueOf(entrada.getValue()));
        }
        document.add(tabla);
        document.close();
        return archivo;
    }

    public static File generarFuncionarios(List<Funcionario> funcionarios) throws Exception {
        File archivo = new File("Reporte_Funcionarios.pdf");
        PdfWriter writer = new PdfWriter(archivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("REPORTE DE FUNCIONARIOS").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Listado de funcionarios"));
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{30, 50, 20}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell("ID");
        tabla.addHeaderCell("Nombre");
        tabla.addHeaderCell("Teléfono");
        for (Funcionario funcionario : funcionarios) {
            tabla.addCell(funcionario.getId() != null ? funcionario.getId() : "N/A");
            tabla.addCell(funcionario.getNombre() != null ? funcionario.getNombre() : "N/A");
            tabla.addCell(funcionario.getTelefono() != null ? funcionario.getTelefono() : "N/A");
        }
        document.add(tabla);
        document.add(new Paragraph("\nDocumento generado automáticamente por el Sistema de Reserva de Recursos."));
        document.close();
        return archivo;
    }

    public static File generarCategorias(List<Categoria> categorias) throws Exception {
        File archivo = new File("Reporte_Categorias.pdf");
        PdfWriter writer = new PdfWriter(archivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("REPORTE DE CATEGORÍAS").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Listado de categorías"));
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell("ID");
        tabla.addHeaderCell("Descripción");
        for (Categoria categoria : categorias) {
            tabla.addCell(categoria.getId() != null ? categoria.getId() : "N/A");
            tabla.addCell(categoria.getDescripcion() != null ? categoria.getDescripcion() : "N/A");
        }
        document.add(tabla);
        document.add(new Paragraph("\nDocumento generado automáticamente por el Sistema de Reserva de Recursos."));
        document.close();
        return archivo;
    }

    public static File generarRecursos(List<Recurso> recursos) throws Exception {
        File archivo = new File("Reporte_Recursos.pdf");
        PdfWriter writer = new PdfWriter(archivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("REPORTE DE RECURSOS").setFontSize(18).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Listado de recursos"));
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{20, 25, 55}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.addHeaderCell("ID");
        tabla.addHeaderCell("Categoría");
        tabla.addHeaderCell("Descripción");
        for (Recurso recurso : recursos) {
            tabla.addCell(recurso.getId() != null ? recurso.getId() : "N/A");
            String categoria = recurso.getCategoriaId();
            if (categoria == null && recurso.getCategoria() != null) {
                categoria = recurso.getCategoria().getDescripcion();
            }
            tabla.addCell(categoria != null ? categoria : "N/A");
            tabla.addCell(recurso.getDescripcion() != null ? recurso.getDescripcion() : "N/A");
        }
        document.add(tabla);
        document.add(new Paragraph("\nDocumento generado automáticamente por el Sistema de Reserva de Recursos."));
        document.close();
        return archivo;
    }

    public static File generarCalendarioRecursos(
            Categoria categoria,
            LocalDate fecha,
            List<Recurso> recursos,
            Map<String, Reserva> reservasPorRecurso)
            throws Exception {

        File archivo =
                new File("Reporte_Calendario_Recursos.pdf");

        PdfWriter writer =
                new PdfWriter(archivo);

        PdfDocument pdf =
                new PdfDocument(writer);

        Document document =
                new Document(pdf);

        DateTimeFormatter formatoFecha =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        DateTimeFormatter formatoHora =
                DateTimeFormatter.ofPattern("HH:mm");

        document.add(
                new Paragraph(
                        "CALENDARIZACIÓN DE RECURSOS"
                )
                        .setFontSize(18)
                        .setTextAlignment(
                                TextAlignment.CENTER
                        )
        );

        document.add(
                new Paragraph(
                        "Fecha: "
                                + fecha.format(formatoFecha)
                )
        );

        document.add(
                new Paragraph(
                        "Categoría: "
                                + categoria.getDescripcion()
                )
        );

        Table tabla =
                new Table(
                        UnitValue.createPercentArray(
                                new float[]{
                                        20,
                                        25,
                                        30,
                                        25
                                }
                        )
                );

        tabla.setWidth(
                UnitValue.createPercentValue(100)
        );
        tabla.addHeaderCell("Recurso");
        tabla.addHeaderCell("Horario");
        tabla.addHeaderCell("Actividad");
        tabla.addHeaderCell("Funcionario");
        for (Recurso recurso : recursos) {
            Reserva reserva = reservasPorRecurso.get(recurso.getId());
            if (reserva != null) {
                tabla.addCell(recurso.getDescripcion());
                String horario = reserva.getHoraInicio().format(formatoHora) + " - " + reserva.getHoraFin().format(formatoHora);
                tabla.addCell(horario);
                tabla.addCell(reserva.getActividad() != null ? reserva.getActividad() : "N/A");
                String funcionario = reserva.getFuncionario() != null ? reserva.getFuncionario().getNombre() : reserva.getFuncionarioId();
                tabla.addCell(funcionario != null ? funcionario : "N/A");
            } else {
                tabla.addCell(recurso.getDescripcion()
                );
                tabla.addCell("Disponible");
                tabla.addCell("-");
                tabla.addCell("-");
            }
        }
        document.add(tabla);
        document.add(new Paragraph("\nDocumento generado automáticamente " + "por el Sistema de Reserva de Recursos."));
        document.close();
        return archivo;
    }
}