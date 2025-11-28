/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistencia;

/**
 *
 * @author adrif
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import modelo_tanuki.Logro; 
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ConvertidorLogros {

    public static void main(String[] args) {
        
        // --- 1. CONFIGURACIÓN DE RUTA ABSOLUTA ---
        // Usamos la misma ruta que funcionó en el otro convertidor
        String rutaBase = "C:/Users/adrif/Videos/ING UCAB/03 semestre/POO/proyecto/";
        String archivoExcel = rutaBase + "logros.xlsx";
        String archivoJsonSalida = "src/main/resources/data/logros.json";

        List<Logro> listaLogros = new ArrayList<>();

        System.out.println("=== INICIANDO CONVERSIÓN DE LOGROS ===");
        System.out.println("Buscando archivo en: " + archivoExcel);

        // Verificación inicial
        File f = new File(archivoExcel);
        if (!f.exists()) {
            System.err.println("❌ ERROR: No encuentro 'logros.xlsx' en la ruta especificada.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(f);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); 

            // Iteramos filas (saltando la 0 que es el encabezado)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // Usamos métodos seguros para leer (evita errores si Excel confunde texto con números)
                    
                    // Col 0: ID
                    int id = (int) row.getCell(0).getNumericCellValue();
                    
                    // Col 1: Nombre
                    String nombre = getValorCelda(row.getCell(1));
                    
                    // Col 2: Descripcion
                    String descripcion = getValorCelda(row.getCell(2));
                    
                    // Col 3: idTemaAsociado
                    int idTema = (int) row.getCell(3).getNumericCellValue();
                    
                    // Col 4: PuntosRequeridos
                    int puntos = (int) row.getCell(4).getNumericCellValue();
                    
                    // Col 5: RutaIcono
                    String rutaIcono = getValorCelda(row.getCell(5));

                    // Crear objeto Logro (Usando el constructor nuevo que hicimos)
                    Logro logro = new Logro(id, nombre, descripcion, idTema, puntos, rutaIcono);
                    listaLogros.add(logro);

                    // System.out.println("  > Leído: " + nombre);

                } catch (Exception e) {
                    System.err.println("  [!] Error en fila " + (i + 1) + ": " + e.getMessage());
                }
            }

            // Guardar JSON
            new File(archivoJsonSalida).getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(archivoJsonSalida)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(listaLogros, writer);
                System.out.println("\n✅ ¡ÉXITO! Se generaron " + listaLogros.size() + " logros en: " + archivoJsonSalida);
            }

        } catch (IOException e) {
            System.err.println("❌ Error de lectura/escritura: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // --- UTILIDAD: Para leer celdas sin que el programa explote si cambia el formato ---
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
}