/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/**
 *
 * @author adrif
 */
public class Tema {
    private String nombre;
    private ArrayList<Tema> temasHijos;
    private String descripcion;
    private Tema temaPadre;
    private int id;
    private String nombrePersonaje;
    private String presentacionPersonaje;
    private String celebrandoPersonaje;
    private String correctoPersonaje;
    private String incorrectoPersonaje;
    /**
     * ATRIBUTO CLAVE:
     * Un "diccionario" que agrupa las listas de ejercicios
     * por su nivel de dificultad.
     * Ej: { BASICO -> [ej1, ej2, ...], INTERMEDIO -> [ej10, ej11, ...] }
     */
    private Map<NivelDificultad, List<Ejercicio>> ejerciciosPorDificultad;
    
    public Tema(int id, String nombre, Tema temaPadre,String nombrePersonaje, String presentacionPersonaje, String celebrandoPersonaje, String correctoPersonaje, String incorrectoPersonaje) {
        this.id = id;
        this.nombre = nombre;
        this.temaPadre = temaPadre;
        
        // inicializa sus propias colecciones
        this.temasHijos = new ArrayList<>();
        this.ejerciciosPorDificultad = new HashMap<>();
        
        // ¡Importante! Inicializa las listas DENTRO del mapa
        // para evitar errores de NullPointerException.
        for (NivelDificultad nivel : NivelDificultad.values()) {
            this.ejerciciosPorDificultad.put(nivel, new ArrayList<>());
        }
        this.nombrePersonaje = nombrePersonaje;
        this.presentacionPersonaje = presentacionPersonaje;
        this.celebrandoPersonaje = celebrandoPersonaje;
        this.correctoPersonaje = correctoPersonaje;
        this.incorrectoPersonaje = incorrectoPersonaje;
    }
    
    public Tema(){
        this(0, "", null,"","","","",""); //llama al constructor ppal
        
    }
    
    public void agregarTemaHijo(Tema hijo) {
        this.temasHijos.add(hijo); //agrega a array de temashijos
    }
    
