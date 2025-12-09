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
        TextWriter writer = new TextWriter(content, 750); 
        
        // Encabezado
        writer.writeLine("Reporte de Progreso: Salón " + datos.getSalon().getNombre(), Standard14Fonts.FontName.HELVETICA_BOLD, 16);
        writer.newLine(10);
        writer.writeLine("Período: " + datos.getFechaInicio() + " al " + datos.getFechaFin(), Standard14Fonts.FontName.HELVETICA, 12);
        writer.newLine(20);

        if (datos.esReporteIndividual()) {
            // ... (Lógica individual se queda IGUAL, no la tocamos) ...
            ReporteDatosIndividual ind = datos.getDatosIndividuales();
            writer.writeLine("Reporte Individual: " + datos.getEstudiante().getNombre(), Standard14Fonts.FontName.HELVETICA_BOLD, 14);
            writer.newLine(15);
            writer.writeLine(String.format("Puntaje en Período: %d", ind.getPuntajeTotal()), Standard14Fonts.FontName.HELVETICA, 12);
            writer.writeLine(String.format("%% Aciertos General: %.1f%%", ind.getPorcentajeAciertos()), Standard14Fonts.FontName.HELVETICA, 12);
            writer.newLine(20);
            writer.writeLine("Desglose por Tema:", Standard14Fonts.FontName.HELVETICA_BOLD, 14);
            writer.newLine(10);
            writer.writeLine(String.format("%-20s | %-10s | %-8s | %s", "Tema", "Nivel", "Puntos", "% Aciertos"), Standard14Fonts.FontName.HELVETICA_BOLD, 10);
            writer.writeLine("------------------------------------------------------------", Standard14Fonts.FontName.HELVETICA_BOLD, 10);
            if (ind.getDetallePorTema() != null) {
                for (ReporteDetalleTemaEstudiante d : ind.getDetallePorTema()) {
                    double porc = d.getPorcentajeAciertos() <= 1.0 ? d.getPorcentajeAciertos() * 100 : d.getPorcentajeAciertos();
                    String linea = String.format("%-20s | %-10s | %-8d | %.1f%%",
                        d.getNombreTema().length() > 18 ? d.getNombreTema().substring(0,18)+".." : d.getNombreTema(),
                        d.getNivelActual().toString(), d.getPuntosEnPeriodo(), porc);
                    writer.writeLine(linea, Standard14Fonts.FontName.COURIER, 10);
                }
            }

        } else {
            // ==========================================
            // CASO B: REPORTE DE SALÓN (MODIFICADO)
            // ==========================================
            
            // 1. IMPRIMIR RANKING (Si existe)
            if (datos.getRanking() != null && !datos.getRanking().isEmpty()) {
                
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
                
                // Espacio grande antes de la siguiente sección
                writer.newLine(30); 
            }
            
            // 2. IMPRIMIR DETALLES (Si existen) - ¡YA NO HAY 'ELSE'!
            if (datos.getDatosPorTema() != null && !datos.getDatosPorTema().isEmpty()) {
                
                writer.writeLine("Reporte Detallado por Tema", Standard14Fonts.FontName.HELVETICA_BOLD, 14);
                writer.newLine(15);
                
                for (ReporteDatosPorTema d : datos.getDatosPorTema().values()) {
                    writer.writeLine(d.getNombreTema() + ":", Standard14Fonts.FontName.HELVETICA_BOLD, 12);
                    
                    double porc = d.getPorcentajeAciertos();
                    if (porc <= 1.0) porc *= 100.0;
                    
                    String lineaAciertos = String.format("  - %% Aciertos: %.1f%%", porc);
                    String lineaPuntos = String.format("  - Puntos Generados: %d", d.getPuntosTotales());
                    
                    writer.writeLine(lineaAciertos, Standard14Fonts.FontName.HELVETICA, 11);
                    writer.writeLine(lineaPuntos, Standard14Fonts.FontName.HELVETICA, 11);
                    writer.newLine(10);
                }
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