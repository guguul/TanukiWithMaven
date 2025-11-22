/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;

/**
 *
 * @author adrif
 */
public class ReporteDatosPorTema {
    private String nombreTema;
    private int intentosTotales = 0;
    private int aciertosTotales = 0;
    private int puntosTotales = 0;
    private double porcentajeAciertos = 0.0;

    public ReporteDatosPorTema(String nombreTema) {
        this.nombreTema = nombreTema;
    }

    // getters
    public String getNombreTema() { return nombreTema; }
    public int getIntentosTotales() { return intentosTotales; }
    public int getAciertosTotales() { return aciertosTotales; }
    public int getPuntosTotales() { return puntosTotales; }
    public double getPorcentajeAciertos() { return porcentajeAciertos; }

    //agrega un resultado al conteo de este tema
    public void agregarResultado(Resultado res) {
        this.intentosTotales++;
        this.puntosTotales += res.getPuntos();
        if (res.isEsCorrecto()) {
            this.aciertosTotales++;
        }
    }

    //lama a esta funcion despues de agregar todos los resultados
    public void calcularPorcentaje() {
        if (this.intentosTotales == 0) {
            this.porcentajeAciertos = 0;
        } else {
            this.porcentajeAciertos = ((double)this.aciertosTotales / this.intentosTotales) * 100.0;
        }
    }
}
