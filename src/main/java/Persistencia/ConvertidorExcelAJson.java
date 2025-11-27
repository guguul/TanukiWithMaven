/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// IMPORTA TUS CLASES
import modelo_tanuki.Ejercicio;
import modelo_tanuki.NivelDificultad;
import modelo_tanuki.Tema;
/**
 *
 * @author adrif
 */
/**
 * Convertidor AUTOMÁTICO de Excel a JSON.
 * Actualizado para leer las rutas de imágenes de personajes en Temas.
 */
public class ConvertidorExcelAJson {
    
    public static void main(String[] args) {
        // --- RUTAS DE TUS ARCHIVOS EXCEL ---
        String rutaExcelEjercicios = "C:\\Users\\adrif\\Videos\\ING UCAB\\03 semestre\\POO\\proyecto\\ejerciciosdefinitivo.xlsx";
    
        // Ojo: Aquí en tu mensaje pusiste una 'D' al final del nombre (PersonajesD), verifica si el archivo se llama así
        String rutaExcelTemas = "C:\\Users\\adrif\\Videos\\ING UCAB\\03 semestre\\POO\\proyecto\\TemasCOILconPersonajesD.xlsx";
        try {
            // 1. CONVERTIR TEMAS
            System.out.println("--- PROCESANDO TEMAS ---");
            List<Tema> listaTemas = leerTemasDeExcel(rutaExcelTemas);
            guardarJson(listaTemas, "temas.json");
            System.out.println("✅ temas.json creado exitosamente (con rutas de personajes).");

            // 2. CONVERTIR EJERCICIOS
            System.out.println("\n--- PROCESANDO EJERCICIOS ---");
            List<Ejercicio> listaEjercicios = leerEjerciciosDeExcel(rutaExcelEjercicios);
            guardarJson(listaEjercicios, "ejercicios.json");
            System.out.println("✅ ejercicios.json creado exitosamente.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    //      LÓGICA PARA LEER TEMAS
    // ==========================================
    private static List<Tema> leerTemasDeExcel(String ruta) throws IOException {
        List<Tema> lista = new ArrayList<>();
        File file = new File(ruta);
        if (!file.exists()) {
            System.err.println("❌ ERROR: No se encontró el archivo " + ruta);
            return lista;
        }

        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0); 

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                // SEGÚN TU EXCEL "TemasCOILconPersonajes.xlsx":
                // 0: idTema
                // 1: Nombre
                // 2: idTemaPadre
                // 3: Descripcion
                // 4: personajeNombre
                // 5: rutaPresentacion (NUEVO)
                // 6: rutaCelebrando (NUEVO)
                // 7: rutaCorrecto (NUEVO)
                // 8: rutaIncorrecto (NUEVO)

                int id = (int) obtenerNumero(row.getCell(0));
                String nombre = obtenerTexto(row.getCell(1));
                
                // Tema Padre
                int idPadre = (int) obtenerNumero(row.getCell(2));
                Tema temaPadre = null;
                if (idPadre > 0) {
                    temaPadre = new Tema();
                    temaPadre.setId(idPadre);
                }

                String descripcion = obtenerTexto(row.getCell(3));
                String personajeNombre = obtenerTexto(row.getCell(4));
                
                // --- LECTURA DE LAS RUTAS DE IMAGENES (NUEVO) ---
                String rutaPres = obtenerTexto(row.getCell(5));
                String rutaCel = obtenerTexto(row.getCell(6));
                String rutaCorr = obtenerTexto(row.getCell(7));
                String rutaIncorr = obtenerTexto(row.getCell(8));

                // --- CREAR OBJETO TEMA ---
                Tema tema = new Tema();
                tema.setId(id);
                tema.setNombre(nombre);
                tema.setTemaPadre(temaPadre);
                tema.setDescripcion(descripcion);
                tema.setPersonajeNombre(personajeNombre);
                
                // Asignar las rutas nuevas
                tema.setPersonajeRutaImagenPresentacion(rutaPres);
                tema.setPersonajeRutaImagenCelebrando(rutaCel);
                tema.setPersonajeRutaImagenCorrecto(rutaCorr);
                tema.setPersonajeRutaImagenIncorrecto(rutaIncorr);

                lista.add(tema);

            } catch (Exception e) {
                System.err.println("Error leyendo tema fila " + i + ": " + e.getMessage());
            }
        }
        workbook.close();
        fis.close();
        return lista;
    }

