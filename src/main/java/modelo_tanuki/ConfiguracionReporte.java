/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
/**
 *
 * @author adrif
 */
public class ConfiguracionReporte {
    private final Salon salon;         // El salón que se está reportando (siempre requerido)
    private final Estudiante estudiante; // El estudiante (es 'null' si el reporte es de todo el salón)

    // Req 2: El período de tiempo
    private PeriodoReporte periodo;

    // Req 3: La forma de los datos
    private FormatoVisual formatoVisual;

    // Req 4: El archivo de salida
    private FormatoArchivo formatoArchivo;

    // --- Constructores (Privados) ---

    /**
     * Constructor privado. Obliga a usar los métodos estáticos
     * (paraReporteSalon / paraReporteEstudiante) para crear un objeto.
     * Esto asegura que la configuración siempre sea válida.
     */
    private ConfiguracionReporte(Salon salon, Estudiante estudiante) {
        if (salon == null) {
            throw new IllegalArgumentException("El Salón no puede ser nulo.");
        }
        this.salon = salon;
        this.estudiante = estudiante;
        
        // Asignamos valores por defecto (Default)
        this.periodo = PeriodoReporte.COMPLETO;
        this.formatoVisual = FormatoVisual.TABLA;
        this.formatoArchivo = FormatoArchivo.PDF;
    }

    // --- Métodos de Fábrica (Req 1) ---

    /**
     * Crea una configuración para un REPORTE GRUPAL (promedios, ranking, etc.)
     * @param salon El salón a reportar.
     * @return 
     */
    public static ConfiguracionReporte paraReporteSalon(Salon salon) {
        // El estudiante es 'null', indicando que es un reporte grupal
        return new ConfiguracionReporte(salon, null);
    }

    /**
     * Crea una configuración para un REPORTE INDIVIDUAL.
     * @param salon El salón al que pertenece el estudiante (para contexto).
     * @param estudiante El estudiante específico a reportar.
     * @return 
     */
    public static ConfiguracionReporte paraReporteEstudiante(Salon salon, Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El Estudiante no puede ser nulo para un reporte individual.");
        }
        return new ConfiguracionReporte(salon, estudiante);
    }

    // --- Setters Fluidos (Para las opciones 2, 3 y 4) ---
    // Devuelven 'this' para permitir "encadenar" métodos.

    /**
     * (Req 2) Define el período de tiempo del reporte.
     * @param periodo
     * @return 
     */
    public ConfiguracionReporte conPeriodo(PeriodoReporte periodo) {
        this.periodo = periodo;
        return this; // Devuelve el mismo objeto
    }

    /**
     * (Req 3) Define el formato visual (Tabla o Gráfico).
     * @param formato
     * @return 
     */
    public ConfiguracionReporte conFormatoVisual(FormatoVisual formato) {
        this.formatoVisual = formato;
        return this;
    }

    /**
     * @param archivo * @return
     * (Req 4) Define el tipo de archivo de salida (PDF o Excel).
     * @return 
     *  
     */
    public ConfiguracionReporte enArchivo(FormatoArchivo archivo) {
        this.formatoArchivo = archivo;
        return this;
    }

    // --- Getters (Para que el "Motor de Reportes" lea la configuración) ---

    public Salon getSalon() {
        return salon;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    /**
     * Método útil para saber si es un reporte individual (true)
     * o de salón (false).
     * @return 
     */
    public boolean esReporteIndividual() {
        return this.estudiante != null;
    }

    public PeriodoReporte getPeriodo() {
        return periodo;
    }

    public FormatoVisual getFormatoVisual() {
        return formatoVisual;
    }

    public FormatoArchivo getFormatoArchivo() {
        return formatoArchivo;
    }
    
}
