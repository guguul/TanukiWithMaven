/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;
import java.util.List;
import java.util.Random;
/**
 *
 * @author adrif
 */
public class FrasesMotivacionales {
    private static final List<String> FRASES_CORRECTAS = List.of(
        "¡Muy bien! Se nota que estás aprendiendo mucho.",
        "¡Excelente trabajo! Sigue así.",
        "¡Perfecto! Piensas como un verdadero matemático.",
        "¡Eso fue brillante!",
        "¡Increíble! Has logrado resolverlo correctamente.",
        "¡Qué gran avance! Estás mejorando cada vez más.",
        "¡Exacto! Tienes una mente muy curiosa.",
        "¡Correcto! Lo hiciste con mucha atención."
    );
    
    private static final List<String> FRASES_INCORRECTAS = List.of(
        "No pasa nada, ¡inténtalo otra vez!",
        "Casi lo logras, revisa con calma.",
        "Vas muy bien, solo necesitas ajustar un poco.",
        "Los errores también enseñan, inténtalo de nuevo.",
        "Muy buen intento, ¡No te rindas!",
        "¡Te estás acercando, inténtalo otra vez!.",
        "Lo importante es que sigas intentándolo.",
        "¡Vas por buen camino! Solo falta un pequeño detalle.",
        "Cada intento te hace aprender algo nuevo."
    );
    
    private static final Random rand = new Random();
    
    public static String getFraseCorrecta() { // devuelve una frase random de la list de correctas
        int index = rand.nextInt(FRASES_CORRECTAS.size());
        return FRASES_CORRECTAS.get(index);
    }
    
    public static String getFraseIncorrecta() { // devuelve una frase random de la list de incorrectas
        int index = rand.nextInt(FRASES_INCORRECTAS.size());
        return FRASES_INCORRECTAS.get(index);
    }
    
    
}
