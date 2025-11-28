/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo_tanuki;

/**
 *
 * @author adrif
 */
public abstract class Usuario {
    protected int idUsuario;
    protected String nombre;
    protected String apellido;
    protected String username;
    protected String contrasena;
    
    public Usuario (int idUsuario, String nombre, String apellido, String username, String contrasena){
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.contrasena = contrasena;
    }
    
    public Usuario(){
        idUsuario = 0;
        nombre = apellido = username = contrasena = "";
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public boolean validarDatosAcceso(String username, String contrasena){
        return (this.username.equals(username) && this.contrasena.equals(contrasena));
    }
    
    
}


