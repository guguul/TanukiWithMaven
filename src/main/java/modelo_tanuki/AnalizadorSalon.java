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

    public AnalizadorSalon(Salon salon) {
        this.salon = salon;
    }

    public Reporte generarDatosReporte(PeriodoReporte periodo, Estudiante est, List<Tema> listaTemas) {
    
        // 1. Definir fechas
        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = calcularFechaInicio(periodo);

        // 2. Crear objeto Reporte
        Reporte reporte = new Reporte(this.salon, fechaInicio, fechaFin);

        // 3. Obtener progresos de TODOS los estudiantes
        List<Progreso> progresos = this.salon.getListaEstudiantes().stream()
                                             .map(Estudiante::getProgreso)
                                             .collect(Collectors.toList());

        // 4. Lógica Individual vs Salón
        if (est != null) {
            // REPORTE INDIVIDUAL (Usa la lista de temas para mostrar todo)
            reporte.setDatosIndividuales(est,
                calcularDatosIndividuales(est.getProgreso(), fechaInicio, fechaFin, listaTemas)
            );
        } else {
            // REPORTE DE SALÓN
            // a) Ranking
            reporte.setRanking(
                calcularRanking(progresos, fechaInicio, fechaFin)
            );
            
            // b) Datos por Tema (Solo lo practicado)
            reporte.setDatosPorTema(
                calcularDatosDetalladosPorTema(progresos, fechaInicio, fechaFin)
            );
        }

        return reporte;
    }

    /**
     * REPORTE INDIVIDUAL (Sin cambios, usa la lista para mostrar todo)
     */
    private ReporteDatosIndividual calcularDatosIndividuales(Progreso p, LocalDate inicio, LocalDate fin, List<Tema> listaTemas) {
        int puntos = p.getPuntajeTotal(inicio, fin);
        double porcentajeGeneral = p.getPorcentajeAciertos(inicio, fin);
        String dificultades = p.getTemasConDificultad(inicio, fin);

        Map<String, Double> promedioPorTemaInd = new HashMap<>();     
        List<ReporteDetalleTemaEstudiante> detallePorTema = new ArrayList<>(); 

        // Aplanamos temas para buscar en subcarpetas
        List<Tema> todosLosTemas = new ArrayList<>();
        recopilarTemasHojas(listaTemas, todosLosTemas);

        for (Tema tema : todosLosTemas) {
            NivelDificultad nivel = p.getNivelActual(tema);
            int puntosTema = p.getPuntajeTotalPorTema(tema, inicio, fin);
            double[] aciertosIntentos = p.getAciertosIntentosPorTema(tema, inicio, fin);

            double aciertos = aciertosIntentos[0];
            double intentos = aciertosIntentos[1];

            if (intentos > 0) {
                double promedio = (aciertos / intentos) * 100.0;
                promedioPorTemaInd.put(tema.getNombre(), promedio);

                detallePorTema.add(new ReporteDetalleTemaEstudiante(
                    tema.getNombre(),
                    nivel != null ? nivel : NivelDificultad.BAJO,
                    puntosTema,
                    promedio, 
                    (int)intentos
                ));
            }
        }
        return new ReporteDatosIndividual(puntos, porcentajeGeneral, dificultades, promedioPorTemaInd, detallePorTema);
    }
    
    /**
     * REPORTE DE SALÓN: DATOS POR TEMA
     * Lógica: Solo mostramos lo que los estudiantes hayan practicado.
     */
    private Map<String, ReporteDatosPorTema> calcularDatosDetalladosPorTema(List<Progreso> progresos, LocalDate inicio, LocalDate fin) {
        
        Map<String, ReporteDatosPorTema> datosPorTema = new HashMap<>();

        // 1. Recorremos TODOS los estudiantes del salón
        for (Progreso p : progresos) {
            
            // 2. Obtenemos los RESULTADOS de ese estudiante en el rango de fechas
            List<Resultado> resultados = p.getResultados(inicio, fin);
            
            // Debug: Si esto imprime 0, es que el filtro de fechas está muy estricto
            // System.out.println("Estudiante: " + p.getEstudiante().getNombre() + " - Resultados encontrados: " + resultados.size());

            // 3. Recorremos cada resultado
            for (Resultado res : resultados) {
                
                // Obtenemos el tema DIRECTO del ejercicio (ej: Suma, Resta)
                Tema tema = res.getEjercicio().getTema();
                String nombreTema = tema.getNombre();

                // 4. Buscamos si ya existe la fila en el reporte, si no, la creamos
                ReporteDatosPorTema datosTema = datosPorTema.computeIfAbsent(
                    nombreTema, 
                    k -> new ReporteDatosPorTema(nombreTema)
                );

                // 5. Sumamos los datos de este resultado al acumulado del salón
                datosTema.agregarResultado(res);
            }
        }

        // 6. Calculamos los promedios finales
        for (ReporteDatosPorTema datosTema : datosPorTema.values()) {
            datosTema.calcularPorcentaje();
        }

        return datosPorTema;
    }

    /**
     * Recursividad para reporte individual
     */
    private void recopilarTemasHojas(List<Tema> entrada, List<Tema> salida) {
        if (entrada == null) return;
        for (Tema t : entrada) {
            if (t.getTemasHijos() != null && !t.getTemasHijos().isEmpty()) {
                recopilarTemasHojas(t.getTemasHijos(), salida);
            } else {
                salida.add(t);
            }
        }
    }

    private List<RankingEntry> calcularRanking(List<Progreso> progresos, LocalDate inicio, LocalDate fin) {
        List<RankingEntry> ranking = new ArrayList<>();
        for (Progreso p : progresos) {
            int puntos = p.getPuntajeTotal(inicio, fin);
            ranking.add(new RankingEntry(p.getEstudiante(), puntos));
        }
        ranking.sort(Comparator.comparingInt(RankingEntry::getPuntaje).reversed());
        return ranking;
    }

    private LocalDate calcularFechaInicio(PeriodoReporte periodo) {
        switch (periodo) {
            case SEMANA: return LocalDate.now().minusWeeks(1);
            case MES: return LocalDate.now().minusMonths(1);
            case TRIMESTRE: return LocalDate.now().minusMonths(3);
            case COMPLETO: default: return LocalDate.of(2000, 1, 1); 
        }
    }
}

