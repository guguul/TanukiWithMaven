/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import java.util.*;
import java.time.LocalDate;
import java.time.Duration;
/**
 *
 * @author adrif
 */
public class Estudiante extends Usuario {
    private int grado;
    private char seccion;
    private Salon salon;
    private Progreso progreso;
    private ArrayList<Logro> logros;
    
    public Estudiante (int idUsuario, String nombre, String apellido, String correo, String contrasena, int grado, char seccion, Salon salon, Progreso progreso, ArrayList<Logro> logros){
        super(idUsuario,nombre,apellido,correo,contrasena);
        this.grado = grado;
        this.seccion = seccion;
        this.salon = null; // empieza sin salon
        this.progreso = new Progreso(this); //corregir
        this.logros = new ArrayList<>();
    }
    
    public Estudiante(){
        grado = 0;
        seccion = ' ';
        salon = null;
        progreso = new Progreso(this);//corregir
        logros = new ArrayList<>();
    }

    public int getGrado() {
        return grado;
    }

    public void setGrado(int grado) {
        this.grado = grado;
    }

    public char getSeccion() {
        return seccion;
    }

    public void setSeccion(char seccion) {
        this.seccion = seccion;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public Progreso getProgreso() {
        return progreso;
    }

    public void setProgreso(Progreso progreso) {
        this.progreso = progreso;
    }

    public ArrayList getLogros() {
        return logros;
    }

    public void setLogros(ArrayList logros) {
        this.logros = logros;
    }
    
    public void unirASalon(Salon S) {
        this.setSalon(S);
    }
    
    public void eliminarSalon(Salon S) {
        if (this.salon != null && this.salon.equals(S)) {
            this.setSalon(null);
        }
    }
    
    public void agregarLogro(Logro l) {
        // evita añadir el mismo logro dos veces
        if (l != null && !this.logros.contains(l)) {
            this.logros.add(l);
        }
    }
    
    //METODO USADO PARA LLENAR LA JLIST DE SOLICITUDES
    @Override
    public String toString() {
        return this.getNombre() + " " + this.getApellido();
    }
    
}
