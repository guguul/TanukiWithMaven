/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;

/**
 *
 * @author adrif
 */
public class RankingEntry {
    private Estudiante estudiante;
    private int puntaje;
    
    public RankingEntry(Estudiante e, int p) {
        this.estudiante = e;
        this.puntaje = p;
    }
    public Estudiante getEstudiante() { return estudiante; }
    public int getPuntaje() { return puntaje; }
}