    // ==========================================
    //      LÓGICA PARA LEER EJERCICIOS
    // ==========================================
    private static List<Ejercicio> leerEjerciciosDeExcel(String ruta) throws IOException {
        List<Ejercicio> lista = new ArrayList<>();
        File file = new File(ruta);
        if (!file.exists()) {
            System.err.println("❌ ERROR: No se encontró el archivo " + ruta);
            return lista;
        }

        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0); 

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                // SEGÚN TU EXCEL "ejerciciosdefinitivo.xlsx":
                // 0: Id tema hijo
                // 1: idEjercicio
                // 2: Ejercicio
                // 3-6: Opciones A-D
                // 7: Respuesta correcta
                // 8: Dificultad
                // 9: Grado
                // 10: Puntaje
                // 11: RutaImagenFigura
                // 12: Retroalimentación
                // 13: ForzarSeleccion
                // 14: EsOpcionImagen

                int idTema = (int) obtenerNumero(row.getCell(0));
                int idEjercicio = (int) obtenerNumero(row.getCell(1));
                String pregunta = obtenerTexto(row.getCell(2));
                
                String opA = obtenerTexto(row.getCell(3));
                String opB = obtenerTexto(row.getCell(4));
                String opC = obtenerTexto(row.getCell(5));
                String opD = obtenerTexto(row.getCell(6));
                String[] opcionesRaw = {opA, opB, opC, opD};
                
                String respCorrecta = obtenerTexto(row.getCell(7));
                
                String nivelStr = obtenerTexto(row.getCell(8));
                NivelDificultad nivel = NivelDificultad.BAJO; 
                try {
                    if (!nivelStr.isEmpty()) nivel = NivelDificultad.valueOf(nivelStr.toUpperCase().trim());
                } catch (IllegalArgumentException ex) {
                    System.err.println("Advertencia Fila " + i + ": Nivel '" + nivelStr + "' no válido.");
                }

                int grado = (int) obtenerNumero(row.getCell(9));
                int puntaje = (int) obtenerNumero(row.getCell(10));
                String rutaImg = obtenerTexto(row.getCell(11));
                String retro = obtenerTexto(row.getCell(12));
                
                String forzarStr = obtenerTexto(row.getCell(13));
                boolean forzar = forzarStr.equals("1") || forzarStr.equalsIgnoreCase("SI");

                String esImgOpStr = obtenerTexto(row.getCell(14));
                boolean esImgOpcion = esImgOpStr.equals("1") || esImgOpStr.equalsIgnoreCase("SI");

                // --- CREAR OBJETO EJERCICIO ---
                Ejercicio ej = new Ejercicio();
                ej.setIdEjercicio(idEjercicio);
                ej.setPregunta(pregunta);
                ej.setRespuestaCorrecta(respCorrecta);
                ej.setDificultad(nivel);
                ej.setGrado(grado);
                ej.setValorPuntos(puntaje);
                ej.setRutaImagen(rutaImg);
                ej.setRetroalimentacion(retro);
                ej.setForzarSeleccion(forzar);

                // Asignar Tema ID Dummy
                Tema temaDummy = new Tema();
                temaDummy.setId(idTema); 
                ej.setTema(temaDummy);

                if (esImgOpcion) {
                    ej.setRutasOpciones(opcionesRaw);
                    ej.setOpciones(new String[]{"Img A", "Img B", "Img C", "Img D"});
                } else {
                    ej.setOpciones(opcionesRaw);
                }

                lista.add(ej);
                
            } catch (Exception e) {
                System.err.println("Error leyendo ejercicio fila " + i + ": " + e.getMessage());
            }
        }
        workbook.close();
        fis.close();
        return lista;
    }

    // ==========================================
    //      HELPERS
    // ==========================================
    private static String obtenerTexto(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((int)cell.getNumericCellValue());
            case BOOLEAN: return cell.getBooleanCellValue() ? "1" : "0";
            default: return "";
        }
    }

    private static double obtenerNumero(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                String text = cell.getStringCellValue().trim();
                if (text.isEmpty()) return 0;
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private static void guardarJson(List<?> lista, String nombreArchivo) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(nombreArchivo), 
                StandardCharsets.UTF_8)) { // UTF-8 para Emojis
            gson.toJson(lista, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}