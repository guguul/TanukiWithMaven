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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import modelo_tanuki.Ejercicio;
import modelo_tanuki.NivelDificultad;
import modelo_tanuki.Tema;

/**
 *
 * @author adrif
 */
public class ConvertidorExcelAJson {
    
    public static void main(String[] args) {
        // CAMBIA ESTO POR LA RUTA REAL DE TU ARCHIVO EXCEL EN TU PC
        String rutaExcel = "C:/MisDocumentos/Proyecto/datos_tanuki.xlsx";
        
        try {
            System.out.println("Leyendo Excel...");
            List<Ejercicio> listaEjercicios = leerEjerciciosDeExcel(rutaExcel);
            
            System.out.println("Generando JSON...");
            guardarJson(listaEjercicios, "ejercicios.json");
            
            System.out.println("¡ÉXITO! Archivo ejercicios.json creado.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Ejercicio> leerEjerciciosDeExcel(String ruta) throws IOException {
        List<Ejercicio> lista = new ArrayList<>();
        FileInputStream fis = new FileInputStream(new File(ruta));
        Workbook workbook = new XSSFWorkbook(fis);
        
        // ASUMIMOS QUE LA HOJA 0 ES "EJERCICIOS"
        Sheet sheet = workbook.getSheetAt(0); 

        // Iterar filas (empezamos en 1 para saltar los encabezados)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                // --- LECTURA DE CELDAS ---
                // Nota: Asegúrate que el orden coincida con tus columnas
                
                // Col 0: ID (int)
                int id = (int) row.getCell(0).getNumericCellValue();
                
                // Col 1: Pregunta (String)
                String pregunta = getValorCelda(row.getCell(1));
                
                // Col 2-5: Opciones
                String opA = getValorCelda(row.getCell(2));
                String opB = getValorCelda(row.getCell(3));
                String opC = getValorCelda(row.getCell(4));
                String opD = getValorCelda(row.getCell(5));
                String[] opcionesRaw = {opA, opB, opC, opD};
                
                // Col 6: Respuesta Correcta
                String respCorrecta = getValorCelda(row.getCell(6));
                
                // Col 7: Nivel (String -> Enum)
                String nivelStr = getValorCelda(row.getCell(7));
                NivelDificultad nivel = NivelDificultad.valueOf(nivelStr.toUpperCase());
                
                // Col 8: Tema (Aquí guardamos el String temporalmente o el ID)
                // Nota: Al cargar el JSON en la app real, tendrás que enlazar 
                // este String con el objeto Tema real.
                String nombreTema = getValorCelda(row.getCell(8));
                
                // Col 9: Grado
                int grado = (int) row.getCell(9).getNumericCellValue();
                
                String imgPregunta = getValorCelda(row.getCell(10));
                
                String retro = getValorCelda(row.getCell(11));
                
                String esImgOpcion = getValorCelda(row.getCell(12));
                boolean opcionesSonImagenes = esImgOpcion.equalsIgnoreCase("SI") || esImgOpcion.equals("1");

                String forzarSel = getValorCelda(row.getCell(13));
                boolean forzar = forzarSel.equalsIgnoreCase("SI") || forzarSel.equals("1");

                Ejercicio ej = new Ejercicio();
                ej.setIdEjercicio(id);
                ej.setPregunta(pregunta);
                ej.setRespuestaCorrecta(respCorrecta);
                ej.setDificultad(nivel);
                ej.setGrado(grado);
                ej.setRutaImagen(imgPregunta);
                ej.setRetroalimentacion(retro);
                ej.setForzarSeleccion(forzar);

                if (opcionesSonImagenes) {
                    // Si son imágenes, usamos setRutasOpciones
                    ej.setRutasOpciones(opcionesRaw);
                    // Llenamos opciones de texto con vacíos o placeholders para evitar nulls
                    ej.setOpciones(new String[]{"Img A", "Img B", "Img C", "Img D"}); 
                } else {
                    // Si es texto normal
                    ej.setOpciones(opcionesRaw);
                }
              
                lista.add(ej);
                
            } catch (Exception e) {
                System.err.println("Error en fila " + i + ": " + e.getMessage());
            }
        }
        
        workbook.close();
        fis.close();
        return lista;
    }

    private static String getValorCelda(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((int)cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }

    private static void guardarJson(List<Ejercicio> lista, String nombreArchivo) {
        // Pretty printing para que el JSON sea legible
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
