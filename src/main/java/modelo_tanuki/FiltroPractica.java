/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;

/**
 *
 * @author adrif
 */
import java.util.List;
import java.util.ArrayList;

public class FiltroPractica {
    
    // Este filtro es simple, solo necesita la lista maestra de temas para iniciar,
    // o puede operar directamente con el objeto Tema que recibe.

    public FiltroPractica() {
        // No necesita inicialización compleja.
    }

    /**
     * Obtiene una sub-lista aleatoria de Ejercicios del tema que coincidan con el nivel y grado.
     * (Llama directamente al método getPractica de la clase Tema).
     * @param temaSeleccionado
     * @param nivelElegido
     * @param gradoGrupo
     * @return 
     */
    public List<Ejercicio> getEjerciciosFiltrados(Tema temaSeleccionado, NivelDificultad nivelElegido, int gradoGrupo) {
        
        if (temaSeleccionado == null) {
            return new ArrayList<>();
        }
        return temaSeleccionado.getPractica(nivelElegido, gradoGrupo);
    }
}
