/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.time.LocalDate;
/**
 *
 * @author adrif
 */
public class Progreso {
    private Estudiante estudiante;
    private ArrayList<Resultado> resultados;
    private Map<Tema, NivelDificultad> nivelesDesbloqueados;
  
    public Progreso(Estudiante e) {
        this.estudiante = e;
        this.resultados = new ArrayList<>();
        this.nivelesDesbloqueados = new HashMap<>();
    }
    
    public Progreso(){
        estudiante = null;
        resultados = new ArrayList<>();
        nivelesDesbloqueados = new HashMap<>();
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public ArrayList<Resultado> getResultados() {
        return resultados;
    }

    public void setResultados(ArrayList<Resultado> resultados) {
        this.resultados = resultados;
    }
    
    public NivelDificultad getNivelActual(Tema t) {
        return this.nivelesDesbloqueados.getOrDefault(t, NivelDificultad.BAJO);
    }
    
    public void agregarResultado(Resultado r) {
        this.resultados.add(r);
    }
    public int getPuntajeTotalPorTema(Tema t) {
        int totalTema = 0;
        for (Resultado res : this.resultados) {
            //compara si el ejercicio de este resultado es del tema 't'
            if (res.getEjercicio().getTema().equals(t)) {
                totalTema += res.getPuntos();
            }
        }
        return totalTema;
    }
    public void subirNivel(Tema t) {
        NivelDificultad nivelActual = this.getNivelActual(t);
        
        switch (nivelActual) {
            case BAJO://si el tema no esta en el map, devuelve bajo
                this.nivelesDesbloqueados.put(t, NivelDificultad.MEDIO);
                break;
            case MEDIO:
                this.nivelesDesbloqueados.put(t, NivelDificultad.OPTIMO);
                break;
            case OPTIMO:
                this.nivelesDesbloqueados.put(t, NivelDificultad.ELEVADO);
                break;
            case ELEVADO:
                this.nivelesDesbloqueados.put(t, NivelDificultad.MUYELEVADO);
                break;
            case MUYELEVADO:
                //nivel maximo no hace nada
                break;
        }
    }
        
    
    // metodos para reportes del maestro y logros
    /**
    * calcula el puntaje total en un período.
     * @param inicio
     * @param fin
     * @return 
    */
    public int getPuntajeTotal(LocalDate inicio, LocalDate fin) {
        int total = 0;
        for (Resultado r : this.getResultados(inicio, fin)) { // Usa la lista filtrada
            total += r.getPuntos();
        }
        return total;
    }
    
    /**
    * (NUEVO) Calcula el % de aciertos en un período.
     * @param inicio
     * @param fin
     * @return 
    */
    public double getPorcentajeAciertos(LocalDate inicio, LocalDate fin) {
        List<Resultado> filtrados = this.getResultados(inicio, fin);
        if (filtrados.isEmpty()) return 0.0;

        double correctos = 0;
        for (Resultado r : filtrados) {
            if (r.isEsCorrecto()) correctos++;
        }
        return (correctos / filtrados.size()) * 100.0;
    }
    
    /**
     * Identifica temas donde el estudiante tiene < 50% de aciertos.
     * resalta areas de dificultad
     * Un String con los nombres de los temas, ej: "Suma, Resta"
     * @param inicio
     * @param fin
     * @return**/
    public String getTemasConDificultad(LocalDate inicio, LocalDate fin) {
        Map<Tema, Integer> aciertos = new HashMap<>();
        Map<Tema, Integer> intentos = new HashMap<>();

        for (Resultado r : this.getResultados(inicio, fin)) { // Usa la lista filtrada
            Tema t = r.getEjercicio().getTema();
            intentos.put(t, intentos.getOrDefault(t, 0) + 1);
            if (r.isEsCorrecto()) {
                aciertos.put(t, aciertos.getOrDefault(t, 0) + 1);
            }
        }
        StringBuilder temasDificiles = new StringBuilder();
        for (Tema t : intentos.keySet()) {
            double porcentaje = ((double)aciertos.getOrDefault(t, 0) / intentos.get(t)) * 100.0;
            if (porcentaje < 50.0) {
                if (temasDificiles.length() > 0) temasDificiles.append(", ");
                temasDificiles.append(t.getNombre());
            }
        }
        return temasDificiles.length() == 0 ? "Ninguna" : temasDificiles.toString();
    }
    
    /**
    * (NUEVO) Filtra los resultados por un rango de fechas.
     * @param fechaInicio
     * @param fechaFin
     * @return 
    */
    public List<Resultado> getResultados(LocalDate fechaInicio, LocalDate fechaFin) {
       List<Resultado> filtrados = new ArrayList<>();
       for (Resultado r : this.resultados) {
           if (!r.getFecha().isBefore(fechaInicio) && !r.getFecha().isAfter(fechaFin)) {
               filtrados.add(r);
           }
       }
       return filtrados;
   }
    
   /**
     *para el panel de progreso del estudiante
     * Calcula el puntaje total en un período Y para un tema específico.
     * @param t El Tema a filtrar.
     * @param inicio La fecha de inicio del período.
     * @param fin La fecha de fin del período.
     * @return El total de puntos para ese tema en ese período.
     */
    public int getPuntajeTotalPorTema(Tema t, LocalDate inicio, LocalDate fin) {
        int totalTema = 0;
        
        // Reutiliza el método getResultados(inicio, fin) que ya existe
        // y que creamos para los reportes del maestro.
        for (Resultado r : this.getResultados(inicio, fin)) {
            if (r.getEjercicio().getTema().equals(t)) {
                totalTema += r.getPuntos();
            }
        }
        return totalTema;
    }
    
    /**
    * Suma los puntajes de TODOS los temas para el ranking.
    * @return El puntaje total general del estudiante.
    */
    public int getPuntajeTotalGeneral() {
        int puntajeTotal = 0;
        // itera sobre la lista de todos los resultados y los va sumando
        for (Resultado res : this.resultados) {
            puntajeTotal += res.getPuntos();
        }
        return puntajeTotal;
    }
    
    public int getPuntajeTotalGeneral(LocalDate inicio, LocalDate fin) { //sobrecarga
        int puntajeTotal = 0;
        //filtra por fecha
        for (Resultado res : this.getResultados(inicio, fin)) {
            puntajeTotal += res.getPuntos();
        }
        return puntajeTotal;
    }
    
    public double[] getAciertosIntentosPorTema(Tema tema, LocalDate inicio, LocalDate fin) {
        double aciertos = 0;
        double intentos = 0;
        for (Resultado res : this.getResultados(inicio, fin)) {
            if (res.getEjercicio().getTema().equals(tema)) {
                intentos++;
                if (res.isEsCorrecto()) { // O fueCorrecto()
                    aciertos++;
                }
            }
        }
        return new double[]{aciertos, intentos};
    }
    
}
