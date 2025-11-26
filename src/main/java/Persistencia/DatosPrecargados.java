/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistencia;
import java.util.ArrayList;
import java.util.List;

//clases de modelo

import modelo_tanuki.Tema;
import modelo_tanuki.Logro;
import modelo_tanuki.Usuario;
import modelo_tanuki.Salon;
import modelo_tanuki.Ejercicio;
import modelo_tanuki.NivelDificultad;
import modelo_tanuki.Maestro;
import modelo_tanuki.Estudiante;

/**
 *
 * @author adrif
 */
public class DatosPrecargados {

    // listas que simulan las tablas de base de datos
    private List<Tema> temas = new ArrayList<>();
    private List<Logro> logros = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>(); // guarda estudaintes y maestros
    private List<Salon> salones = new ArrayList<>();
    
    public DatosPrecargados() {
        
        //creando temas con el constructor
        Tema operaciones_basicas = new Tema(1, "Operaciones basicas", null,null); // tema padre
        Tema geometria = new Tema(4, "Geometria", null,null);  // otro tema padre (id,nombre,temapadre)

        Tema suma = new Tema(2, "Suma", operaciones_basicas,null); // hijo de operaciones_basicas
        Tema resta = new Tema(3, "Resta", operaciones_basicas,null); // hijo de operaciones_basicas
        Tema figuras = new Tema(5, "Figuras", geometria,null); // hijo de geometria
        
        operaciones_basicas.agregarTemaHijo(suma);
        operaciones_basicas.agregarTemaHijo(resta);
        geometria.agregarTemaHijo(figuras);
        

        // creando ejercicios y asignandole los temas
        // constructor de 3jercicio
        // (pregunta, opciones[4], respCorrecta, dificultad, tema, valorPuntos, grado, idEjercicio, rutaImagen, retroalimentacion)
        
        // GRADO 1-3 (Grupo 1)
        //  ejercicios de suma (Nivel BAJO: 5 ejercicios)
        suma.agregarEjercicio(new Ejercicio("2 + 2 = ?", new String[]{"3", "4", "5", "1"}, "4", NivelDificultad.BAJO, suma, 10, 1, 101, null, "La suma de 2 y 2 es 4."));
        suma.agregarEjercicio(new Ejercicio("5 + 3 = ?", new String[]{"7", "6", "8", "9"}, "8", NivelDificultad.BAJO, suma, 10, 1, 102, null, null));
        suma.agregarEjercicio(new Ejercicio("1 + 1 = ?", new String[]{"2", "3", "4", "0"}, "2", NivelDificultad.BAJO, suma, 10, 1, 103, null, null));
        suma.agregarEjercicio(new Ejercicio("3 + 4 = ?", new String[]{"5", "6", "7", "8"}, "7", NivelDificultad.BAJO, suma, 10, 1, 104, null, null));
        suma.agregarEjercicio(new Ejercicio("5 + 5 = ?", new String[]{"8", "9", "10", "11"}, "10", NivelDificultad.BAJO, suma, 10, 1, 105, null, null));

        // ejercicios de suma (Nivel MEDIO:5 ejercicios)
        suma.agregarEjercicio(new Ejercicio("10 + 15 = ?", new String[]{"25", "20", "30", "35"}, "25", NivelDificultad.MEDIO, suma, 15, 1, 106, null, null));
        suma.agregarEjercicio(new Ejercicio("20 + 30 = ?", new String[]{"40", "50", "60", "70"}, "50", NivelDificultad.MEDIO, suma, 15, 1, 107, null, null));
        suma.agregarEjercicio(new Ejercicio("50 + 12 = ?", new String[]{"62", "60", "72", "52"}, "62", NivelDificultad.MEDIO, suma, 15, 1, 108, null, null));
        suma.agregarEjercicio(new Ejercicio("22 + 11 = ?", new String[]{"30", "32", "33", "44"}, "33", NivelDificultad.MEDIO, suma, 15, 1, 109, null, null));
        suma.agregarEjercicio(new Ejercicio("40 + 40 = ?", new String[]{"70", "80", "90", "100"}, "80", NivelDificultad.MEDIO, suma, 15, 1, 110, null, null));
        
        // optimo suma---
        suma.agregarEjercicio(new Ejercicio("100 + 50 = ?", new String[]{"150", "160", "200", "105"}, "150", NivelDificultad.OPTIMO, suma, 20, 1, 111, null, null));
        suma.agregarEjercicio(new Ejercicio("75 + 25 = ?", new String[]{"90", "95", "100", "105"}, "100", NivelDificultad.OPTIMO, suma, 20, 1, 112, null, "Recuerda que 4 monedas de 25 hacen 100."));
        suma.agregarEjercicio(new Ejercicio("50 + 50 = ?", new String[]{"90", "100", "110", "80"}, "100", NivelDificultad.OPTIMO, suma, 20, 1, 113, null, null));
        suma.agregarEjercicio(new Ejercicio("110 + 20 = ?", new String[]{"120", "130", "140", "125"}, "130", NivelDificultad.OPTIMO, suma, 20, 1, 114, null, null));
        suma.agregarEjercicio(new Ejercicio("60 + 30 = ?", new String[]{"70", "80", "90", "100"}, "90", NivelDificultad.OPTIMO, suma, 20, 1, 115, null, null));
        
        // elevado
        suma.agregarEjercicio(new Ejercicio("50 + 50 = ?", new String[]{"100", "90", "110", "95"}, "100", NivelDificultad.ELEVADO, suma, 25, 1, 116, null, null));
        suma.agregarEjercicio(new Ejercicio("75 + 25 = ?", new String[]{"90", "95", "100", "105"}, "100", NivelDificultad.ELEVADO, suma, 25, 1, 117, null, "Recuerda que 4 monedas de 25 hacen 100."));
        suma.agregarEjercicio(new Ejercicio("30 + 35 = ?", new String[]{"55", "60", "65", "70"}, "65", NivelDificultad.ELEVADO, suma, 25, 1, 118, null, null));
        suma.agregarEjercicio(new Ejercicio("45 + 15 = ?", new String[]{"50", "55", "60", "65"}, "60", NivelDificultad.ELEVADO, suma, 25, 1, 119, null, null));
        suma.agregarEjercicio(new Ejercicio("25 + 25 = ?", new String[]{"40", "45", "50", "55"}, "50", NivelDificultad.ELEVADO, suma, 25, 1, 120, null, null));
   
        // GRADO 4-6 (Grupo 4)
        // bajo
        suma.agregarEjercicio(new Ejercicio("100 + 200 = ?", new String[]{"300", "400", "500", "200"}, "300", NivelDificultad.BAJO, suma, 10, 4, 126, null, null));
        suma.agregarEjercicio(new Ejercicio("50 + 30 = ?", new String[]{"70", "60", "80", "90"}, "80", NivelDificultad.BAJO, suma, 10, 4, 127, null, null));
        suma.agregarEjercicio(new Ejercicio("10 + 10 = ?", new String[]{"20", "30", "40", "100"}, "20", NivelDificultad.BAJO, suma, 10, 4, 128, null, null));
        suma.agregarEjercicio(new Ejercicio("30 + 40 = ?", new String[]{"50", "60", "70", "80"}, "70", NivelDificultad.BAJO, suma, 10, 4, 129, null, null));
        suma.agregarEjercicio(new Ejercicio("50 + 50 = ?", new String[]{"80", "90", "100", "110"}, "100", NivelDificultad.BAJO, suma, 10, 4, 130, null, null));
        // medio
        suma.agregarEjercicio(new Ejercicio("150 + 100 = ?", new String[]{"250", "200", "300", "350"}, "250", NivelDificultad.MEDIO, suma, 15, 4, 131, null, null));
        suma.agregarEjercicio(new Ejercicio("250 + 100 = ?", new String[]{"300", "350", "400", "450"}, "350", NivelDificultad.MEDIO, suma, 15, 4, 132, null, null));
        suma.agregarEjercicio(new Ejercicio("120 + 110 = ?", new String[]{"220", "230", "240", "250"}, "230", NivelDificultad.MEDIO, suma, 15, 4, 133, null, null));
        suma.agregarEjercicio(new Ejercicio("220 + 220 = ?", new String[]{"400", "420", "440", "444"}, "440", NivelDificultad.MEDIO, suma, 15, 4, 134, null, null));
        suma.agregarEjercicio(new Ejercicio("300 + 150 = ?", new String[]{"400", "450", "500", "550"}, "450", NivelDificultad.MEDIO, suma, 15, 4, 135, null, null));
        // optimo
        suma.agregarEjercicio(new Ejercicio("150 + 250 = ?", new String[]{"300", "350", "400", "450"}, "400", NivelDificultad.OPTIMO, suma, 20, 4, 136, null, null));
        suma.agregarEjercicio(new Ejercicio("300 + 400 = ?", new String[]{"700", "600", "800", "1000"}, "700", NivelDificultad.OPTIMO, suma, 20, 4, 137, null, null));
        suma.agregarEjercicio(new Ejercicio("222 + 111 = ?", new String[]{"333", "323", "321", "444"}, "333", NivelDificultad.OPTIMO, suma, 20, 4, 138, null, null));
        suma.agregarEjercicio(new Ejercicio("500 + 250 = ?", new String[]{"700", "750", "800", "650"}, "750", NivelDificultad.OPTIMO, suma, 20, 4, 139, null, null));
        suma.agregarEjercicio(new Ejercicio("120 + 130 = ?", new String[]{"240", "250", "260", "245"}, "250", NivelDificultad.OPTIMO, suma, 20, 4, 140, null, null));
        // elevado
        suma.agregarEjercicio(new Ejercicio("1000 + 2500 = ?", new String[]{"3000", "3500", "4000", "3300"}, "3500", NivelDificultad.ELEVADO, suma, 25, 4, 141, null, null));
        suma.agregarEjercicio(new Ejercicio("1500 + 1500 = ?", new String[]{"3000", "2500", "3500", "4000"}, "3000", NivelDificultad.ELEVADO, suma, 25, 4, 142, null, null));
        suma.agregarEjercicio(new Ejercicio("2200 + 3300 = ?", new String[]{"5000", "5500", "5550", "5050"}, "5500", NivelDificultad.ELEVADO, suma, 25, 4, 143, null, null));
        suma.agregarEjercicio(new Ejercicio("5000 + 1250 = ?", new String[]{"6250", "6500", "6050", "7250"}, "6250", NivelDificultad.ELEVADO, suma, 25, 4, 144, null, null));
        suma.agregarEjercicio(new Ejercicio("1234 + 1000 = ?", new String[]{"2234", "2000", "2134", "2334"}, "2234", NivelDificultad.ELEVADO, suma, 25, 4, 145, null, null));
     
        //RESTA GRADO 1-3 (Grupo 1) 
        // bajo
        resta.agregarEjercicio(new Ejercicio("5 - 2 = ?", new String[]{"1", "2", "3", "4"}, "3", NivelDificultad.BAJO, resta, 10, 1, 301, null, null));
        resta.agregarEjercicio(new Ejercicio("10 - 5 = ?", new String[]{"5", "4", "6", "3"}, "5", NivelDificultad.BAJO, resta, 10, 1, 302, null, "10 menos 5 es la mitad, 5."));
        resta.agregarEjercicio(new Ejercicio("3 - 1 = ?", new String[]{"0", "1", "2", "3"}, "2", NivelDificultad.BAJO, resta, 10, 1, 303, null, null));
        resta.agregarEjercicio(new Ejercicio("8 - 4 = ?", new String[]{"2", "4", "6", "0"}, "4", NivelDificultad.BAJO, resta, 10, 1, 304, null, null));
        resta.agregarEjercicio(new Ejercicio("9 - 3 = ?", new String[]{"5", "6", "7", "4"}, "6", NivelDificultad.BAJO, resta, 10, 1, 305, null, null));
        // medio
        resta.agregarEjercicio(new Ejercicio("12 - 6 = ?", new String[]{"6", "5", "7", "8"}, "6", NivelDificultad.MEDIO, resta, 15, 1, 306, null, null));
        resta.agregarEjercicio(new Ejercicio("10 - 8 = ?", new String[]{"1", "2", "3", "4"}, "2", NivelDificultad.MEDIO, resta, 15, 1, 307, null, null));
        resta.agregarEjercicio(new Ejercicio("15 - 7 = ?", new String[]{"6", "7", "8", "9"}, "8", NivelDificultad.MEDIO, resta, 15, 1, 308, null, null));
        resta.agregarEjercicio(new Ejercicio("14 - 5 = ?", new String[]{"9", "8", "7", "10"}, "9", NivelDificultad.MEDIO, resta, 15, 1, 309, null, null));
        resta.agregarEjercicio(new Ejercicio("11 - 2 = ?", new String[]{"7", "8", "9", "10"}, "9", NivelDificultad.MEDIO, resta, 15, 1, 310, null, null));
        // optimo
        resta.agregarEjercicio(new Ejercicio("20 - 10 = ?", new String[]{"5", "10", "15", "0"}, "10", NivelDificultad.OPTIMO, resta, 20, 1, 311, null, null));
        resta.agregarEjercicio(new Ejercicio("15 - 5 = ?", new String[]{"5", "10", "15", "0"}, "10", NivelDificultad.OPTIMO, resta, 20, 1, 312, null, null));
        resta.agregarEjercicio(new Ejercicio("30 - 10 = ?", new String[]{"10", "15", "20", "25"}, "20", NivelDificultad.OPTIMO, resta, 20, 1, 313, null, null));
        resta.agregarEjercicio(new Ejercicio("25 - 5 = ?", new String[]{"15", "20", "10", "5"}, "20", NivelDificultad.OPTIMO, resta, 20, 1, 314, null, null));
        resta.agregarEjercicio(new Ejercicio("50 - 10 = ?", new String[]{"20", "30", "40", "35"}, "40", NivelDificultad.OPTIMO, resta, 20, 1, 315, null, null));
        // elevado
        resta.agregarEjercicio(new Ejercicio("50 - 25 = ?", new String[]{"10", "15", "20", "25"}, "25", NivelDificultad.ELEVADO, resta, 25, 1, 316, null, null));
        resta.agregarEjercicio(new Ejercicio("100 - 50 = ?", new String[]{"50", "40", "60", "25"}, "50", NivelDificultad.ELEVADO, resta, 25, 1, 317, null, null));
        resta.agregarEjercicio(new Ejercicio("75 - 25 = ?", new String[]{"25", "40", "50", "60"}, "50", NivelDificultad.ELEVADO, resta, 25, 1, 318, null, null));
        resta.agregarEjercicio(new Ejercicio("60 - 30 = ?", new String[]{"30", "20", "40", "10"}, "30", NivelDificultad.ELEVADO, resta, 25, 1, 319, null, null));
        resta.agregarEjercicio(new Ejercicio("40 - 20 = ?", new String[]{"10", "15", "20", "25"}, "20", NivelDificultad.ELEVADO, resta, 25, 1, 320, null, null));
    
        //GRADO 4-6 (Grupo 4)
        // bajo
        resta.agregarEjercicio(new Ejercicio("100 - 50 = ?", new String[]{"50", "40", "60", "25"}, "50", NivelDificultad.BAJO, resta, 10, 4, 326, null, null));
        resta.agregarEjercicio(new Ejercicio("75 - 25 = ?", new String[]{"25", "40", "50", "60"}, "50", NivelDificultad.BAJO, resta, 10, 4, 327, null, null));
        resta.agregarEjercicio(new Ejercicio("50 - 25 = ?", new String[]{"10", "15", "20", "25"}, "25", NivelDificultad.BAJO, resta, 10, 4, 328, null, null));
        resta.agregarEjercicio(new Ejercicio("60 - 30 = ?", new String[]{"30", "20", "40", "10"}, "30", NivelDificultad.BAJO, resta, 10, 4, 329, null, null));
        resta.agregarEjercicio(new Ejercicio("40 - 20 = ?", new String[]{"10", "15", "20", "25"}, "20", NivelDificultad.BAJO, resta, 10, 4, 330, null, null));
        // medio
        resta.agregarEjercicio(new Ejercicio("500 - 100 = ?", new String[]{"100", "200", "300", "400"}, "400", NivelDificultad.MEDIO, resta, 15, 4, 331, null, null));
        resta.agregarEjercicio(new Ejercicio("1000 - 500 = ?", new String[]{"500", "400", "600", "100"}, "500", NivelDificultad.MEDIO, resta, 15, 4, 332, null, null));
        resta.agregarEjercicio(new Ejercicio("350 - 150 = ?", new String[]{"100", "150", "200", "250"}, "200", NivelDificultad.MEDIO, resta, 15, 4, 333, null, null));
        resta.agregarEjercicio(new Ejercicio("225 - 25 = ?", new String[]{"175", "200", "220", "150"}, "200", NivelDificultad.MEDIO, resta, 15, 4, 334, null, null));
        resta.agregarEjercicio(new Ejercicio("1000 - 100 = ?", new String[]{"800", "850", "900", "950"}, "900", NivelDificultad.MEDIO, resta, 15, 4, 335, null, null));
        // optimo
        resta.agregarEjercicio(new Ejercicio("5000 - 2500 = ?", new String[]{"1500", "2000", "2500", "3000"}, "2500", NivelDificultad.OPTIMO, resta, 20, 4, 336, null, null));
        resta.agregarEjercicio(new Ejercicio("10000 - 1000 = ?", new String[]{"9000", "8000", "7000", "9500"}, "9000", NivelDificultad.OPTIMO, resta, 20, 4, 337, null, null));
        resta.agregarEjercicio(new Ejercicio("1200 - 300 = ?", new String[]{"800", "900", "1000", "700"}, "900", NivelDificultad.OPTIMO, resta, 20, 4, 338, null, null));
        resta.agregarEjercicio(new Ejercicio("550 - 50 = ?", new String[]{"510", "505", "500", "450"}, "500", NivelDificultad.OPTIMO, resta, 20, 4, 339, null, null));
        resta.agregarEjercicio(new Ejercicio("2500 - 1250 = ?", new String[]{"1000", "1250", "1500", "1300"}, "1250", NivelDificultad.OPTIMO, resta, 20, 4, 340, null, null));
        // elevado
        resta.agregarEjercicio(new Ejercicio("150 - 125 = ?", new String[]{"25", "50", "15", "30"}, "25", NivelDificultad.ELEVADO, resta, 25, 4, 341, null, null));
        resta.agregarEjercicio(new Ejercicio("550 - 25 = ?", new String[]{"500", "525", "530", "535"}, "525", NivelDificultad.ELEVADO, resta, 25, 4, 342, null, null));
        resta.agregarEjercicio(new Ejercicio("1000 - 10 = ?", new String[]{"900", "950", "990", "999"}, "990", NivelDificultad.ELEVADO, resta, 25, 4, 343, null, null));
        resta.agregarEjercicio(new Ejercicio("505 - 5 = ?", new String[]{"500", "501", "502", "503"}, "500", NivelDificultad.ELEVADO, resta, 25, 4, 344, null, null));
        resta.agregarEjercicio(new Ejercicio("101 - 11 = ?", new String[]{"80", "85", "90", "95"}, "90", NivelDificultad.ELEVADO, resta, 25, 4, 345, null, null));
       
        
        // FIGURAS GRADO 1-3 (Grupo 1) ---
        // bajo
        figuras.agregarEjercicio(new Ejercicio("¿Que figura tiene 3 lados?", new String[]{"Círculo", "Cuadrado", "Triángulo", "Rectángulo"}, "Triángulo", NivelDificultad.BAJO, figuras, 15, 1, 501, "/Recursos/Imagenes/triangulo.png", "¡Correcto! Un triángulo es una figura que tiene 3 lados."));
        figuras.agregarEjercicio(new Ejercicio("¿Qué figura tiene 4 lados iguales?", new String[]{"Cuadrado", "Rectángulo", "Círculo", "Pentágono"}, "Cuadrado", NivelDificultad.BAJO, figuras, 15, 1, 502, null, "El cuadrado tiene 4 lados iguales."));
        figuras.agregarEjercicio(new Ejercicio("¿Qué figura no tiene lados ni vertices?", new String[]{"Círculo", "Óvalo", "Esfera", "Cilindro"}, "Círculo", NivelDificultad.BAJO, figuras, 15, 1, 503, null, null));
        figuras.agregarEjercicio(new Ejercicio("¿Cómo se llama esta figura?", new String[]{"Cuadrado", "Triángulo", "Óvalo", "Rectángulo"}, "Rectángulo", NivelDificultad.BAJO, figuras, 15, 1, 504, null, "El rectángulo tiene 4 lados, pero no todos son iguales."));
        figuras.agregarEjercicio(new Ejercicio("Tengo 5 puntas y a menudo me dibujan en el cielo por la noche. ¿Qué figura soy?", new String[]{"Rombo", "Estrella", "Corazón", "Luna"}, "Estrella", NivelDificultad.BAJO, figuras, 15, 1, 505, null, null));
        // medio
        figuras.agregarEjercicio(new Ejercicio("¿Cuántos lados tiene un triángulo?", new String[]{"3", "4", "5", "depende"}, "3", NivelDificultad.MEDIO, figuras, 20, 1, 506, null, "Los triángulos siempre tienen 3 lados."));
        figuras.agregarEjercicio(new Ejercicio("¿Cuántos lados tiene un cuadrado?", new String[]{"2", "3", "4", "5"}, "4", NivelDificultad.MEDIO, figuras, 20, 1, 507, null, "Los cuadrados siempre tienen 4 lados."));
        figuras.agregarEjercicio(new Ejercicio("¿Qué figura no tiene lados rectos?", new String[]{"Triángulo", "Círculo", "Cuadrado", "Rectángulo"}, "Círculo", NivelDificultad.MEDIO, figuras, 20, 1, 508, null, "El círculo es una línea curva cerrada."));
        figuras.agregarEjercicio(new Ejercicio("Me parezco a un cuadrado que se ha inclinado o 'acostado'. Todos mis cuatro lados miden exactamente lo mismo. ¿Qué figura soy?", new String[]{"Triángulo", "Pentágono", "Rombo", "Círculo"}, "Rombo", NivelDificultad.MEDIO, figuras, 20, 1, 509, null, null));
        figuras.agregarEjercicio(new Ejercicio("¿Cual de las siguientes figuras tiene 5 lados?", new String[]{"Hexágono", "Pentágono", "Heptágono", "Cuadrado"}, "Pentágono", NivelDificultad.MEDIO, figuras, 20, 1, 510, null, "¡Las figuras de 5 lados se llaman Pentágonos!"));
        // optimo
        figuras.agregarEjercicio(new Ejercicio("¿Cuántos lados tiene un Rombo?", new String[]{"3", "4", "5", "6"}, "4", NivelDificultad.OPTIMO, figuras, 25, 1, 511, null, "Un rombo, como un cuadrado, tiene 4 lados."));
        figuras.agregarEjercicio(new Ejercicio("¿Cómo se llama la figura de 6 lados?", new String[]{"Pentágono", "Hexágono", "Heptágono", "Octágono"}, "Hexágono", NivelDificultad.OPTIMO, figuras, 25, 1, 512, null, null));
        figuras.agregarEjercicio(new Ejercicio("¿Cuántos lados tiene un octagono?", new String[]{"5", "6", "7", "8"}, "8", NivelDificultad.OPTIMO, figuras, 25, 1, 513, null, "Un Octágono tiene 8 lados."));
        figuras.agregarEjercicio(new Ejercicio("Soy una figura de 4 lados, pero no todos son iguales. Me reconocerás porque tengo solo dos lados que son paralelos (nunca se tocan), mientras que mis otros dos lados sí se inclinan. ¿Qué figura soy?", new String[]{"Trapecio", "Rombo", "Cubo", "Esfera"}, "Trapecio", NivelDificultad.OPTIMO, figuras, 25, 1, 514, null, null));
        figuras.agregarEjercicio(new Ejercicio("Un campo de fútbol se parece a un:", new String[]{"Círculo", "Rectángulo", "Triángulo", "Óvalo"}, "Rectángulo", NivelDificultad.OPTIMO, figuras, 25, 1, 515, null, null));
        // elevado
        figuras.agregarEjercicio(new Ejercicio("¿Cuál es un cuerpo geométrico?", new String[]{"Cuadrado", "Círculo", "Cubo", "Triángulo"}, "Cubo", NivelDificultad.ELEVADO, figuras, 30, 1, 516, null, "Un cubo tiene volumen, los otros son planos."));
        figuras.agregarEjercicio(new Ejercicio("¿Cuál es un cuerpo geométrico?", new String[]{"Cilindro", "Rectángulo", "Óvalo", "Rombo"}, "Cilindro", NivelDificultad.ELEVADO, figuras, 30, 1, 517, null, "Un cilindro tiene volumen."));
        figuras.agregarEjercicio(new Ejercicio("Esta figura es una:", new String[]{"Círculo", "Esfera", "Cilindro", "Cono"}, "Esfera", NivelDificultad.ELEVADO, figuras, 30, 1, 518, null, "Una pelota es una esfera."));
        figuras.agregarEjercicio(new Ejercicio("¿Cómo se llama esta figura?", new String[]{"Pirámide", "Cono", "Cubo", "Prisma"}, "Pirámide", NivelDificultad.ELEVADO, figuras, 30, 1, 519, null, null));
        figuras.agregarEjercicio(new Ejercicio("Esta figura es un:", new String[]{"Prisma", "Cubo", "Cono", "Pirámide"}, "Cono", NivelDificultad.ELEVADO, figuras, 30, 1, 520, null, "Un cono de helado tiene esta forma."));
   
        // GRADO 4-6 (Grupo 4)
        // bajo
        figuras.agregarEjercicio(new Ejercicio("¿Qué figura tiene 3 lados?", new String[]{"Triángulo", "Cuadrado", "Círculo", "Pentágono"}, "Triángulo", NivelDificultad.BAJO, figuras, 15, 4, 526, null, null));
        figuras.agregarEjercicio(new Ejercicio("¿Qué figura tiene 4 lados iguales?", new String[]{"Rectángulo", "Rombo", "Trapecio", "Cuadrado"}, "Cuadrado", NivelDificultad.BAJO, figuras, 15, 4, 527, null, null));
        figuras.agregarEjercicio(new Ejercicio("¿Qué figura es redonda?", new String[]{"Círculo", "Óvalo", "Rectángulo", "Rombo"}, "Círculo", NivelDificultad.BAJO, figuras, 15, 4, 528, null, null));
        figuras.agregarEjercicio(new Ejercicio("¿Cómo se llama la figura de 5 lados?", new String[]{"Hexágono", "Pentágono", "Heptágono", "Octágono"}, "Pentágono", NivelDificultad.BAJO, figuras, 15, 4, 529, null, null));
        figuras.agregarEjercicio(new Ejercicio("¿Cómo se llama la figura de 6 lados?", new String[]{"Pentágono", "Hexágono", "Heptágono", "Octágono"}, "Hexágono", NivelDificultad.BAJO, figuras, 15, 4, 530, null, null));
        // medio
        figuras.agregarEjercicio(new Ejercicio("¿Cómo se llama la figura de 8 lados?", new String[]{"Pentágono", "Hexágono", "Heptágono", "Octágono"}, "Octágono", NivelDificultad.MEDIO, figuras, 20, 4, 531, null, null));
        figuras.agregarEjercicio(new Ejercicio("Un Rombo y un Cuadrado tienen...", new String[]{"3 lados", "4 lados", "5 lados", "Lados curvos"}, "4 lados", NivelDificultad.MEDIO, figuras, 20, 4, 532, null, "Ambos son cuadriláteros."));
        figuras.agregarEjercicio(new Ejercicio("¿Cuál de estos NO es un cuadrilátero?", new String[]{"Cuadrado", "Rectángulo", "Pentágono", "Rombo"}, "Pentágono", NivelDificultad.MEDIO, figuras, 20, 4, 533, null, "Un pentágono tiene 5 lados, no 4."));
        figuras.agregarEjercicio(new Ejercicio("Esta figura es un:", new String[]{"Trapecio", "Romboide", "Cubo", "Esfera"}, "Trapecio", NivelDificultad.MEDIO, figuras, 20, 4, 534, null, null));
        figuras.agregarEjercicio(new Ejercicio("Un triángulo con 3 lados iguales se llama:", new String[]{"Isósceles", "Equilátero", "Escaleno", "Rectángulo"}, "Equilátero", NivelDificultad.MEDIO, figuras, 20, 4, 535, null, "'Equi' significa igual, 'látero' significa lado."));
        // optimo
        figuras.agregarEjercicio(new Ejercicio("Un triángulo con 2 lados iguales se llama:", new String[]{"Isósceles", "Equilátero", "Escaleno", "Rectángulo"}, "Isósceles", NivelDificultad.OPTIMO, figuras, 25, 4, 536, null, null));
        figuras.agregarEjercicio(new Ejercicio("Un triángulo con 0 lados iguales se llama:", new String[]{"Isósceles", "Equilátero", "Escaleno", "Rectángulo"}, "Escaleno", NivelDificultad.OPTIMO, figuras, 25, 4, 537, null, null));
        figuras.agregarEjercicio(new Ejercicio("¿Cuál es un cuerpo geométrico?", new String[]{"Cuadrado", "Círculo", "Cubo", "Triángulo"}, "Cubo", NivelDificultad.OPTIMO, figuras, 25, 4, 538, null, "Un cubo tiene volumen (3D)."));
        figuras.agregarEjercicio(new Ejercicio("¿Cuántas caras tiene un cubo?", new String[]{"4", "5", "6", "8"}, "6", NivelDificultad.OPTIMO, figuras, 25, 4, 539, null, "Un cubo tiene 6 caras cuadradas."));
        figuras.agregarEjercicio(new Ejercicio("Esta figura es una:", new String[]{"Círculo", "Esfera", "Cilindro", "Cono"}, "Esfera", NivelDificultad.OPTIMO, figuras, 25, 4, 540, null, "Una pelota es una esfera."));
        // elevado
        figuras.agregarEjercicio(new Ejercicio("Esta figura es un:", new String[]{"Prisma", "Cubo", "Cono", "Pirámide"}, "Cono", NivelDificultad.ELEVADO, figuras, 30, 4, 541, null, "Un cono de helado tiene esta forma."));
        figuras.agregarEjercicio(new Ejercicio("¿Cuántos vértices (esquinas) tiene un cubo?", new String[]{"4", "6", "8", "12"}, "8", NivelDificultad.ELEVADO, figuras, 30, 4, 542, null, "Un cubo tiene 8 vértices."));
        figuras.agregarEjercicio(new Ejercicio("¿Qué figura plana forma la base de un cilindro?", new String[]{"Cuadrado", "Triángulo", "Círculo", "Rectángulo"}, "Círculo", NivelDificultad.ELEVADO, figuras, 30, 4, 543, null, "Un cilindro tiene dos bases circulares."));
        figuras.agregarEjercicio(new Ejercicio("Esta figura es un:", new String[]{"Cilindro", "Rectángulo", "Óvalo", "Rombo"}, "Cilindro", NivelDificultad.ELEVADO, figuras, 30, 4, 544, null, null));
        figuras.agregarEjercicio(new Ejercicio("¿Cómo se llama esta figura?", new String[]{"Pirámide", "Cono", "Cubo", "Prisma"}, "Pirámide", NivelDificultad.ELEVADO, figuras, 30, 4, 545, null, null));
        
       // logros 
        // constructir de Logro(String nombre, String descripcion, int puntosNecesarios, Tema tema, int id, String rutaIcono)
        Logro l1 = new Logro("Novato de la Suma", "Gana 50 puntos en Suma", 50, suma, 501,"/Recursos/Imagenes/iconos/novato_suma.png");
        Logro l2 = new Logro("Maestro de la Suma", "Gana 200 puntos en Suma", 200, suma, 502, "/Recursos/Imagenes/iconos/maestro_suma.png");
        Logro l3 = new Logro("Geómetra", "Gana 30 puntos en Figuras", 30, figuras, 503, "/Recursos/Imagenes/iconos/geometra_figuras.png");
        Logro l4 = new Logro("Experto en Restas", "Gana 100 puntos en Resta", 100, resta, 504, "/Recursos/Imagenes/iconos/experto_resta.png");
     
        // usuarios: maestro y estudiantes, para usar el login y probar que funciona. ademas, asi se puede probar lo de los reportes
        // constructor: Maestro(int idUsuario, String nombre, String apellido, String correo, String contrasena)
        Maestro m1 = new Maestro(1, "Profesor", "Alan", "profe@gmail.com", "123");
        
        // constructor: Salon(int grado, char seccion, Maestro maestro, int idSalon)
        Salon s1 = new Salon(2, 'A', m1, 901); // Salón para Ana y Carlos
        Salon s2 = new Salon(5, 'B', m1, 902); // Salón para Luis
        
        //darle los salones al maestro
        m1.getSalones().add(s1);
        m1.getSalones().add(s2);
        
        // Estudiantes  de los salones
        Estudiante e1 = new Estudiante();
        e1.setIdUsuario(10);
        e1.setNombre("Ana");
        e1.setApellido("Gomez");
        e1.setCorreo("ana@gmail.com");
        e1.setContrasena("123");
        e1.setGrado(2); // Grado 2 -> Grupo 1
        e1.setSeccion('A');
        
        Estudiante e2 = new Estudiante();
        e2.setIdUsuario(11);
        e2.setNombre("Luis");
        e2.setApellido("Reyes");
        e2.setCorreo("luis@gmail.com");
        e2.setContrasena("123");
        e2.setGrado(0); // Grado 5 -> Grupo 4 //SI LUIS AUN NO TIENE SALON NO DEBERIA TENER GRADO NI SECCION
        e2.setSeccion(' ');
        
        Estudiante e3 = new Estudiante();
        e3.setIdUsuario(12);
        e3.setNombre("Carlos");
        e3.setApellido("Pinto");
        e3.setCorreo("carlos@gmail.com");
        e3.setContrasena("123");
        e3.setGrado(3); // Grado 3 -> Grupo 1
        e3.setSeccion('A');
        
        // simular que Ana y Carlos ya estan en el salon 1
        s1.agregarEstudiante(e1);
        s1.agregarEstudiante(e3);
        
        // simular q luis esta en la lista de solicitudes del salon 2
        s2.recibirSolicitud(e2);
        
        // guardar todo en las listas
        
        this.temas.add(operaciones_basicas);
        this.temas.add(geometria);
        this.temas.add(suma);
        this.temas.add(resta);
        this.temas.add(figuras);
        
        this.logros.add(l1);
        this.logros.add(l2);
        this.logros.add(l3);
        this.logros.add(l4);
        
        this.usuarios.add(m1);
        this.usuarios.add(e1);
        this.usuarios.add(e2);
        this.usuarios.add(e3);
        
        this.salones.add(s1);
        this.salones.add(s2);
    }
    
    // los getters para q el controlador lea los datos precargados

    public List<Tema> getTemas() {
        return this.temas;
    }

    public List<Logro> getLogros() {
        return this.logros;
    }

    public List<Usuario> getUsuarios() {
        return this.usuarios;
    }

    public List<Salon> getSalones() {
        return this.salones;
    }
}