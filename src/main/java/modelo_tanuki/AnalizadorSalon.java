/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author adrif
 */
public class AnalizadorSalon {
    
    private Salon salon;

    /**
     * El analizador se crea para un salón específico.
     * @param salon
     */
    public AnalizadorSalon(Salon salon) {
        this.salon = salon;
    }

    /**
     * Método principal. Orquesta todos los cálculos
     * basándose en la configuración del período (Req 2)
     * y el tipo de reporte (Req 1).
     * @param periodo El enum (SEMANA, MES, etc.)
     * @param est El estudiante (o null si es reporte de salón)
     * @return Un objeto 'Reporte' con todos los datos calculados.
     */
    public Reporte generarDatosReporte(PeriodoReporte periodo, Estudiante est, List<Tema> listaTemas) {
    
        // --- 1. Resuelve Req 2: El Período ---
        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = calcularFechaInicio(periodo);

        // 2. Prepara el objeto Reporte que se va a devolver
        Reporte reporte = new Reporte(this.salon, fechaInicio, fechaFin);

        // 3. Obtiene los progresos de TODOS los estudiantes del salón
        List<Progreso> progresos = this.salon.getListaEstudiantes().stream()
                                        .map(Estudiante::getProgreso)
                                        .collect(Collectors.toList());

        // --- 4. Resuelve Req 1: Individual vs. Salón ---
        if (est != null) {

            // --- CASO A: REPORTE INDIVIDUAL ---
            // ¡AQUÍ ESTÁ LA CORRECCIÓN!
            // Ahora le pasamos el 4to argumento (listaTemas)
            reporte.setDatosIndividuales(est,
                calcularDatosIndividuales(est.getProgreso(), fechaInicio, fechaFin, listaTemas)
            );

        } else {
            // --- CASO B: REPORTE DE SALÓN ---
            // (Esto se queda igual, ya que 'calcularDatosDetalladosPorTema'
            //  no necesita la lista de temas, la descubre de los resultados)
            reporte.setRanking(
                calcularRanking(progresos, fechaInicio, fechaFin)
            );
            reporte.setDatosPorTema(
                calcularDatosDetalladosPorTema(progresos, fechaInicio, fechaFin)
            );
        }

        return reporte;
    }

    /**
     * (Req 1) Lógica de REPORTE INDIVIDUAL.
     * Simplemente llama a los nuevos métodos de 'Progreso'.
     */
    private ReporteDatosIndividual calcularDatosIndividuales(Progreso p, LocalDate inicio, LocalDate fin, List<Tema> listaTemas) {

        // --- 1. Cálculos Generales (Tu código original) ---
        int puntos = p.getPuntajeTotal(inicio, fin);
        double porcentajeGeneral = p.getPorcentajeAciertos(inicio, fin);
        String dificultades = p.getTemasConDificultad(inicio, fin);

        // --- 2. Prepara las nuevas colecciones ---
        Map<String, Double> promedioPorTemaInd = new HashMap<>();     // Para el gráfico
        List<ReporteDetalleTemaEstudiante> detallePorTema = new ArrayList<>(); // Para la tabla

        // --- 3. Bucle ÚNICO para calcular #2 y #3 ---
        for (Tema tema : listaTemas) {
            // Solo "temas hijos" (que tienen ejercicios)
            if (tema.getTemasHijos() == null || tema.getTemasHijos().isEmpty()) {

                // Llama a los helpers que creamos en Progreso.java
                NivelDificultad nivel = p.getNivelActual(tema);
                int puntosTema = p.getPuntajeTotalPorTema(tema, inicio, fin);
                double[] aciertosIntentos = p.getAciertosIntentosPorTema(tema, inicio, fin);

                double aciertos = aciertosIntentos[0];
                double intentos = aciertosIntentos[1];

                // Solo procesa temas que se han practicado en el período
                if (intentos > 0) {
                    double promedio = (aciertos / intentos) * 100.0;

                    // A) Añade al MAPA (para tu gráfico)
                    promedioPorTemaInd.put(tema.getNombre(), promedio);

                    // B) Añade a la LISTA (para la nueva tabla)
                    detallePorTema.add(new ReporteDetalleTemaEstudiante(
                        tema.getNombre(),
                        nivel,
                        puntosTema,
                        promedio, // Reutiliza el promedio
                        (int)intentos
                    ));
                }
            }
        }

        // --- 4. Devuelve el objeto COMPLETO ---
        return new ReporteDatosIndividual(
            puntos, 
            porcentajeGeneral, 
            dificultades, 
            promedioPorTemaInd, // <-- Tus datos para el gráfico
            detallePorTema      // <-- Los nuevos datos para la tabla
        );
    }
    
    
    /**
     * (Req 1) Lógica de RANKING DE SALÓN.
     * Obtiene el puntaje de cada estudiante y ordena la lista.
     */
    private List<RankingEntry> calcularRanking(List<Progreso> progresos, LocalDate inicio, LocalDate fin) {
        List<RankingEntry> ranking = new ArrayList<>();
        
        for (Progreso p : progresos) {
            int puntos = p.getPuntajeTotal(inicio, fin);
            ranking.add(new RankingEntry(p.getEstudiante(), puntos));
        }
        
        // Ordena la lista de mayor a menor puntaje
        ranking.sort(Comparator.comparingInt(RankingEntry::getPuntaje).reversed());
        return ranking;
    }

    private Map<String, ReporteDatosPorTema> calcularDatosDetalladosPorTema(List<Progreso> progresos, LocalDate inicio, LocalDate fin) {
    
    // mapa para guardar los objetos de datos
        Map<String, ReporteDatosPorTema> datosPorTema = new HashMap<>();

        //itera por cada estudiante del salon
        for (Progreso p : progresos) {
            // itera por cada resultado filtrado de ese estudiante
            for (Resultado res : p.getResultados(inicio, fin)) {
                Tema tema = res.getEjercicio().getTema();
                String nombreTema = tema.getNombre();

                // obtiene o crea el objeto de datos para ese tema
                ReporteDatosPorTema datosTema = datosPorTema.computeIfAbsent(
                    nombreTema, 
                    k -> new ReporteDatosPorTema(nombreTema)
                );

                // agrega el resultado al objeto
                datosTema.agregarResultado(res);
            }
        }

        // calcula los promedios finales
        for (ReporteDatosPorTema datosTema : datosPorTema.values()) {
            datosTema.calcularPorcentaje();
        }

        return datosPorTema; // devuelve el mapa de objetos detallados
    }

    /**
     * (Req 2) Método Helper para calcular fechas.
     */
    private LocalDate calcularFechaInicio(PeriodoReporte periodo) {
        switch (periodo) {
            case SEMANA:
                return LocalDate.now().minusWeeks(1);
            case MES:
                return LocalDate.now().minusMonths(1);
            case TRIMESTRE:
                return LocalDate.now().minusMonths(3);
            case COMPLETO:
            default:
                // Retorna una fecha muy antigua para incluir todo
                return LocalDate.of(2000, 1, 1); 
        }
    }
}

