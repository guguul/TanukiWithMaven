/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import java.util.*;
/**
 *
 * @author adrif
 */
public class ReporteDatosIndividual {
    
    // --- Datos del Resumen Antiguo (para image_b2e3a6.png) ---
    private int puntajeTotal;
    private double porcentajeAciertos;
    private String temasDificiles;
    
    // --- Datos para el Gráfico Individual ---
    private Map<String, Double> promedioPorTema; 
    
    // --- Datos para la Nueva Tabla Detallada ---
    private List<ReporteDetalleTemaEstudiante> detallePorTema; 

    // --- Constructor Completo ---
    public ReporteDatosIndividual(
            int puntajeTotal, 
            double porcentajeAciertos, 
            String temasDificiles, 
            Map<String, Double> promedioPorTema, 
            List<ReporteDetalleTemaEstudiante> detallePorTema
    ) {
        this.puntajeTotal = puntajeTotal;
        this.porcentajeAciertos = porcentajeAciertos;
        this.temasDificiles = temasDificiles;
        this.promedioPorTema = promedioPorTema;
        this.detallePorTema = detallePorTema;
    }
    
    public int getPuntajeTotal() {
        return puntajeTotal;
    }
    public double getPorcentajeAciertos() {
        return porcentajeAciertos;
    }
    public String getTemasDificiles() {
        return temasDificiles;
    }
    public Map<String, Double> getPromedioPorTema() {
        return promedioPorTema;
    }
    public List<ReporteDetalleTemaEstudiante> getDetallePorTema() {
        return detallePorTema;
    }
}