   /**
     * Añade un ejercicio a este tema.
     * Este método clasifica automáticamente el ejercicio
     * en la lista de dificultad correcta.
     * (Llamado por el SistemaController al cargar la BD).
     * @param ej El ejercicio a añadir.
     */
    public void agregarEjercicio(Ejercicio ej) {
        if (ej == null) return;
        
        // 1. Obtiene la dificultad del ejercicio (ej: BASICO)
        NivelDificultad d = ej.getDificultad();
        
        // 2. Obtiene la lista correspondiente (ej: la lista de BASICO)
        // 3. Añade el ejercicio a esa lista
        this.ejerciciosPorDificultad.get(d).add(ej);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Tema> getTemasHijos() {
        return temasHijos;
    }

    public void setTemasHijos(ArrayList<Tema> temasHijos) {
        this.temasHijos = temasHijos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Map<NivelDificultad, List<Ejercicio>> getEjercicios() {
        return ejerciciosPorDificultad;
    }

    public void setEjercicios(Map<NivelDificultad, List<Ejercicio>> ejerciciosPorDificultad) {
        this.ejerciciosPorDificultad = ejerciciosPorDificultad;
    }

    public Tema getTemaPadre() {
        return temaPadre;
    }

    public void setTemaPadre(Tema temaPadre) {
        this.temaPadre = temaPadre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombrePersonaje() {
        return nombrePersonaje;
    }

    public void setNombrePersonaje(String nombrePersonaje) {
        this.nombrePersonaje = nombrePersonaje;
    }

    public String getPresentacionPersonaje() {
        return presentacionPersonaje;
    }

    public void setPresentacionPersonaje(String presentacionPersonaje) {
        this.presentacionPersonaje = presentacionPersonaje;
    }

    public String getCelebrandoPersonaje() {
        return celebrandoPersonaje;
    }

    public void setCelebrandoPersonaje(String celebrandoPersonaje) {
        this.celebrandoPersonaje = celebrandoPersonaje;
    }

    public String getCorrectoPersonaje() {
        return correctoPersonaje;
    }

    public void setCorrectoPersonaje(String correctoPersonaje) {
        this.correctoPersonaje = correctoPersonaje;
    }

    public String getIncorrectoPersonaje() {
        return incorrectoPersonaje;
    }

    public void setIncorrectoPersonaje(String incorrectoPersonaje) {
        this.incorrectoPersonaje = incorrectoPersonaje;
    }
    
  

    
    
    /**
     * MÉTODO CLAVE MODIFICADO (Lógica del 70%)
     * Ahora acepta el Nivel Y el Grupo de Grado.
     * @param d La dificultad solicitada (ej: BASICO)
     * @param gradoGrupoBuscado El grupo de grado (ej: 1 para "1ro-3ro")
     * @return Una lista de ejercicios para la práctica.
     */
    public List<Ejercicio> getPractica(NivelDificultad d, int gradoGrupoBuscado) {
        
        // 1. Obtiene el "banco" completo de ejercicios para este nivel (ej: todos los "BASICO")
        List<Ejercicio> bancoTotalPorNivel = this.ejerciciosPorDificultad.get(d);
        
        if (bancoTotalPorNivel == null || bancoTotalPorNivel.isEmpty()) {
            return new ArrayList<>(); // No hay ejercicios de este nivel
        }

        // 2. --- ¡NUEVO FILTRO! ---
        //    Crea una nueva lista solo con los ejercicios
        //    que también coinciden con el grupo de grado.
        List<Ejercicio> bancoFiltradoPorGrado = new ArrayList<>();
        for (Ejercicio ej : bancoTotalPorNivel) {
            if (ej.getGrado() == gradoGrupoBuscado) {
                bancoFiltradoPorGrado.add(ej);
            }
        }
        // (Ahora 'bancoFiltradoPorGrado' tiene, por ej,
        //  solo los ejercicios "BASICO" del grupo "1ro-3ro")

        // 3. Maneja el caso: "no se sabe cuantos hay de cual nivel"
        if (bancoFiltradoPorGrado.isEmpty()) {
            return new ArrayList<>(); // No hay ejercicios para este nivel Y este grado
        }
        
        // 4. Define la regla de cuántos ejercicios mostrar 
        int cantidadAMostrar = this.getCantidadParaNivel(d);
        
        // 5. Maneja el caso: (Si la regla pide 7 pero solo tenemos 4)
        if (bancoFiltradoPorGrado.size() <= cantidadAMostrar) {
            return new ArrayList<>(bancoFiltradoPorGrado); // Devuelve todos
        }
        
        // 6. Caso ideal: (Tenemos 20, queremos 7)
        List<Ejercicio> copiaBanco = new ArrayList<>(bancoFiltradoPorGrado);
        Collections.shuffle(copiaBanco); // Mezcla
        return copiaBanco.subList(0, cantidadAMostrar); // Devuelve los primeros 7
    }

    @Override
    public String toString() {
        return this.nombre;
    }
    
    @Override
    public boolean equals(Object o) {
        // Si es el mismo objeto en memoria, es igual
        if (this == o) return true;
        // Si no es un objeto de la clase Tema, no puede ser igual
        if (o == null || getClass() != o.getClass()) return false;
        // Compara los objetos basándose en su ID
        Tema tema = (Tema) o;
        return this.id == tema.id;
    }

    /**
     * hashCode()
     * Devuelve un "código" único para el objeto.
     * Crítico para que Map<Tema, ...> funcione.
     * @return 
     */
    @Override
    public int hashCode() {
        // Usa el ID como el código hash
        return Objects.hash(this.id);
    }
    
    /**
     * Helper que define tu regla de negocio.
     */
    private int getCantidadParaNivel(NivelDificultad d) {
        switch (d) {
            case BAJO:
                return 5; // 7 ejercicios para Bajo
            case MEDIO:
                return 5; // 7 ejercicios para medio
            case OPTIMO:
                return 5; // 7 ejercicios para optimo
            case ELEVADO:
                return 5;
            default:
                return 5; // un valor por defecto seguro
        }
    }
  
}
