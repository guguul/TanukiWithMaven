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
public class Logro {
    private String nombre;
    private String descripcion;
    private int puntosNecesarios;
    private Tema tema;
    private int id;
    private String rutaIcono;
    
    public Logro(String nombre, String descripcion, int puntosNecesarios, Tema tema, int id, String rutaIcono){
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosNecesarios = puntosNecesarios;
        this.tema = tema;
        this.id = id;
        this.rutaIcono = rutaIcono;
    }
    
    public Logro(){
        nombre = descripcion = "";
        puntosNecesarios = id = 0;
        tema = null;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPuntosNecesarios() {
        return puntosNecesarios;
    }

    public void setPuntosNecesarios(int puntosNecesarios) {
        this.puntosNecesarios = puntosNecesarios;
    }

    public Tema getTema() {
        return tema;
    }

    public void setTema(Tema tema) {
        this.tema = tema;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getRutaIcono() {
        return this.rutaIcono;
    }
    
    public void setRutaIcono(String rutaIcono){
        this.rutaIcono = rutaIcono;
    }
    
}
