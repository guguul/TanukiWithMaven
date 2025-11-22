/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import java.util.*;
import java.util.Arrays;
/**
 *
 * @author adrif
 */
public class Ejercicio {
    private String pregunta;
    private String[] opciones;
    private String respuestaCorrecta;
    private NivelDificultad dificultad;
    private Tema tema;
    private int valorPuntos;
    private int grado;
    private int idEjercicio;
    private String rutaImagen; // Ruta al archivo de imagen (ej: "triangulo.png"), !!!estas imagenes deben estan en la carpeta imagenes
    private String retroalimentacion;
    private TipoRespuesta tipoRespuesta;
    
    public Ejercicio(String pregunta, String[] opciones, String respuestaCorrecta, NivelDificultad dificultad, Tema tema, int valorPuntos, int grado, int idEjercicio, String rutaImagen, String retroalimentacion){
        this.pregunta = pregunta;
        if (opciones == null || opciones.length != 4) {
            throw new IllegalArgumentException("Un ejercicio debe tener exactamente 4 opciones.");
        }
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
        this.dificultad = dificultad;
        this.tema = tema;
        this.valorPuntos = valorPuntos;
        this.grado = grado;
        this.idEjercicio = idEjercicio;
        this.rutaImagen = rutaImagen;
        this.retroalimentacion = retroalimentacion;
        
    }
    
    public Ejercicio(){
        pregunta = respuestaCorrecta = "";
        opciones = new String[4];
        valorPuntos = grado = 0;
        tema = null;
        dificultad = NivelDificultad.BAJO;
        idEjercicio = 0;
        rutaImagen = null;
        retroalimentacion = null;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String[] getOpciones() {
        return opciones;
    }

    public void setOpciones(String[] opciones) {
        //Aplica la misma validación en el setter
        if (opciones == null || opciones.length != 4) {
            throw new IllegalArgumentException("Un ejercicio debe tener exactamente 4 opciones.");
        }
        this.opciones = opciones;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public NivelDificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(NivelDificultad dificultad) {
        this.dificultad = dificultad;
    }

    public Tema getTema() {
        return tema;
    }

    public void setTema(Tema tema) {
        this.tema = tema;
    }

    public int getValorPuntos() {
        return valorPuntos;
    }

    public void setValorPuntos(int valorPuntos) {
        this.valorPuntos = valorPuntos;
    }

    public int getGrado() {
        return grado;
    }

    public void setGrado(int grado) {
        this.grado = grado;
    }

    public int getIdEjercicio() {
        return idEjercicio;
    }

    public void setIdEjercicio(int idEjercicio) {
        this.idEjercicio = idEjercicio;
    }
    
    public String getRutaImagen() {
        return rutaImagen;
    }
    
    public String getRetroalimentacion() {
        return retroalimentacion;
    }
    
    public void SetRutaImagen(String rutaImagen){
        this.rutaImagen = rutaImagen;
    }
    
    public void SetRetroalimentacion(String retroalimentacion){
        this.retroalimentacion = retroalimentacion;
    }
    
    /**
     * Valida la respuesta del usuario.
     * Funciona tanto para respuesta escrita como múltiple.
     * @param respuestaUsuario La respuesta enviada por el estudiante.
     * @return true si es correcta, false si es incorrecta.
     */
    public boolean validarRespuesta(String respuestaUsuario) {
        if (respuestaUsuario == null) {
            return false;
        }
        // Compara la respuesta (ignorando mayúsculas/minúsculas)
        return this.respuestaCorrecta.equalsIgnoreCase(respuestaUsuario.trim());
    }
    
    public boolean tieneImagen() {
        return this.rutaImagen != null && !this.rutaImagen.isEmpty();
    }
    
}
