/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Librerías EXTERNAS de Apache POI (Excel)
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Librerías EXTERNAS de JFreeChart (Gráficos - Req 3)
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

// Librerías de Java para archivos, UI e imágenes
import java.io.File;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream; // Para la imagen del gráfico
import javax.imageio.ImageIO; // Para la imagen del gráfico
import javax.swing.JFileChooser;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.util.Map;
/**
 *
 * @author adrif
 */
/**
 * IMPLEMENTACIÓN (Trabajador de Excel)
 * Genera el reporte en formato Excel (.xlsx).
 * Sabe cómo manejar los 2 formatos visuales (Req 3):
 * - TABLA (Escribe filas y celdas)
 * - GRAFICO (Inserta una imagen del gráfico)
 */
public class GeneradorExcel implements IGeneradorReporte {

    private FormatoVisual formatoVisual;

    public GeneradorExcel(FormatoVisual visual) {
        this.formatoVisual = visual;
    }

    @Override
    public void guardar(Reporte datos) throws Exception {
        
        // 1. Preguntar al usuario dónde guardar el archivo
        String ruta = this.obtenerRutaGuardado(datos.getSalon().getNombre());
        if (ruta == null) {
            return; // El usuario canceló
        }

        // 2. Crear el libro de Excel en memoria
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // 3. (Req 3) Decide qué método llamar
            if (this.formatoVisual == FormatoVisual.TABLA) {
                // Caso: Tabla en Excel
                crearReporteTabla(workbook, datos);
            } else {
                // Caso: Gráfico en Excel
                crearReporteGrafico(workbook, datos);
            }

            // 4. Escribir el libro en el archivo físico
            try (FileOutputStream fileOut = new FileOutputStream(ruta)) {
                workbook.write(fileOut);
            }
            
            // 5. (Opcional) Abrir el archivo generado
            Desktop.getDesktop().open(new File(ruta));
        }
    }

    /**
     * LÓGICA CASO 3: TABLA en EXCEL (Req 1, 2, 5)
     * Crea las hojas, filas y celdas con los datos del reporte.
     */
    private void crearReporteTabla(Workbook workbook, Reporte datos) {
        // --- Estilos de Celda (Esto se queda igual) ---
        CellStyle styleTitulo = createEstilo(workbook, 16, true, HorizontalAlignment.CENTER);
        CellStyle styleSubtitulo = createEstilo(workbook, 14, true, HorizontalAlignment.LEFT);
        CellStyle styleHeader = createEstilo(workbook, 12, true, HorizontalAlignment.LEFT);

        // --- Crear Hoja ---
        Sheet sheet = workbook.createSheet("Reporte de Progreso");
        int numFila = 0;

        // Título y Período (Esto se queda igual)
        // ... (Tu código para el Título y Período va aquí) ...
        numFila++; 
        numFila++;
        numFila++; 

        // --- (Req 1) Decide qué tabla dibujar ---
        if (datos.esReporteIndividual()) {
            
            // --- CASO A: REPORTE INDIVIDUAL (¡ACTUALIZADO!) ---
            Estudiante est = datos.getEstudiante();
            ReporteDatosIndividual ind = datos.getDatosIndividuales();
            
            // --- 1. Resumen General (Tu reporte antiguo) ---
            sheet.createRow(numFila++).createCell(0).setCellValue("Reporte Individual: " + est.getNombre());
            sheet.getRow(numFila-1).getCell(0).setCellStyle(styleSubtitulo);
            
            Row rHeaderResumen = sheet.createRow(numFila++);
            rHeaderResumen.createCell(0).setCellValue("Estadística General");
            rHeaderResumen.createCell(1).setCellValue("Valor");
            rHeaderResumen.getCell(0).setCellStyle(styleHeader);
            rHeaderResumen.getCell(1).setCellStyle(styleHeader);
            
            Row rPuntos = sheet.createRow(numFila++);
            rPuntos.createCell(0).setCellValue("Puntaje en Período");
            rPuntos.createCell(1).setCellValue(ind.getPuntajeTotal());
            
            Row rPorc = sheet.createRow(numFila++);
            rPorc.createCell(0).setCellValue("% Aciertos en Período");
            rPorc.createCell(1).setCellValue(String.format("%.1f%%", ind.getPorcentajeAciertos()));

            Row rDif = sheet.createRow(numFila++);
            rDif.createCell(0).setCellValue("Áreas de Dificultad");
            rDif.createCell(1).setCellValue(ind.getTemasDificiles());
            
            numFila++; // Fila vacía

            // --- 2. Nueva Tabla de Desglose por Tema ---
            sheet.createRow(numFila++).createCell(0).setCellValue("Desglose de Desempeño por Tema");
            sheet.getRow(numFila-1).getCell(0).setCellStyle(styleSubtitulo);

            Row filaHeaderTemas = sheet.createRow(numFila++);
            String[] headersTemas = {"Tema", "Nivel Actual", "Puntos (Período)", "% Aciertos", "Intentos"};
            for (int i = 0; i < headersTemas.length; i++) {
                filaHeaderTemas.createCell(i).setCellValue(headersTemas[i]);
                filaHeaderTemas.getCell(i).setCellStyle(styleHeader);
            }
            
            for (ReporteDetalleTemaEstudiante d : ind.getDetallePorTema()) {
                Row fila = sheet.createRow(numFila++);
                fila.createCell(0).setCellValue(d.getNombreTema());
                fila.createCell(1).setCellValue(d.getNivelActual().toString());
                fila.createCell(2).setCellValue(d.getPuntosEnPeriodo());
                fila.createCell(3).setCellValue(String.format("%.1f%%", d.getPorcentajeAciertos()));
                fila.createCell(4).setCellValue(d.getIntentosEnPeriodo());
            }
        } else {
            // REPORTE POR SALON
            
            // Seccion Ranking
            sheet.createRow(numFila++).createCell(0).setCellValue("Ranking del Salón (Top Puntajes)");
            sheet.getRow(numFila-1).getCell(0).setCellStyle(styleSubtitulo);

            Row filaHeaderRanking = sheet.createRow(numFila++);
            String[] headersRanking = {"Ranking", "Estudiante", "Puntaje Total (en período)"};
            for (int i = 0; i < headersRanking.length; i++) {
                filaHeaderRanking.createCell(i).setCellValue(headersRanking[i]);
                filaHeaderRanking.getCell(i).setCellStyle(styleHeader);
            }
            
            int rankingPos = 1;
            for (RankingEntry entry : datos.getRanking()) {
                Row fila = sheet.createRow(numFila++);
                fila.createCell(0).setCellValue(rankingPos++); // Posicion (1, 2, 3...)
                fila.createCell(1).setCellValue(entry.getEstudiante().getNombre());
                fila.createCell(2).setCellValue(entry.getPuntaje());
            }
            
            numFila++; // Fila vacia

            sheet.createRow(numFila++).createCell(0).setCellValue("Reporte Detallado por Tema");
            sheet.getRow(numFila-1).getCell(0).setCellStyle(styleSubtitulo);

            // crea la nueva fila de cabecera
            Row filaHeaderTemas = sheet.createRow(numFila++);
            String[] headersTemas = {"Tema", "Intentos Totales", "% Aciertos", "Puntos Generados"};
            for (int i = 0; i < headersTemas.length; i++) {
                filaHeaderTemas.createCell(i).setCellValue(headersTemas[i]);
                filaHeaderTemas.getCell(i).setCellStyle(styleHeader);
            }

            // llena la tabla con los datos detallados
            for (ReporteDatosPorTema d : datos.getDatosPorTema().values()) {
                Row fila = sheet.createRow(numFila++);
                fila.createCell(0).setCellValue(d.getNombreTema());
                fila.createCell(1).setCellValue(d.getIntentosTotales());
                fila.createCell(2).setCellValue(String.format("%.1f%%", d.getPorcentajeAciertos()));
                fila.createCell(3).setCellValue(d.getPuntosTotales());
            }
        }
        
        // Auto-ajustar columnas 
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
    }

    /**
     * LÓGICA CASO 4: GRÁFICO en EXCEL (Req 1, 3)
     * Genera un gráfico con JFreeChart y lo inserta en la hoja.
     */
    private void crearReporteGrafico(Workbook workbook, Reporte datos) throws Exception {
        Sheet sheet = workbook.createSheet("Gráfico Resumen");
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String tituloGrafico = ""; // Título dinámico

        if (datos.esReporteIndividual()) {
            // --- CASO A: GRÁFICO INDIVIDUAL ---
            tituloGrafico = "Desempeño por Tema de " + datos.getEstudiante().getNombre();
            
            // (Usa el mapa que añadimos a ReporteDatosIndividual)
            Map<String, Double> promedios = datos.getDatosIndividuales().getPromedioPorTema();

            if (promedios == null || promedios.isEmpty()) {
                sheet.createRow(0).createCell(0).setCellValue("No hay datos de temas para graficar para este estudiante.");
                return;
            }

            for (Map.Entry<String, Double> entry : promedios.entrySet()) {
                dataset.addValue(entry.getValue(), "% Aciertos", entry.getKey());
            }

        } else {
            // --- CASO B: GRÁFICO DE SALÓN ---
            tituloGrafico = "Promedio del Salón por Tema";
            
            // (¡Usa la nueva clase ReporteDatosPorTema!)
            Map<String, ReporteDatosPorTema> datosPorTema = datos.getDatosPorTema();

            if (datosPorTema == null || datosPorTema.isEmpty()) {
                 sheet.createRow(0).createCell(0).setCellValue("No hay datos de temas para graficar para este salón.");
                return;
            }

            for (ReporteDatosPorTema d : datosPorTema.values()) {
                dataset.addValue(d.getPorcentajeAciertos(), "% Aciertos", d.getNombreTema());
            }
        }

        // --- 2. Crear el Gráfico en memoria ---
        // ¡ESTA ES LA LÍNEA QUE PREGUNTABAS, AHORA ACTUALIZADA!
        JFreeChart barChart = ChartFactory.createBarChart(
            tituloGrafico, // <-- Título dinámico
            "Tema", 
            "% Aciertos", 
            dataset,
            PlotOrientation.VERTICAL,
            false, true, false); 

        // --- 3. Convertir el Gráfico a una Imagen (en RAM) ---
        BufferedImage chartImage = barChart.createBufferedImage(600, 400); // 600px ancho, 400px alto
        
        byte[] imageBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(chartImage, "png", baos);
            imageBytes = baos.toByteArray();
        }

        // --- 4. Añadir la imagen de bytes al libro de Excel ---
        int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
        CreationHelper helper = workbook.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        
        // Crear un "ancla" para la imagen (se ancla en la celda B3)
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(1); // Columna B
        anchor.setRow1(2); // Fila 3
        
        Picture pict = drawing.createPicture(anchor, pictureIdx);
        
        // Ajustar el tamaño
        pict.resize(1.0); 
    }

    /**
     * Helper: Muestra una ventana de "Guardar como..."
     */
    private String obtenerRutaGuardado(String nombreSalon) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte Excel");
        fileChooser.setSelectedFile(new File("Reporte_" + nombreSalon + ".xlsx"));
        
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null; // El usuario presionó "Cancelar"
    }

    /**
     * Helper para crear estilos de celda (Req 5)
     */
    private CellStyle createEstilo(Workbook wb, int fontSize, boolean isBold, HorizontalAlignment align) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(isBold);
        font.setFontHeightInPoints((short) fontSize);
        style.setFont(font);
        style.setAlignment(align);
        return style;
    }
}
