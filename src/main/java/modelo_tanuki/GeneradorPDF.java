/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

// Librerías de Java
import java.io.File;
import javax.swing.JFileChooser;
import java.awt.Desktop;
import java.awt.image.BufferedImage; // Para el gráfico en memoria
import java.util.Map;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
/**
 *
 * @author adrif
 */
public class GeneradorPDF implements IGeneradorReporte {

    private FormatoVisual formatoVisual;

    public GeneradorPDF(FormatoVisual visual) {
        this.formatoVisual = visual;
    }

    @Override
    public void guardar(Reporte datos) throws Exception {
        
        String ruta = this.obtenerRutaGuardado(datos.getSalon().getNombre());
        if (ruta == null) return; // Cancelado

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                
                if (this.formatoVisual == FormatoVisual.TABLA) {
                    this.crearReporteTabla(content, datos);
                } else {
                    this.crearReporteGrafico(doc, content, datos);
                }
            }
            doc.save(ruta);
        }
        
        Desktop.getDesktop().open(new File(ruta));
    }

    /**
     * LÓGICA CASO 2: TABLA en PDF
     */
    private void crearReporteTabla(PDPageContentStream content, Reporte datos) throws Exception {
        TextWriter writer = new TextWriter(content, 750); // (Tu helper de escritura)
        // Título y Período (Esto se queda igual)
        writer.writeLine("Reporte de Progreso: Salón " + datos.getSalon().getNombre(), Standard14Fonts.FontName.HELVETICA_BOLD, 16);
        writer.newLine(10);
        writer.writeLine("Período: " + datos.getFechaInicio() + " al " + datos.getFechaFin(), Standard14Fonts.FontName.HELVETICA, 12);
        writer.newLine(20);

        if (datos.esReporteIndividual()) {
            
            // --- REPORTE INDIVIDUAL (¡ACTUALIZADO!) ---
            ReporteDatosIndividual ind = datos.getDatosIndividuales();
            
            writer.writeLine("Reporte Individual: " + datos.getEstudiante().getNombre(), Standard14Fonts.FontName.HELVETICA_BOLD, 14);
            writer.newLine(15);
            
            // --- 1. Resumen General (Tu reporte antiguo) ---
            writer.writeLine(String.format("Puntaje en Período: %d", ind.getPuntajeTotal()), Standard14Fonts.FontName.HELVETICA, 12);
            writer.writeLine(String.format("%% Aciertos General: %.1f%%", ind.getPorcentajeAciertos()), Standard14Fonts.FontName.HELVETICA, 12);
            writer.writeLine(String.format("Áreas de Dificultad: %s", ind.getTemasDificiles()), Standard14Fonts.FontName.HELVETICA, 12);
            writer.newLine(25); // Más espacio

            // --- 2. Nueva Tabla de Desglose por Tema ---
            writer.writeLine("Desglose de Desempeño por Tema:", Standard14Fonts.FontName.HELVETICA_BOLD, 14);
            writer.newLine(15);
            
            // (Cabecera de la tabla)
            writer.writeLine(String.format("%-20s | %-10s | %-8s | %-10s | %s", 
                "Tema", "Nivel", "Puntos", "% Aciertos", "Intentos"), 
                Standard14Fonts.FontName.HELVETICA_BOLD, 10);
            writer.writeLine("-------------------------------------------------------------------", Standard14Fonts.FontName.HELVETICA_BOLD, 10);
            
            for (ReporteDetalleTemaEstudiante d : ind.getDetallePorTema()) {
                String linea = String.format("%-20s | %-10s | %-8d | %-10.1f%% | %d",
                    d.getNombreTema(),
                    d.getNivelActual().toString(),
                    d.getPuntosEnPeriodo(),
                    d.getPorcentajeAciertos(),
                    d.getIntentosEnPeriodo()
                );
                writer.writeLine(linea, Standard14Fonts.FontName.COURIER, 10); // (Usamos Courier para alinear)
            }

        } else {
            // --- Reporte de Salón ---
            
            // Ranking (Req 1)
            writer.writeLine("Ranking del Salón", Standard14Fonts.FontName.HELVETICA_BOLD, 14);
            writer.newLine(15);
            int rankingPos = 1;
            for (RankingEntry entry : datos.getRanking()) {
                String linea = String.format("%d. %s - %d puntos", 
                                 rankingPos++, 
                                 entry.getEstudiante().getNombre(), 
                                 entry.getPuntaje());
                writer.writeLine(linea, Standard14Fonts.FontName.HELVETICA, 12);
            }
            writer.newLine(20);

            writer.writeLine("Reporte Detallado por Tema", Standard14Fonts.FontName.HELVETICA_BOLD, 14);
            writer.newLine(15);
            
            for (ReporteDatosPorTema d : datos.getDatosPorTema().values()) { //itera sobre la clase reporteDatosPorTema
                
                // escribe el nombre del tema en negrita
                writer.writeLine(d.getNombreTema() + ":", Standard14Fonts.FontName.HELVETICA_BOLD, 12);
                
                // escribe los detalles
                String lineaAciertos = String.format("  - %% Aciertos: %.1f%% (%d / %d intentos)", 
                                                     d.getPorcentajeAciertos(), 
                                                     d.getAciertosTotales(), 
                                                     d.getIntentosTotales());
                String lineaPuntos = String.format("  - Puntos Generados: %d", d.getPuntosTotales());
                
                writer.writeLine(lineaAciertos, Standard14Fonts.FontName.HELVETICA, 11);
                writer.writeLine(lineaPuntos, Standard14Fonts.FontName.HELVETICA, 11);
                writer.newLine(5); // Un pequeno espacio
            }
        }
    }

    /**
     * LÓGICA CASO 4: GRÁFICO en PDF (Req 3)
     */
    private void crearReporteGrafico(PDDocument doc, PDPageContentStream content, Reporte datos) throws Exception {
    
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String tituloGrafico = "";

        if (datos.esReporteIndividual()) {
            // GRAFICO INDIVIDUAL
            tituloGrafico = "Desempeño por Tema de " + datos.getEstudiante().getNombre();
            Map<String, Double> promedios = datos.getDatosIndividuales().getPromedioPorTema();

            if (promedios == null || promedios.isEmpty()) {
                TextWriter writer = new TextWriter(content, 750);
                writer.writeLine("No hay datos de temas para graficar.", Standard14Fonts.FontName.HELVETICA_BOLD, 12);
                return;
            }

            for (Map.Entry<String, Double> entry : promedios.entrySet()) {
                dataset.addValue(entry.getValue(), "% Aciertos", entry.getKey());
            }

        } else {
            // Grafico de salon
            tituloGrafico = "Promedio del Salón por Tema";

            for (ReporteDatosPorTema d : datos.getDatosPorTema().values()) {
                dataset.addValue(d.getPorcentajeAciertos(), "% Aciertos", d.getNombreTema());
            }
        }

        // general el grafico independientemente si es indv o salon
        JFreeChart barChart = ChartFactory.createBarChart(
            tituloGrafico, "Tema", "% Aciertos",
            dataset, PlotOrientation.VERTICAL,
            false, true, false); 

        BufferedImage bufferedImage = barChart.createBufferedImage(450, 400);
        PDImageXObject pdImage = LosslessFactory.createFromImage(doc, bufferedImage);
        content.drawImage(pdImage, 50, 300, 450, 400);

        TextWriter writer = new TextWriter(content, 750);
        writer.writeLine("Reporte Gráfico: " + datos.getSalon().getNombre(), Standard14Fonts.FontName.HELVETICA_BOLD, 16);
    }

    /**
     * Helper: Muestra una ventana de "Guardar como..."
     */
    private String obtenerRutaGuardado(String nombreSalon) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setSelectedFile(new File("Reporte_" + nombreSalon + ".pdf"));
        
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
    
    
    /**
     * -------------------------------------------------------------------
     * Esta clase interna usa la nueva sintaxis de PDFBox 3.x
     * -------------------------------------------------------------------
     */
    private static class TextWriter {
        private PDPageContentStream content;
        private float currentY;
        
        TextWriter(PDPageContentStream content, float startY) {
            this.content = content;
            this.currentY = startY;
        }
        
        void writeLine(String text, Standard14Fonts.FontName fontName, int fontSize) throws Exception {
            content.beginText();
            
            // Creamos la fuente usando la nueva sintaxis
            content.setFont(new PDType1Font(fontName), fontSize);
            
            content.newLineAtOffset(50, currentY); // 50 = margen izquierdo
            content.showText(text);
            content.endText();
            currentY -= (fontSize * 1.5f); // Baja el "cursor"
        }
        
        void newLine(float space) {
            currentY -= space;
        }
    }
}