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
public class Salon {
    private int grado;
    private char seccion;
    private Maestro maestro;
    private ArrayList<Estudiante> listaEstudiantes;
    private int idSalon;
    private ArrayList<Estudiante> listaSolicitudes;
    
    public Salon (int grado, char seccion, Maestro maestro, int idSalon){
        this.grado = grado;
        this.seccion = seccion;
        this.maestro = maestro;
        this.idSalon = idSalon;
        this.listaEstudiantes = new ArrayList<>();
        this.listaSolicitudes = new ArrayList<>();
    }
        
    public Salon(){
        grado = idSalon = 0;
        seccion = ' ';
        maestro = null;
        listaEstudiantes = new ArrayList<>();
        listaSolicitudes = new ArrayList<>();
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

    public Maestro getMaestro() {
        return maestro;
    }

    public void setMaestro(Maestro maestro) {
        this.maestro = maestro;
    }

    public ArrayList<Estudiante> getListaEstudiantes() {
        return listaEstudiantes;
    }

    public void setListaEstudiantes(ArrayList<Estudiante> listaEstudiantes) {
        this.listaEstudiantes = listaEstudiantes;
    }

    public int getIdSalon() {
        return idSalon;
    }

    public void setIdSalon(int idSalon) {
        this.idSalon = idSalon;
    }

    public ArrayList<Estudiante> getListaSolicitudes() {
        return listaSolicitudes;
    }

    public void setListaSolicitudes(ArrayList<Estudiante> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }
    
    public void agregarEstudiante(Estudiante estudiante) {
        if (estudiante != null && !this.listaEstudiantes.contains(estudiante)) { // el estudiante no esta en la clase
            this.listaEstudiantes.add(estudiante); // lo anade
            estudiante.setSalon(this); //asigna el salon
        }
    }
    
    public void recibirSolicitud(Estudiante estudiante) {
        if ((estudiante != null) && !this.listaEstudiantes.contains(estudiante) && !this.listaSolicitudes.contains(estudiante)) { //que el estudiante no este en ninguna lista
            this.listaSolicitudes.add(estudiante); //se anade a la lista de solicitudes
        }
    }
    
    public void admitirSolicitud(Estudiante estudiante) {
        if ((estudiante != null) && this.listaSolicitudes.contains(estudiante)) {
            this.listaSolicitudes.remove(estudiante);
            this.agregarEstudiante(estudiante);
        }
    }
    
    public void rechazarSolicitud(Estudiante estudiante) {
        if (estudiante != null) {
            this.listaSolicitudes.remove(estudiante);
        }
    }
    
    public void eliminarEstudiante(Estudiante estudiante) {
        if (estudiante != null && this.listaEstudiantes.contains(estudiante)) {
            this.listaEstudiantes.remove(estudiante);
            estudiante.setSalon(null); //ahora el salon del estudiante es null
        }
    }
    
    public String getNombre() { //muestra el grado y seccion
        return this.grado + "° Grado " + this.seccion;
    }
}
