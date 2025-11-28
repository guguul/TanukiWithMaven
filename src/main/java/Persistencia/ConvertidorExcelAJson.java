/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Persistencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import modelo_tanuki.Ejercicio;
import modelo_tanuki.NivelDificultad;
import modelo_tanuki.Tema;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ConvertidorExcelAJson {
    public static void main(String[] args) {
   // Si los mueves, tienes que cambiar esto.
        String rutaBase = "C:/Users/adrif/Videos/ING UCAB/03 semestre/POO/proyecto/";
        
        // Nombres de los archivos
        String archivoTemas = rutaBase + "TemasCOILconPersonajes.xlsx";
        String archivoEjercicios = rutaBase + "ejerciciosdefinitivo.xlsx";
        
        // Rutas de salida (Donde se guardarán los JSON dentro de tu proyecto NetBeans)
        // Nota: Esto asume que estás ejecutando el script dentro del proyecto en NetBeans
        String salidaTemas = "src/main/resources/data/temas.json";
        String salidaEjercicios = "src/main/resources/data/ejercicios.json";

        try {
            System.out.println("=== INICIANDO CONVERSIÓN ===");
            
            // Debug: Mostrar dónde estamos buscando
            System.out.println("Buscando excels en: " + rutaBase);

            // 1. PROCESAR TEMAS
            System.out.println("\n>> Procesando Temas...");
            File fTemas = new File(archivoTemas);
            if (!fTemas.exists()) {
                System.err.println("❌ ERROR: No encuentro el archivo: " + archivoTemas);
                return; // Detener si no encuentra el archivo
            }
            List<Tema> listaTemas = leerTemasDeExcel(archivoTemas);
            guardarJson(listaTemas, salidaTemas);
            
            // 2. PROCESAR EJERCICIOS
            System.out.println("\n>> Procesando Ejercicios...");
            File fEjercicios = new File(archivoEjercicios);
            if (!fEjercicios.exists()) {
                System.err.println("❌ ERROR: No encuentro el archivo: " + archivoEjercicios);
                return;
            }
            List<Ejercicio> listaEjercicios = leerEjerciciosDeExcel(archivoEjercicios);
            guardarJson(listaEjercicios, salidaEjercicios);

            System.out.println("\n✅ ¡CONVERSIÓN EXITOSA! JSONs generados.");

        } catch (Exception e) {
            System.err.println("❌ ERROR FATAL:");
            e.printStackTrace();
        }
    }

    // ==========================================
    //           MÉTODOS PARA TEMAS
    // ==========================================
    private static List<Tema> leerTemasDeExcel(String ruta) throws IOException {
        List<Tema> lista = new ArrayList<>();
        FileInputStream fis = new FileInputStream(new File(ruta));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                int id = (int) row.getCell(0).getNumericCellValue();
                String nombre = getValorCelda(row.getCell(1));
                
                // Manejo ID Padre
                Tema temaPadreDummy = null;
                Cell celdaPadre = row.getCell(2);
                if (celdaPadre != null && celdaPadre.getCellType() == CellType.NUMERIC) {
                    int idPadre = (int) celdaPadre.getNumericCellValue();
                    temaPadreDummy = new Tema();
                    temaPadreDummy.setId(idPadre);
                }

                String descripcion = getValorCelda(row.getCell(3));
                String persNombre = getValorCelda(row.getCell(4));
                String imgPres = getValorCelda(row.getCell(5));
                String imgCeleb = getValorCelda(row.getCell(6));
                String imgCorr = getValorCelda(row.getCell(7));
                String imgIncorr = getValorCelda(row.getCell(8));

                Tema t = new Tema(id, nombre, temaPadreDummy, persNombre, imgPres, imgCeleb, imgCorr, imgIncorr);
                t.setDescripcion(descripcion);
                lista.add(t);

            } catch (Exception e) {
                System.err.println("  [!] Error en Tema fila " + (i + 1));
            }
        }
        workbook.close();
        return lista;
    }

    // ==========================================
    //           MÉTODOS PARA EJERCICIOS
    // ==========================================
    private static List<Ejercicio> leerEjerciciosDeExcel(String ruta) throws IOException {
        List<Ejercicio> lista = new ArrayList<>();
        FileInputStream fis = new FileInputStream(new File(ruta));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                // Mapeo columnas
                int idTema = (int) row.getCell(0).getNumericCellValue();
                int idEjercicio = (int) row.getCell(1).getNumericCellValue();
                String pregunta = getValorCelda(row.getCell(2));
                
                String[] opcionesRaw = new String[4];
                opcionesRaw[0] = getValorCelda(row.getCell(3));
                opcionesRaw[1] = getValorCelda(row.getCell(4));
                opcionesRaw[2] = getValorCelda(row.getCell(5));
                opcionesRaw[3] = getValorCelda(row.getCell(6));
                
                String respCorrecta = getValorCelda(row.getCell(7));
                
                String difStr = getValorCelda(row.getCell(8));
                NivelDificultad dificultad = NivelDificultad.BAJO;
                try { dificultad = NivelDificultad.valueOf(difStr.toUpperCase()); } catch (Exception e) {}
                
                int grado = (int) row.getCell(9).getNumericCellValue();
                int puntos = (int) row.getCell(10).getNumericCellValue();
                String rutaImg = getValorCelda(row.getCell(11));
                String retro = getValorCelda(row.getCell(12));
                
                // Booleanos
                boolean forzarSeleccion = esCeldaTrue(row.getCell(13));
                boolean opcionesSonImagenes = esCeldaTrue(row.getCell(14));
                boolean imagenEnPregunta = esCeldaTrue(row.getCell(15));
                
                Ejercicio ej = new Ejercicio();
                ej.setIdEjercicio(idEjercicio);
                ej.setPregunta(pregunta);
                ej.setRespuestaCorrecta(respCorrecta);
                ej.setDificultad(dificultad);
                ej.setGrado(grado);
                ej.setValorPuntos(puntos);
                ej.setRutaImagen(rutaImg);
                ej.setRetroalimentacion(retro);
                ej.setForzarSeleccion(forzarSeleccion);
                ej.setImagenEnPregunta(imagenEnPregunta);

                if (opcionesSonImagenes) {
                    ej.setRutasOpciones(opcionesRaw);
                    ej.setOpciones(new String[]{"(Img)", "(Img)", "(Img)", "(Img)"});
                } else {
                    ej.setOpciones(opcionesRaw);
                    ej.setRutasOpciones(null); 
                }

                Tema tDummy = new Tema();
                tDummy.setId(idTema);
                ej.setTema(tDummy);

                lista.add(ej);

            } catch (Exception e) {
                 // System.err.println("  [!] Error Ejercicio Fila " + (i+1));
            }
        }
        workbook.close();
        System.out.println("  > Total ejercicios leídos: " + lista.size());
        return lista;
    }

    // ==========================================
    //           UTILIDADES
    // ==========================================
    private static String getValorCelda(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: 
                if (cell.getNumericCellValue() % 1 == 0) return String.valueOf((int) cell.getNumericCellValue());
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }

    private static boolean esCeldaTrue(Cell cell) {
        if (cell == null) return false;
        if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue() == 1;
        if (cell.getCellType() == CellType.STRING) {
            String val = cell.getStringCellValue().trim().toUpperCase();
            return val.equals("1") || val.equals("SI") || val.equals("TRUE");
        }
        return false;
    }

    private static void guardarJson(List<?> lista, String ruta) {
        new File(ruta).getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(ruta)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(lista, writer);
            System.out.println("  > Archivo guardado: " + ruta);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}