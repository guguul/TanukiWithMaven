/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
/**
 *
 * @author adrif
 */
public class Reporte {
    private Salon salon;
    private Estudiante estudiante; // el estudiante (si es individual)
    private LocalDate fechaInicio, fechaFin;
    private boolean esIndividual;
    
    // datos para reporte individual
    private ReporteDatosIndividual datosIndividuales;
    
    // datos para reporte de salon
    private List<RankingEntry> ranking;
    private Map<String, ReporteDatosPorTema> datosPorTema;

    public Reporte(Salon s, LocalDate i, LocalDate f) {
        this.salon = s; this.fechaInicio = i; this.fechaFin = f; this.esIndividual = false; //es de salon
    }
    
    public void setDatosIndividuales(Estudiante est, ReporteDatosIndividual d) {
        this.estudiante = est;
        this.datosIndividuales = d;
        this.esIndividual = true; // lo marca como individual
    }
    
    public void setDatosPorTema(Map<String, ReporteDatosPorTema> p) { 
        this.datosPorTema = p; 
    }
    public void setRanking(List<RankingEntry> r) { this.ranking = r; }

    public Salon getSalon() {
        return salon;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public boolean isEsIndividual() {
        return esIndividual;
    }
    
    public Estudiante getEstudiante() { return estudiante; }
    
    public ReporteDatosIndividual getDatosIndividuales() {
        return datosIndividuales;
    }

    public List<RankingEntry> getRanking() {
        return ranking;
    }
    
    public Map<String, ReporteDatosPorTema> getDatosPorTema() { 
        return datosPorTema; 
    }
    
    public boolean esReporteIndividual() {
        return this.esIndividual;
    }
}
