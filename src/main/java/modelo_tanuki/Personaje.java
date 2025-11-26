
package modelo_tanuki;


public class Personaje {
    private String nombre;
    private String presentacion;
    private String celebrando;
    private String correcto;
    private String incorrecto;
    
    public Personaje(String nombre, String presentacion, String celebrando, String correcto, String incorrecto){
        this.nombre = nombre;
        this.presentacion = presentacion;
        this.celebrando = celebrando;
        this.correcto = correcto;
        this.incorrecto = incorrecto;
    }
    
    public Personaje(){
        nombre = "";
        presentacion = "";
        celebrando = "";
        correcto = "";
        incorrecto = "";
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getCelebrando() {
        return celebrando;
    }

    public void setCelebrando(String celebrando) {
        this.celebrando = celebrando;
    }

    public String getCorrecto() {
        return correcto;
    }

    public void setCorrecto(String correcto) {
        this.correcto = correcto;
    }

    public String getIncorrecto() {
        return incorrecto;
    }

    public void setIncorrecto(String incorrecto) {
        this.incorrecto = incorrecto;
    }
    
    
}
