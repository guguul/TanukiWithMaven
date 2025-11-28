//datos precargados
package Persistencia;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo_tanuki.*;

public class DatosPrecargados {

    private List<Usuario> usuarios;
    private List<Tema> temas;
    private List<Logro> logros;
    private List<Salon> salones;

    public DatosPrecargados() {
        this.usuarios = new ArrayList<>();
        this.temas = new ArrayList<>();
        this.logros = new ArrayList<>();
        this.salones = new ArrayList<>();
        
       
        cargarDatosDesdeJson(); 
        
    }

    private void cargarDatosDesdeJson() {
        try {
            Gson gson = new Gson();

            // ==========================================
            //           A. CARGAR TEMAS
            // ==========================================
            String rutaTemas = "/data/temas.json"; 
            
            if (getClass().getResource(rutaTemas) == null) {
                System.err.println("❌ ERROR CRÍTICO: No se encuentra " + rutaTemas);
                return;
            }

            Reader readerTemas = new InputStreamReader(
                getClass().getResourceAsStream(rutaTemas), StandardCharsets.UTF_8);
            
            Type listTypeTemas = new TypeToken<ArrayList<Tema>>(){}.getType();
            this.temas = gson.fromJson(readerTemas, listTypeTemas);
            
            // --- MAPEO DE TEMAS (Para búsquedas rápidas por ID) ---
            Map<Integer, Tema> mapaTemas = new HashMap<>();
            for (Tema t : this.temas) {
                // Inicializar mapas internos si vienen nulos
                if (t.getEjercicios() == null) {
                      Map<NivelDificultad, List<Ejercicio>> mapaVacio = new HashMap<>();
                      for (NivelDificultad n : NivelDificultad.values()) mapaVacio.put(n, new ArrayList<>());
                      t.setEjercicios(mapaVacio);
                }
                mapaTemas.put(t.getId(), t);
            }

            // --- RECONSTRUIR JERARQUÍA (Padres e Hijos) ---
            for (Tema t : this.temas) {
                if (t.getTemaPadre() != null && t.getTemaPadre().getId() > 0) {
                    Tema padreReal = mapaTemas.get(t.getTemaPadre().getId());
                    if (padreReal != null) {
                        t.setTemaPadre(padreReal);
                        padreReal.agregarTemaHijo(t);
                    }
                }
            }

            // ==========================================
            //           B. CARGAR EJERCICIOS
            // ==========================================
            String rutaEjercicios = "/data/ejercicios.json";
             if (getClass().getResource(rutaEjercicios) == null) {
                System.err.println("❌ ERROR CRÍTICO: No se encuentra " + rutaEjercicios);
                // No retornamos, intentamos cargar logros al menos
            } else {
                Reader readerEjercicios = new InputStreamReader(
                    getClass().getResourceAsStream(rutaEjercicios), StandardCharsets.UTF_8);
                
                Type listTypeEjercicios = new TypeToken<ArrayList<Ejercicio>>(){}.getType();
                List<Ejercicio> todosLosEjercicios = gson.fromJson(readerEjercicios, listTypeEjercicios);
                
                // --- ASIGNAR EJERCICIOS A SUS TEMAS ---
                int contadorEjer = 0;
                for (Ejercicio ej : todosLosEjercicios) {
                    if (ej.getTema() != null) {
                        Tema temaReal = mapaTemas.get(ej.getTema().getId());
                        if (temaReal != null) {
                            ej.setTema(temaReal); 
                            temaReal.agregarEjercicio(ej); 
                            contadorEjer++;
                        }
                    }
                }
                System.out.println("✅ Ejercicios cargados: " + contadorEjer);
            }
            
            // ==========================================
            //           C. CARGAR LOGROS 
            // ==========================================
            String rutaLogros = "/data/logros.json";
            
            if (getClass().getResource(rutaLogros) != null) {
                Reader readerLogros = new InputStreamReader(
                    getClass().getResourceAsStream(rutaLogros), StandardCharsets.UTF_8);
                
                Type listTypeLogros = new TypeToken<ArrayList<Logro>>(){}.getType();
                List<Logro> listaLogrosTemp = gson.fromJson(readerLogros, listTypeLogros);
                
                // --- VINCULAR LOGROS CON TEMAS REALES ---
                for (Logro l : listaLogrosTemp) {
                    // El logro viene con idTema (int). Buscamos el objeto Tema real.
                    Tema temaAsociado = mapaTemas.get(l.getIdTema());
                    
                    if (temaAsociado != null) {
                        l.setTema(temaAsociado); // ¡Conexión vital para el juego!
                    } else {
                        // System.err.println("⚠️ Advertencia: Logro '" + l.getNombre() + "' apunta a tema ID " + l.getIdTema() + " que no existe.");
                    }
                    this.logros.add(l);
                }
                System.out.println("✅ Logros cargados: " + this.logros.size());
                
            } else {
                System.err.println("⚠️ Advertencia: No se encontró " + rutaLogros + ". Cargando manuales...");
                cargarLogrosManuales(); // Fallback por si acaso
            }
            
            System.out.println("✅ SISTEMA CARGADO COMPLETAMENTE: " + temas.size() + " Temas activos.");

        } catch (Exception e) {
            System.err.println("❌ Error fatal cargando JSONs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarLogrosManuales() {
        // de emergencia por si falla el JSON
    }

    // Getters
    public List<Tema> getTemas() { return temas; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Logro> getLogros() { return logros; }
    public List<Salon> getSalones() { return salones; }
}