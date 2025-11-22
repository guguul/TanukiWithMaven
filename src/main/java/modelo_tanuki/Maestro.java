/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
//para los reportes
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.File;
import javax.swing.JFileChooser; // para la ventana de "Guardar como..."
/**
 *
 * @author adrif
 */
public class Maestro extends Usuario{
    public static int contadorMaestro=0;

    private ArrayList<Salon> salones;
    
    public Maestro (int idUsuario, String nombre, String apellido, String correo, String contrasena){
        super(idUsuario, nombre, apellido, correo, contrasena);
        this.salones = new ArrayList<>();
    }
    
    public Maestro(){
        salones = new ArrayList<>();
    }

    public ArrayList<Salon> getSalones() {
        return salones;
    }

    public void setSalones(ArrayList<Salon> salones) {
        this.salones = salones;
    }
    
    public Salon crearSalon(int grado, char seccion, int idSalon) { 
        Salon nuevoSalon = new Salon(grado, seccion, this, idSalon);//creando un salon del profesor
        this.salones.add(nuevoSalon);
        return nuevoSalon; // lo devuelve al controlador
    }
    
    public void eliminarSalon(Salon S) {
        if ((S != null)&& (this.salones.contains(S))) {
            this.salones.remove(S);
        }
    }
    
    public void agregarEstudianteASalon(Estudiante E, Salon S) {
        if ((E != null) && (S != null) && this.salones.contains(S)) { //que el estudiante exista y que el salon sea del maestro
            if (E.getSalon() == null) {
                S.agregarEstudiante(E);
            }
        }
    }
    
    public void admitirEstudiante(Estudiante E, Salon S){
        if ((E!=null)&& S != null && this.salones.contains(S)){
            S.admitirSolicitud(E);
        }
    }
    
    public List<Progreso> obtenerProgresoSalon(Salon S) { 
        List<Progreso> progresosDelSalon = new ArrayList<>();
        //se asegura que el salon es de este maestro
        if (S != null && this.salones.contains(S)) {
            //itera sobre cada estudiante en la lista de ese salón
            for (Estudiante e : S.getListaEstudiantes()) { 
                progresosDelSalon.add(e.getProgreso());
            }
        }
        return progresosDelSalon; //devuelve la lista
    }
    
    public Progreso obtenerProgresoEstudiante(Estudiante E) {
        if (E != null) {
            return E.getProgreso();
        }
        return null;
    }
    
    public void rechazarEstudiante(Estudiante E, Salon S) {
    // 1. Se asegura que el salón es de este maestro
        if (E != null && S != null && this.salones.contains(S)) {
        
        // 2. Llama al método del Salón para que ejecute la acción
            S.rechazarSolicitud(E);
        }
    }
    
    public List<Resultado> filtrarProgresoPorPeriodo(Salon S, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Resultado> resultadosFiltrados = new ArrayList<>();
        List<Progreso> progresos = this.obtenerProgresoSalon(S);
        
        for (Progreso p : progresos) {
            // Llama al método de Progreso que filtra por fecha
            resultadosFiltrados.addAll(p.getResultados(fechaInicio, fechaFin));
        }
        return resultadosFiltrados;
    }
 
}