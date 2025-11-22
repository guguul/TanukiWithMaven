/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import java.time.LocalDate;
import java.time.Duration;


/**
 *
 * @author adrif
 */
public class Resultado {
    private Estudiante estudiante;
    private Ejercicio ejercicio;
    private LocalDate fecha;
    private int intentos;
    private Duration tiempoTardado;
    private boolean esCorrecto;
    private int puntos;
    private String respuestaUsuario;
    

    public Resultado(Estudiante estudiante, Ejercicio ejercicio, LocalDate fecha, int intentos, Duration tiempoTardado, boolean esCorrecto, int puntos,String respuestaUsuario) {
        this.estudiante = estudiante;
        this.ejercicio = ejercicio;
        this.fecha = fecha;
        this.intentos = intentos;
        this.tiempoTardado = tiempoTardado;
        this.esCorrecto = esCorrecto;
        this.puntos = puntos;
        this.respuestaUsuario=respuestaUsuario;
    }
    
    public Resultado(){
        estudiante = null;
        ejercicio = null;
        fecha = null;
        intentos= puntos = 0;
        esCorrecto = false;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public Duration getTiempoTardado() {
        return tiempoTardado;
    }

    public void setTiempoTardado(Duration tiempoTardado) {
        this.tiempoTardado = tiempoTardado;
    }

    public boolean isEsCorrecto() {
        return esCorrecto;
    }

    public void setEsCorrecto(boolean esCorrecto) {
        this.esCorrecto = esCorrecto;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }


    public String getRespuestaUsuario() {
        return respuestaUsuario;
    }

    public void setRespuestaUsuario(String respuestaUsuario) {
        this.respuestaUsuario = respuestaUsuario;
    }
    
    
    
    
    
    
}
