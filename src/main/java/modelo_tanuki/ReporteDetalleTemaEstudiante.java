/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;

/**
 *
 * @author adrif
 */
public class ReporteDetalleTemaEstudiante {

    private String nombreTema;
    private NivelDificultad nivelActual;
    private int puntosEnPeriodo;
    private double porcentajeAciertos;
    private int intentosEnPeriodo;

    public ReporteDetalleTemaEstudiante(String nombreTema, NivelDificultad nivel, int puntos, double porc, int intentos) {
        this.nombreTema = nombreTema;
        this.nivelActual = nivel;
        this.puntosEnPeriodo = puntos;
        this.porcentajeAciertos = porc;
        this.intentosEnPeriodo = intentos;
    }

    // getters
    public String getNombreTema() { return nombreTema; }
    public NivelDificultad getNivelActual() { return nivelActual; }
    public int getPuntosEnPeriodo() { return puntosEnPeriodo; }
    public double getPorcentajeAciertos() { return porcentajeAciertos; }
    public int getIntentosEnPeriodo() { return intentosEnPeriodo; }
}

