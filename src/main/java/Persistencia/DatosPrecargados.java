/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
        cargarLogrosManuales(); 
    }

    private void cargarDatosDesdeJson() {
        try {
            Gson gson = new Gson();

            String rutaTemas = "/data/temas.json"; 
            
            if (getClass().getResource(rutaTemas) == null) {
                System.err.println("❌ ERROR CRÍTICO: No se encuentra /data/temas.json");
                return;
            }

            Reader readerTemas = new InputStreamReader(
                getClass().getResourceAsStream(rutaTemas), StandardCharsets.UTF_8);
            
            Type listTypeTemas = new TypeToken<ArrayList<Tema>>(){}.getType();
            this.temas = gson.fromJson(readerTemas, listTypeTemas);
            
            Map<Integer, Tema> mapaTemas = new HashMap<>();
            for (Tema t : this.temas) {
                if (t.getEjercicios() == null) {
                     Map<NivelDificultad, List<Ejercicio>> mapaVacio = new HashMap<>();
                     for (NivelDificultad n : NivelDificultad.values()) mapaVacio.put(n, new ArrayList<>());
                     t.setEjercicios(mapaVacio);
                }
                mapaTemas.put(t.getId(), t);
            }

            for (Tema t : this.temas) {
                if (t.getTemaPadre() != null && t.getTemaPadre().getId() > 0) {
                    Tema padreReal = mapaTemas.get(t.getTemaPadre().getId());
                    if (padreReal != null) {
                        t.setTemaPadre(padreReal);
                        padreReal.agregarTemaHijo(t);
                    }
                }
            }
            
            String rutaEjercicios = "/data/ejercicios.json";
             if (getClass().getResource(rutaEjercicios) == null) {
                System.err.println("❌ ERROR CRÍTICO: No se encuentra /data/ejercicios.json");
                return;
            }
            
            Reader readerEjercicios = new InputStreamReader(
                getClass().getResourceAsStream(rutaEjercicios), StandardCharsets.UTF_8);
            
            Type listTypeEjercicios = new TypeToken<ArrayList<Ejercicio>>(){}.getType();
            List<Ejercicio> todosLosEjercicios = gson.fromJson(readerEjercicios, listTypeEjercicios);
            
            // ASIGNAR EJERCICIOS A SUS TEMAS 
            int contadorCargados = 0;
            for (Ejercicio ej : todosLosEjercicios) {
                // El ejercicio viene con un Tema "dummy" que solo tiene el ID
                // Buscamos el Tema real en el mapa.
                if (ej.getTema() != null) {
                    Tema temaReal = mapaTemas.get(ej.getTema().getId());
                    if (temaReal != null) {
                        ej.setTema(temaReal); // enlazamos el objeto Tema real
                        temaReal.agregarEjercicio(ej); // esto lo mete en la lista de dificultad
                        contadorCargados++;
                    }
                }
            }
            
            System.out.println("✅ SISTEMA CARGADO: " + temas.size() + " Temas y " + contadorCargados + " Ejercicios listos.");

        } catch (Exception e) {
            System.err.println("❌ Error fatal cargando JSONs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarLogrosManuales() {
        //faltan los logros
    }

    // Getters
    public List<Tema> getTemas() { return temas; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Logro> getLogros() { return logros; }
    public List<Salon> getSalones() { return salones; }
}