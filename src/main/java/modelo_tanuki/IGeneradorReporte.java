/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;

/**
 *
 * @author adrif
 */
public interface IGeneradorReporte {
    /**
     * Toma el objeto Reporte (con todos los datos) y lo
     * escribe en un archivo físico (Excel o PDF).
     * @param datos El objeto Reporte que te dio el AnalizadorSalon.
     * @throws Exception Si ocurre un error al escribir el archivo.
     */
    void guardar(Reporte datos) throws Exception;
}
