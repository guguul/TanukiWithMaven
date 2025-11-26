
package controller;

import Persistencia.DatosPrecargados;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import modelo_tanuki.Estudiante;
import modelo_tanuki.Logro;
import modelo_tanuki.Maestro;
import modelo_tanuki.Salon;
import modelo_tanuki.Tema;
import modelo_tanuki.Usuario;
import modelo_tanuki.Ejercicio;
import modelo_tanuki.Progreso;
import modelo_tanuki.Resultado;
import javax.swing.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import modelo_tanuki.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.time.*;
import java.io.File;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import java.util.Map;
import java.util.HashMap;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction; // <--- NUEVO E IMPORTANTE
import com.google.firebase.cloud.FirestoreClient;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

import javax.sound.sampled.*; 
import java.io.IOException;
import java.net.URL;

public class SistemaControlador {
    
    private Usuario usuarioActual;
    private List<Usuario> listaUsuarios;
    private List<Tema> listaTemas;
    private List<Logro> listaLogros;
    private List<Salon> listaSalones; 
    private List<Estudiante> estudiantesRegistrados =  new ArrayList<>();;
    private List<modelo_tanuki.Ejercicio> practicaActual;
    private List<modelo_tanuki.Resultado> resultadosTemporales = new ArrayList<>();
    private int indicePreguntaActual;
    private Instant tiempoInicioPregunta;
    private final String RUTA_BASE_IMAGENES = "/Recursos/Imagenes/Ejercicios";
    private List<Ejercicio> ejerciciosFallados = new ArrayList<>();
    private boolean esRondaDeRepeticion = false;
    private Random decisionAleatoria = new Random();
    private List<Ejercicio> ejerciciosPendientes; 
    private Ejercicio ejercicioActual;   
    private Tema temaSeleccionado;
    private NivelDificultad nivelSeleccionado;
    private FiltroPractica filtroPractica;
    
    public SistemaControlador(){
        DatosPrecargados datos = new DatosPrecargados();

            
        this.listaUsuarios = datos.getUsuarios();
        this.listaTemas = datos.getTemas();
        this.listaLogros = datos.getLogros();
        this.listaSalones = datos.getSalones();
        this.filtroPractica = new FiltroPractica();
        
        for (Usuario user: listaUsuarios){
            if (user instanceof Estudiante){
                this.estudiantesRegistrados.add((Estudiante)user);
            }
        }
        
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public List<Tema> getListaTemas() {
        return listaTemas;
    }

    public void setListaTemas(List<Tema> listaTemas) {
        this.listaTemas = listaTemas;
    }

    public List<Logro> getListaLogros() {
        return listaLogros;
    }

    public void setListaLogros(List<Logro> listaLogros) {
        this.listaLogros = listaLogros;
    }

    public List<Salon> getListaSalones() {
        return listaSalones;
    }

    public void setListaSalones(List<Salon> listaSalones) {
        this.listaSalones = listaSalones;
    }

    public List<Estudiante> getEstudiantesRegistrados() {
        return estudiantesRegistrados;
    }

    public void setEstudiantesRegistrados(List<Estudiante> estudiantesRegistrados) {
        this.estudiantesRegistrados = estudiantesRegistrados;
    }
    
    

    public void reproducirSonido(String nombreArchivo) {
        try {
            // 1. Obtenemos la URL del archivo (Igual que con tus imágenes)
            // Nota: Asume que tienes una carpeta "sonidos" dentro de resources
            URL url = getClass().getResource("/sonidos/" + nombreArchivo);

            if (url != null) {
                // 2. Abrir el flujo de audio
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                // 3. Obtener un clip de sonido del sistema
                Clip clip = AudioSystem.getClip();
                // 4. Abrir el clip y reproducirlo
                clip.open(audioIn);
                clip.start();
            } else {
                System.err.println("No se encontró el archivo de sonido: " + nombreArchivo);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    public boolean validarCampoTexto(JTextField campo) {
        String texto = campo.getText().trim(); 

        if (texto.matches("[a-zA-ZñÑáéíóúÁÉÍÓÚ ]+")) {
            return true; 
        } else {
            JOptionPane.showMessageDialog(null,"Solo puede ingresar letras en Nombre y Apellido","Error Datos",JOptionPane.ERROR_MESSAGE);
            return false; // No es válido
        }
    }
    
    public boolean validarContrasena(JTextField clave){
        String contrasena = clave.getText().trim();
        if (contrasena.length()<8 || contrasena.contains(" ")){
            JOptionPane.showMessageDialog(null,"La contraseña debe tener mínimo 8 caracteres y no debe tener espacios intermedios","Error Datos",JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            return true;
        }
                
    }
    public boolean esVacio (JTextField campo, String mensaje){
            if (campo.getText().isEmpty()|| "Ingresa tu primer nombre".equals(campo.getText())|| campo.getText().equals("Ingresa tu primer apellido")||campo.getText().equals("Ingrese un nombre de usuario")|| campo.getText().equals("********"))
               { JOptionPane.showMessageDialog(null, mensaje, "Error falta un dato", JOptionPane.ERROR_MESSAGE); 
                 return true; 
               }
            else
                 return false;
        }  

    public boolean validarEntero (String numero, String mensaje){
        try{
            int numero1 = Integer.parseInt(numero);
            return true; 
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(null,mensaje,"Debe indicar un numero entero", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean validarGrados(float numero,String mensaje){
       {
          if (numero < 1 || numero > 6)
            {JOptionPane.showMessageDialog(null,mensaje,"Rango no permitido", JOptionPane.ERROR_MESSAGE);
             return false;
            }
          else
            return true;   
       }
    }
  
    public boolean validarSalon(){
        Estudiante est = (Estudiante) usuarioActual;
        boolean tieneSalon;
        if (est.getSalon()==null){
            tieneSalon = false;
        }
        else {
            tieneSalon=true;
        }
        return tieneSalon;
    }
    
    
    
    public void iniciaVentana(JFrame ventana, String ruta){
        ventana.setLocationRelativeTo(null); //permite centrar la ventana
        ventana.setIconImage(new ImageIcon(ruta).getImage()); 
        ventana.setResizable(false); 
        ventana.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //para evitar cerrar con la "X", solo se sale de la ventana a través
        // del botón Salir del Sistema.
    }
    
    public Usuario buscarUsuario(String correo){
        for (Usuario user : listaUsuarios){
            if (user.getCorreo().equalsIgnoreCase(correo)) {
                return user;
            }
        }
        return null;
    }
    
    public boolean registrarEstudiante(JTextField nombre, JTextField apellido, JTextField correo, JPasswordField clave){
        boolean avanzar;
        if (esVacio(nombre,"Debe indicar su nombre")==false && esVacio(apellido,"Debe indicar su apellido")==false && esVacio(correo,"Debe indicar un usuario")==false && esVacio(clave,"Debe indicar una contraseña")==false && validarCampoTexto(nombre)==true && validarCampoTexto(apellido)==true && validarContrasena(clave)==true){
            Usuario estudianteRegistrado = buscarUsuario(correo.getText());
            if (estudianteRegistrado == null){
                Estudiante estudianteNuevo = new Estudiante();
                estudianteNuevo.setNombre(nombre.getText());
                estudianteNuevo.setApellido(apellido.getText());
                estudianteNuevo.setCorreo(correo.getText());
                String contrasena = new String(clave.getPassword());
                estudianteNuevo.setContrasena(contrasena);/// CONVERTIR A STRING
                registrarUsuarioFirebase(nombre.getText(),apellido.getText(),correo.getText(), contrasena, "estudiante");
                JOptionPane.showMessageDialog(null,"Usuario registrado exitosamente","",JOptionPane.INFORMATION_MESSAGE);
                this.usuarioActual= estudianteNuevo;
                avanzar = true;
            }
            else{
                JOptionPane.showMessageDialog(null,"Los datos ingresados coinciden con un usuario que ya se ha registrado","Usuario ya registrado",JOptionPane.ERROR_MESSAGE);
                avanzar = false;
            }
        }
        else {
            avanzar = false;
        }
        return avanzar;
    }
    
    public boolean registrarUsuarioFirebase(String nombre, String apellido, String correo, String password, String rolSeleccionado){
        Firestore db = FirestoreClient.getFirestore();

        try {
            // 1. Referencia al contador y al futuro documento del usuario
            DocumentReference contadorRef = db.collection("config").document("contadores");
            DocumentReference usuarioRef = db.collection("usuarios").document(correo);

            // 2. EJECUTAR TRANSACCIÓN (Atómica)
            // Esto asegura que nadie más tome el ID mientras lo leemos
            Long nuevoId = db.runTransaction(new Transaction.Function<Long>() {
                @Override
                public Long updateCallback(Transaction transaction) throws Exception {
                    // A. Leer el último ID
                    DocumentSnapshot snapshot = transaction.get(contadorRef).get();
                    Long ultimoId = snapshot.getLong("ultimoIdUsuario");
                    if (ultimoId == null) ultimoId = 0L;

                    // B. Calcular el nuevo ID (+1)
                    Long siguienteId = ultimoId + 1;

                    // C. Verificar que el correo no exista ya (opcional pero recomendado)
                    DocumentSnapshot usuarioSnapshot = transaction.get(usuarioRef).get();
                    if (usuarioSnapshot.exists()) {
                        throw new Exception("El correo ya está registrado.");
                    }

                    // D. Preparar los datos del usuario para guardar
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("idUsuario", siguienteId); // ¡Aquí va el ID numérico!
                    datos.put("nombre", nombre);
                    datos.put("apellido", apellido);
                    datos.put("correo", correo);
                    datos.put("password", password);
                    datos.put("rol", rolSeleccionado);

                    // --- Inicializar atributos vacíos (Requerimiento 2 y 4) ---
                    if ("estudiante".equals(rolSeleccionado)) {
                        datos.put("puntos", 0);
                        datos.put("grado", 0);         // Por defecto
                        datos.put("seccion", "");      // Por defecto
                        datos.put("idSalon", null);    // Sin salón
                        // (Logros y progreso se manejarán como sub-colecciones luego)
                    } else {
                        // Es maestro
                        // (Salones se manejarán como sub-colección o array luego)
                    }

                    // E. Escribir todo en la base de datos
                    transaction.set(usuarioRef, datos);                 // Guardar usuario
                    transaction.update(contadorRef, "ultimoIdUsuario", siguienteId); // Actualizar contador

                    return siguienteId; // Devolvemos el ID para usarlo en local
                }
            }).get(); // .get() ejecuta la transacción

            // 3. ACTUALIZAR ESTADO LOCAL (this.usuarioActual)
            // Ya tenemos el ID generado (nuevoId), creamos el objeto Java
            if ("estudiante".equals(rolSeleccionado)) {
                // Constructor: id, nombre, apellido, correo, pass, grado, seccion, salon, progreso, logros
                Estudiante nuevoEst = new Estudiante(
                    nuevoId.intValue(), nombre, apellido, correo, password, 
                    0, ' ', null, null, new ArrayList<>()
                );
                // Inicializar progreso (tu clase lo pide en constructor o setter)
                // nuevoEst.setProgreso(new Progreso(nuevoEst)); 
                
                this.usuarioActual = nuevoEst;

            } else {
                Maestro nuevoMstro = new Maestro(
                    nuevoId.intValue(), nombre, apellido, correo, password
                );
                this.usuarioActual = nuevoMstro;
            }

            System.out.println("Registro exitoso. ID asignado: " + nuevoId);
            return true;

        } catch (Exception e) {
            System.err.println("Error en registro: " + e.getMessage());
            return false;
        }
    }
    
    public boolean registrarMaestro(JTextField nombre, JTextField apellido, JTextField correo, JPasswordField clave){
        boolean avanzar;
        if (esVacio(nombre,"Debe indicar su nombre")==false && esVacio(apellido,"Debe indicar su apellido")==false && esVacio(correo,"Debe indicar un nombre de usuario")==false && esVacio(clave,"Debe indicar una contraseña")==false && validarCampoTexto(nombre)==true && validarCampoTexto(apellido)==true  && validarContrasena(clave)==true){
            Usuario maestroRegistrado = buscarUsuario(correo.getText());
            if (maestroRegistrado == null){
                Maestro maestroNuevo = new Maestro();
                maestroNuevo.setNombre(nombre.getText());
                maestroNuevo.setApellido(apellido.getText());
                maestroNuevo.setCorreo(correo.getText());
                String contrasena = new String(clave.getPassword());
                maestroNuevo.setContrasena(contrasena);/// CONVERTIR A STRING
                registrarUsuarioFirebase(nombre.getText(),apellido.getText(),correo.getText(), contrasena, "maestro");
                JOptionPane.showMessageDialog(null,"Usuario registrado exitosamente","",JOptionPane.INFORMATION_MESSAGE);
                this.usuarioActual = maestroNuevo;
                avanzar = true;
            }
            else{
                JOptionPane.showMessageDialog(null,"Los datos ingresados coinciden con un usuario que ya se ha registrado","Usuario ya registrado",JOptionPane.ERROR_MESSAGE);
                avanzar = false;
            }
        }
        else {
            avanzar = false;
        }
        return avanzar;
    }
    
    public Usuario validarInicioSesion(String correo, String contrasena){
        for (Usuario user : listaUsuarios){
            if (user.getCorreo().equalsIgnoreCase(correo) && user.getContrasena().equals(contrasena)) {
                return user;
            }
        }
        return null;
    }
    
    public List<Estudiante> getEstudiantes(){
        
        List<Estudiante> listaFiltrada = new ArrayList<>();
        
        for (Usuario user : this.listaUsuarios){
            if (user instanceof Estudiante){
                listaFiltrada.add((Estudiante)user);
            }
        }
        return listaFiltrada;
    }
    
    public List<Maestro> getMaestro(){
        
        List<Maestro> listaFiltrada = new ArrayList<>();
        
        for (Usuario user : this.listaUsuarios){
            if (user instanceof Maestro){
                listaFiltrada.add((Maestro)user);
            }
        }
        return listaFiltrada;
    }
    
    public Usuario iniciarSesionFirebase(String correoLogin, String passLogin) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection("usuarios").document(correoLogin);

            ApiFuture<DocumentSnapshot> futuro = docRef.get();
            DocumentSnapshot documento = futuro.get();

            if (documento.exists()) {
                // Verificar contraseña
                String passReal = documento.getString("password");
                if (passReal == null || !passReal.equals(passLogin)) {
                    return null;
                }

                String rol = documento.getString("rol");
                // Leer el ID numérico (con seguridad anti-nulos)
                Long idLong = documento.getLong("idUsuario");
                int idLeido = (idLong != null) ? idLong.intValue() : 0; 

                if ("estudiante".equals(rol)) {
                    Estudiante est = new Estudiante();
                    // Llenar datos de Usuario (Padre)
                    est.setIdUsuario(idLeido);
                    est.setNombre(documento.getString("nombre"));
                    est.setApellido(documento.getString("apellido")); // Asegúrate de guardar apellido en el registro
                    est.setCorreo(correoLogin);
                    est.setContrasena(passReal);
                    
                    // Llenar datos de Estudiante
                    Long pts = documento.getLong("puntos");
                    // est.setPuntos(...) -> Tu clase Estudiante NO tiene atributo puntos visible en el archivo que pasaste, 
                    // parece que lo maneja la clase Progreso. Lo dejaremos pendiente para el punto 3.
                    
                    Long idSalonLong = documento.getLong("idSalon");
                    if (idSalonLong != null && idSalonLong != 0) {
                        int idSalon = idSalonLong.intValue();
                        est.setGrado(documento.getLong("grado").intValue());
                        String secStr = documento.getString("seccion");
                        est.setSeccion(secStr != null ? secStr.charAt(0) : ' ');

                        // 1. Buscamos el documento del SALÓN en Firebase
                        DocumentSnapshot salonDoc = db.collection("salones").document(String.valueOf(idSalon)).get().get();
                        
                        if (salonDoc.exists()) {
                            // 2. Creamos el objeto Salón
                            Salon s = new Salon();
                            s.setIdSalon(idSalon);
                            s.setGrado(est.getGrado());
                            s.setSeccion(est.getSeccion());

                            // 3. Creamos un "Maestro falso" solo con los datos necesarios para mostrar
                            // (Esto evita el NullPointerException en consultarInfoSalon)
                            Maestro mInfo = new Maestro();
                            mInfo.setIdUsuario(salonDoc.getLong("idMaestro").intValue());
                            // El nombre suele venir separado, aquí asumimos que guardaste "nombreMaestro" en el salón
                            // Si no, pon un texto genérico o haz otra consulta para buscar al maestro.
                            String nombreCompleto = salonDoc.getString("nombreMaestro"); 
                            if (nombreCompleto != null) {
                                String[] partes = nombreCompleto.split(" ");
                                mInfo.setNombre(partes[0]);
                                mInfo.setApellido(partes.length > 1 ? partes[1] : "");
                            }
                            //mInfo.setCorreo("Contactar maestro"); // Opcional

                            s.setMaestro(mInfo); // Asignamos el maestro al salón
                            est.setSalon(s);     // Asignamos el salón al estudiante
                        }
                    }
                    
                    // CARGAR NIVELES DESBLOQUEADOS
                    Map<String, Object> nivelesMap = (Map<String, Object>) documento.get("progresoNiveles");
                    
                    if (nivelesMap != null) {
                        for (Map.Entry<String, Object> entry : nivelesMap.entrySet()) {
                            String nombreTemaGuardado = entry.getKey();   // Ej: "Sumas"
                            String nombreNivel = (String) entry.getValue(); // Ej: "MEDIO"
                            
                            try {
                                // 1. Buscar el objeto Tema real usando el nombre
                                Tema temaObjeto = buscarTemaPorNombre(nombreTemaGuardado);
                                
                                // 2. Convertir el nivel a Enum
                                NivelDificultad nivelEnum = NivelDificultad.valueOf(nombreNivel);
                                
                                // 3. Si encontramos el tema, lo agregamos al progreso local
                                if (temaObjeto != null) {
                                    // Asumiendo que Progreso tiene un mapa público o un método setter
                                    // Si 'nivelesDesbloqueados' es privado, necesitas un método en Progreso.java:
                                    // public void desbloquearNivel(Tema t, NivelDificultad n) { this.nivelesDesbloqueados.put(t, n); }
                                    est.getProgreso().desbloquearNivel(temaObjeto, nivelEnum); 
                                } else {
                                    System.err.println("Advertencia: Se encontró progreso para el tema '" + nombreTemaGuardado + "' pero ese tema no existe en la app.");
                                }

                            } catch (Exception e) {
                                System.err.println("Error cargando nivel para tema " + nombreTemaGuardado + ": " + e.getMessage());
                            }
                        }
                    }
                    
                    List<Long> idsLogros = (List<Long>) documento.get("logrosIds");
                    if (idsLogros != null) {
                        for (Long idLogroLong : idsLogros) {
                            Logro l = buscarLogroPorId(idLogroLong.intValue());
                            if (l != null) {
                                est.agregarLogro(l); // Método de tu clase Estudiante
                            }
                        }
                    }
                    
                    Long rachaGuardada = documento.getLong("diasRachaGuardada");
                    String ultimaFecha = documento.getString("ultimaFechaPractica");
                    
                    if (rachaGuardada != null) {
                        int racha = rachaGuardada.intValue();
                        
                        // Lógica de validación de fecha:
                        // Si la última fecha fue "antes de ayer", la racha se rompió y vuelve a 0.
                        // Si fue ayer o hoy, se mantiene.
                        if (ultimaFecha != null) {
                            LocalDate fechaUltima = LocalDate.parse(ultimaFecha);
                            LocalDate ayer = LocalDate.now().minusDays(1);
                            
                            if (fechaUltima.isBefore(ayer)) {
                                racha = 0; // Se rompió la racha por no practicar ayer
                            }
                        }
                        
                        // Inyectamos el valor en el objeto Progreso
                        // (Asegúrate de tener este setter en Progreso.java)
                        est.getProgreso().setDiasRacha(racha); 
                    }
                    
                    this.usuarioActual = est;

                } else if ("maestro".equals(rol)) {
                    Maestro mstro = new Maestro();
                    mstro.setIdUsuario(idLeido);
                    mstro.setNombre(documento.getString("nombre"));
                    mstro.setApellido(documento.getString("apellido"));
                    mstro.setCorreo(correoLogin);
                    mstro.setContrasena(passReal);
                    
                    this.usuarioActual = mstro;
                    
                    //Cargar salones del maestro
                    List<QueryDocumentSnapshot> salonesDocs = db.collection("salones")
                        .whereEqualTo("idMaestro", mstro.getIdUsuario())
                        .get().get().getDocuments(); // .get() futuro -> .get() lista
                
                    ArrayList<Salon> misSalones = new ArrayList<>();
                
                    for (DocumentSnapshot doc : salonesDocs) {
                    // Reconstruir objeto Salon
                        Long idSal = doc.getLong("idSalon");
                        Long grad = doc.getLong("grado");
                        String sec = doc.getString("seccion");
                    
                        Salon s = new Salon(
                            grad.intValue(), 
                            sec.charAt(0), 
                            mstro, idSal.intValue()
                        );
                        misSalones.add(s);
                    
                    // También agregarlo a la lista global "listaSalones" si la usas
                    if (this.listaSalones == null) this.listaSalones = new ArrayList<>();
                    this.listaSalones.add(s);
                    }
                
                    mstro.setSalones(misSalones);
                }
                
                System.out.println("Login: " + this.usuarioActual.getNombre() + " (ID: " + this.usuarioActual.getIdUsuario() + ")");
                return usuarioActual;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /* SUPRIMIR SUPRIMIR
    public Usuario iniciarSesionUsuario(JTextField correo, JPasswordField clave){
        if (esVacio(correo,"Debe indicar su nombre de usuario")==false && esVacio(clave,"Debe indicar su contraseña")==false){
            String contrasena = new String(clave.getPassword());
            Usuario user = validarInicioSesion(correo.getText(),contrasena);
            if (user == null){
                JOptionPane.showMessageDialog(null,"Verifique los datos ingresados","Usuario no encontrado",JOptionPane.ERROR_MESSAGE);
                return user;
            }
            else {
                JOptionPane.showMessageDialog(null,"Inicio de sesion exitoso","BIENVENIDO!",JOptionPane.INFORMATION_MESSAGE);
                this.usuarioActual = user;
                return user;
                
            }
        }
        else {
            return null;
        }   
        
    }
    */
    public void MostrarPerfilEstudiante(JLabel nombre, JLabel apellido, JLabel correo, JLabel clave, JLabel grado, JLabel seccion){
        Estudiante estudianteActual = (Estudiante) usuarioActual;
        nombre.setText(estudianteActual.getNombre());
        apellido.setText(estudianteActual.getApellido());
        correo.setText(estudianteActual.getCorreo());
        clave.setText(String.valueOf(estudianteActual.getContrasena()));
        if (estudianteActual.getGrado()==0){
            grado.setFont(new Font("Cy Grotesk Key", Font.ITALIC, 14));
            grado.setText("No asignado");
            seccion.setFont(new Font("Cy Grotesk Key", Font.ITALIC, 14));
            seccion.setText("No asignado");
            
        }
        else {
            grado.setText(String.valueOf(estudianteActual.getGrado()));
            seccion.setText(String.valueOf(estudianteActual.getSeccion()));

        }  
    }
    
    
    public void MostrarPerfilMaestro(JLabel nombre, JLabel apellido, JLabel correo, JLabel clave, JTable salones){
        Maestro maestroActual = (Maestro) usuarioActual;
        nombre.setText(maestroActual.getNombre());
        apellido.setText(maestroActual.getApellido());
        correo.setText(maestroActual.getCorreo());
        clave.setText(String.valueOf(maestroActual.getContrasena()));
        
        List<Salon> listaS = maestroActual.getSalones();
        String[] columna = {"ID","GRADO","SECCIÓN"};
       
        DefaultTableModel dtm = new DefaultTableModel(null, columna) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Esta tabla no es editable
            }
        };

        for (Salon salon : listaS)
            {
                String[] row = {Long.toString(salon.getIdSalon()), Long.toString(salon.getGrado()),String.valueOf(salon.getSeccion()) };
                dtm.addRow(row);
            }
        salones.setModel(dtm);
        
        JTableHeader header = salones.getTableHeader();
        Font fuenteHeader = new Font("Cy Grotesk Key", Font.BOLD, 14);
        header.setFont(fuenteHeader);
        salones.getTableHeader().setForeground(new Color(40,66,119));
    }
    
    public void establecerDatosDelPerfil(JTextField nombre, JTextField apellido, JTextField correo, JTextField contrasena){
        nombre.setText(usuarioActual.getNombre());
        apellido.setText(usuarioActual.getApellido());
        correo.setText(usuarioActual.getCorreo());
        contrasena.setText(String.valueOf(usuarioActual.getContrasena()));
    }
    
    public void establecerDatosDelPerfil(JLabel nombre, JLabel apellido, JLabel correo, JLabel contrasena){
        nombre.setText(usuarioActual.getNombre());
        apellido.setText(usuarioActual.getApellido());
        correo.setText(usuarioActual.getCorreo());
        contrasena.setText(String.valueOf(usuarioActual.getContrasena()));
    }
    
    public boolean actualizarPerfilFirebase(String correo, String nombre, String apellido, String contrasena, String rol) {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("apellido", apellido);
        datos.put("correo", correo);
        datos.put("contrasena", contrasena);
        datos.put("rol", rol);
            
        try {
            ApiFuture<WriteResult> resultado = db.collection("usuarios").document(correo).set(datos);
            resultado.get();
            System.out.println("Datos guardados en Firebase a las: " + resultado.get().getUpdateTime());
            JOptionPane.showMessageDialog(null, "¡Perfil actualizado en la nube!");  
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar el perfil en la nube.");
            return false;
        }
    }
    
    public boolean editarPerfil(JTextField nombre, JTextField apellido, JTextField correo, JTextField clave){
        boolean avanzar;
        if (esVacio(nombre,"Debe indicar su nombre")==false && esVacio(apellido,"Debe indicar su apellido")==false && esVacio(correo,"Debe indicar un usuario")==false && esVacio(clave,"Debe indicar una contraseña")==false){
            if (correo.getText().equals(usuarioActual.getCorreo())){
                usuarioActual.setNombre(nombre.getText());
                usuarioActual.setApellido(apellido.getText());
                usuarioActual.setContrasena(clave.getText());
                avanzar = true;
            }
            else {
                Usuario user = buscarUsuario(correo.getText());
                if (user == null){
                    usuarioActual.setNombre(nombre.getText());
                    usuarioActual.setApellido(apellido.getText());
                    usuarioActual.setContrasena(clave.getText());
                    usuarioActual.setCorreo(correo.getText());
                    avanzar = true;
                }
                else{
                    JOptionPane.showMessageDialog(null,"El correo proporcionado coincide con uno ya registrado, por favor ingrese otro.","Usuario ya registrado",JOptionPane.ERROR_MESSAGE);
                    avanzar = false;
                }
            
            }
            
        }
        else {
            avanzar = false;
        }
        return avanzar;
    }
    
    private void verificarYAsignarLogros(Estudiante est) {
        Progreso prog = est.getProgreso();

        // itera sobre los logros que existen en el juego
        for (Logro logro : this.listaLogros) { 

            // revisa si el estudiante YA tiene este logro
            if (est.getLogros().contains(logro)) {
                continue; //si ya lo tiene al siguiente
            }
            Tema temaDelLogro = logro.getTema();
            int puntosNecesarios = logro.getPuntosNecesarios();

            int puntosDelEstudianteEnEseTema = prog.getPuntajeTotalPorTema(temaDelLogro);

            if (puntosDelEstudianteEnEseTema >= puntosNecesarios) {
                est.agregarLogro(logro);
                JOptionPane.showMessageDialog(null, "¡Nuevo logro desbloqueado: " + logro.getNombre() + "!");
            }
        }
    }
    
    public void mostrarProgreso(JTable logros, JLabel puntosE, JLabel racha) {
        Estudiante estudianteActual = (Estudiante) usuarioActual;

        // calcula los puntos totales (suma de puntos de todos los temas)
        int puntosTotales = estudianteActual.getProgreso().getPuntajeTotalGeneral();
        puntosE.setText(String.valueOf(puntosTotales) + " Puntos");

        
        // actualiza la lista de logros del estudiante
        verificarYAsignarLogros(estudianteActual);

        List<Logro> listaL = estudianteActual.getLogros();

        ImageIcon expertoResta = new ImageIcon(getClass().getResource("/imagenes/iconos/experto_resta.png"));
        ImageIcon geometraFiguras = new ImageIcon(getClass().getResource("/imagenes/iconos/geometra_figuras.png"));
        ImageIcon maestroSuma = new ImageIcon(getClass().getResource("/imagenes/iconos/maestro_suma.png"));
        ImageIcon novatoSuma = new ImageIcon(getClass().getResource("/imagenes/iconos/novato_suma.png"));

        String[] columna = {"MEDALLA","TEMA","NOMBRE","DESCRIPCION","PUNTOS"};

        DefaultTableModel dtm = new DefaultTableModel(null, columna) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) { return Icon.class; }
                return super.getColumnClass(columnIndex);
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Object [] row = new Object[5];
        for (Logro logro : listaL)
        {            
            if (logro.getNombre().equals("Novato de la Suma")){
                row[0] = novatoSuma;
            } else if (logro.getNombre().equals("Maestro de la Suma")){
                row[0] = maestroSuma;
            } else if (logro.getNombre().equals("Geómetra")){
                row[0] = geometraFiguras;
            } else if (logro.getNombre().equals("Experto en Restas")){
                row[0] = expertoResta;
            } else {
                row[0] = null; // O un icono por defecto
            }

            row[1] = logro.getTema().getNombre(); 
            row[2] = logro.getNombre();
            row[3] = logro.getDescripcion();
            row[4] = logro.getPuntosNecesarios();

            dtm.addRow(row);
        }
        logros.setModel(dtm);

        JTableHeader header = logros.getTableHeader();
        Font fuenteHeader = new Font("Cy Grotesk Key", Font.BOLD, 14);
        header.setFont(fuenteHeader);
        logros.getTableHeader().setForeground(new Color(40,66,119));
        
        int diasDeRacha = estudianteActual.getProgreso().getDiasRacha();
        String dRacha = Integer.toString(diasDeRacha);
        racha.setText(dRacha);
    }
    //DADDY
    public Salon buscarSalonID(int id){
        Firestore db = FirestoreClient.getFirestore();
        try {
            // Buscamos el documento con el ID "1", "2", etc.
            DocumentSnapshot doc = db.collection("salones").document(String.valueOf(id)).get().get();

            if (doc.exists()) {
                // Reconstruimos el objeto Salon desde la nube
                Salon s = new Salon();
                s.setIdSalon(id);
                s.setGrado(doc.getLong("grado").intValue());
                String seccionStr = doc.getString("seccion");
                s.setSeccion(seccionStr != null ? seccionStr.charAt(0) : ' ');
                
                // Nota: No traemos el objeto Maestro completo aquí para no hacerlo lento,
                // pero traemos su nombre por si acaso se necesita mostrar.
                // s.setNombreMaestro(doc.getString("nombreMaestro")); 

                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // No existe
    }
     
    public boolean agregarSolicitud(JTextField idField) {
        if (!(usuarioActual instanceof Estudiante)) return false;
        
        try {
            int idSalonSolicitado = Integer.parseInt(idField.getText());
            Firestore db = FirestoreClient.getFirestore();
            
            // 1. Verificar si el salón existe
            DocumentReference salonRef = db.collection("salones").document(String.valueOf(idSalonSolicitado));
            DocumentSnapshot salonDoc = salonRef.get().get();
            
            if (!salonDoc.exists()) {
                JOptionPane.showMessageDialog(null, "No se encontró el salón ID: " + idSalonSolicitado, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // 2. AGREGAR SOLICITUD (Atomicamente)
            // Esto agrega mi ID a la lista "solicitudesIds" SOLO si no está ya ahí.
            ApiFuture<WriteResult> writeResult = salonRef.update("solicitudesIds", FieldValue.arrayUnion(usuarioActual.getIdUsuario()));
            writeResult.get(); // Esperar a que se guarde

            JOptionPane.showMessageDialog(null, "Solicitud enviada al salón " + idSalonSolicitado, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe ser un número", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al enviar solicitud", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public void consultarInfoSalon(JLabel grado, JLabel seccion, JLabel maestro, JLabel correoMaestro){
        Estudiante est = (Estudiante) usuarioActual;
        if (est.getGrado()==0){
            grado.setFont(new Font("Cy Grotesk Key", Font.ITALIC, 18));
            grado.setText("No asignado");
            seccion.setFont(new Font("Cy Grotesk Key", Font.ITALIC, 18));
            seccion.setText("No asignado");
            maestro.setFont(new Font("Cy Grotesk Key", Font.ITALIC, 18));
            maestro.setText("No asignado");
            correoMaestro.setText(" ");
        }
        else {
            grado.setFont(new Font("Cy Grotesk Key", Font.PLAIN,36));
            grado.setText(String.valueOf(est.getGrado()));
            seccion.setFont(new Font("Cy Grotesk Key", Font.PLAIN,36));
            seccion.setText(String.valueOf(est.getSeccion()));
            
            String nombreM = est.getSalon().getMaestro().getNombre();
            String apellidoM = est.getSalon().getMaestro().getApellido();
            maestro.setFont(new Font("Cy Grotesk Key", Font.PLAIN,36));
            maestro.setText(nombreM +" "+ apellidoM);
            correoMaestro.setText(est.getSalon().getMaestro().getCorreo());
        }
    }
    
    public void llenarComboSalones(JComboBox idSalones){
        Maestro maestroActual = (Maestro) usuarioActual;
        List<Salon> salones = maestroActual.getSalones();
        for (Salon s : salones){
            idSalones.addItem(s.getIdSalon());
        }
    }
    
    public void mostrarSolicitudes(int idSalon, JList listaSolicitudes) {
        Firestore db = FirestoreClient.getFirestore();
        DefaultListModel<Estudiante> dtm = new DefaultListModel<>();
        dtm.clear();

        try {
            // 1. Obtener el documento del salón para ver la lista de IDs
            DocumentSnapshot salonDoc = db.collection("salones").document(String.valueOf(idSalon)).get().get();
            
            // Leemos el array de IDs de Firebase
            List<Long> idsSolicitantes = (List<Long>) salonDoc.get("solicitudesIds");

            if (idsSolicitantes != null && !idsSolicitantes.isEmpty()) {
                // 2. Por cada ID, buscar al estudiante en la colección "usuarios"
                for (Long idLong : idsSolicitantes) {
                    int idEst = idLong.intValue();
                    
                    // Buscamos el usuario donde idUsuario == idEst
                    Query query = db.collection("usuarios").whereEqualTo("idUsuario", idEst);
                    QuerySnapshot querySnapshot = query.get().get();

                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                        
                        // Crear objeto Estudiante temporal para mostrarlo en la lista
                        Estudiante est = new Estudiante();
                        est.setIdUsuario(idEst);
                        est.setNombre(userDoc.getString("nombre"));
                        est.setApellido(userDoc.getString("apellido"));
                        est.setCorreo(userDoc.getString("correo"));
                        
                        dtm.addElement(est);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        listaSolicitudes.setModel(dtm);
    }
    
    public void mostrarIDSalon(JLabel idNuevoSalon){
        Maestro maestroActual = (Maestro) usuarioActual;
        Salon ultimoSalonAgregado = maestroActual.getSalones().get(maestroActual.getSalones().size()-1);
        if (ultimoSalonAgregado==null){
            idNuevoSalon.setText("");
            JOptionPane.showMessageDialog(null,"No se han creado salones","Error mostrar ID",JOptionPane.ERROR_MESSAGE);
        }
        else{
            idNuevoSalon.setText(String.valueOf(ultimoSalonAgregado.getIdSalon()));
        }
    }
    
    public void mostrarEstudiantesSinSalon(JList listaE){
        DefaultListModel<Estudiante> dtm = new DefaultListModel<>();
        for (Estudiante est : estudiantesRegistrados) {
            if (est.getSalon() == null) {
                dtm.addElement(est);
            }
        }
        listaE.setModel(dtm);
    }
    
    public Salon ultimoSalonAgregado(){
        Maestro maestroActual = (Maestro) usuarioActual;
        Salon ultimoSalon = maestroActual.getSalones().get(maestroActual.getSalones().size()-1);
        return ultimoSalon;
    }
    
    public void llenarComboEstudiantes(JComboBox estudiantes,Salon salonSeleccionado){
        cargarEstudiantesDelSalon(salonSeleccionado);
        
        List<Estudiante> lista = salonSeleccionado.getListaEstudiantes();
        estudiantes.removeAllItems();
        for (Estudiante est : lista){
            estudiantes.addItem(est);
        }
    }
    
    // Método auxiliar para descargar los estudiantes reales de un salón específico
    public void cargarEstudiantesDelSalon(Salon salon) {
        if (salon == null) return;
        
        Firestore db = FirestoreClient.getFirestore();
        try {
            // 1. Buscamos el documento del salón para obtener la lista actualizada de IDs
            DocumentSnapshot salonDoc = db.collection("salones").document(String.valueOf(salon.getIdSalon())).get().get();
            List<Long> idsEstudiantes = (List<Long>) salonDoc.get("listaEstudiantesIds");

            // Limpiamos la lista local para no duplicar
            salon.getListaEstudiantes().clear();

            if (idsEstudiantes != null && !idsEstudiantes.isEmpty()) {
                // 2. Por cada ID, buscamos al estudiante real
                for (Long idLong : idsEstudiantes) {
                    QuerySnapshot q = db.collection("usuarios").whereEqualTo("idUsuario", idLong).get().get();
                    if (!q.isEmpty()) {
                        DocumentSnapshot userDoc = q.getDocuments().get(0);
                        
                        Estudiante est = new Estudiante();
                        est.setIdUsuario(idLong.intValue());
                        est.setNombre(userDoc.getString("nombre"));
                        est.setApellido(userDoc.getString("apellido"));
                        est.setCorreo(userDoc.getString("correo"));
                        est.setSalon(salon);
                        
                        // Agregamos a la lista local del salón
                        salon.getListaEstudiantes().add(est);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean sacarEstudiante(JComboBox comboIdSalon, JComboBox comboEstudiante) {
        try {
            // Obtener datos del combo
            int idSalon = (int) comboIdSalon.getSelectedItem();
            Estudiante est = (Estudiante) comboEstudiante.getSelectedItem();

            if (est == null) return false;

            Firestore db = FirestoreClient.getFirestore();
            
            // Referencias
            DocumentReference salonRef = db.collection("salones").document(String.valueOf(idSalon));
            DocumentReference estudianteRef = db.collection("usuarios").document(est.getCorreo()); // Usamos correo como ID del documento usuario

            // 1. Eliminar ID del estudiante de la lista del salón
            salonRef.update("listaEstudiantesIds", FieldValue.arrayRemove(est.getIdUsuario()));

            // 2. Actualizar documento del estudiante (quitarle el salón)
            Map<String, Object> actualizaciones = new HashMap<>();
            actualizaciones.put("idSalon", null);
            actualizaciones.put("grado", 0);
            actualizaciones.put("seccion", "");
            
            estudianteRef.update(actualizaciones).get();

            // 3. Actualizar LOCALMENTE (para que se refleje en la pantalla sin recargar)
            est.setSalon(null);
            est.setGrado(0);
            est.setSeccion(' ');
            
            // Buscar el salón local y sacarlo de la lista
            Maestro maestroActual = (Maestro) usuarioActual;
            for(Salon s : maestroActual.getSalones()){
                if(s.getIdSalon() == idSalon){
                    s.getListaEstudiantes().remove(est);
                    break;
                }
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean aceptarSolicitud(int idSalon, Estudiante estudiante) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            // Referencias a documentos
            DocumentReference salonRef = db.collection("salones").document(String.valueOf(idSalon));
            DocumentReference estudianteRef = db.collection("usuarios").document(estudiante.getCorreo());
            
            // 1. Obtener datos del salón para saber grado y sección correctos
            DocumentSnapshot salonSnap = salonRef.get().get();
            if (!salonSnap.exists()) return false;
            
            Long grado = salonSnap.getLong("grado");
            String seccion = salonSnap.getString("seccion");

            // 2. ACTUALIZAR FIREBASE (Batch/Lote para que sea seguro)
            // a. Quitar de solicitudes y agregar a inscritos en el SALÓN
            salonRef.update("solicitudesIds", FieldValue.arrayRemove(estudiante.getIdUsuario()));
            salonRef.update("listaEstudiantesIds", FieldValue.arrayUnion(estudiante.getIdUsuario()));
            
            // b. Actualizar datos del ESTUDIANTE
            Map<String, Object> updatesEstudiante = new HashMap<>();
            updatesEstudiante.put("idSalon", idSalon);
            updatesEstudiante.put("grado", grado);
            updatesEstudiante.put("seccion", seccion);
            estudianteRef.update(updatesEstudiante).get(); // .get() para esperar que termine

            // 3. ACTUALIZAR OBJETOS LOCALES (Para que la vista se actualice sin reiniciar)
            // Actualizar estudiante
            estudiante.setGrado(grado.intValue());
            estudiante.setSeccion(seccion.charAt(0));
            // (Nota: Como 'salon' es un objeto, lo ideal sería buscar el objeto Salon local y asignarlo,
            // pero por ahora null o buscarlo si es crítico).
            
            // Actualizar Salón Local (buscarlo en la lista del maestro)
            if (usuarioActual instanceof Maestro) {
                Maestro m = (Maestro) usuarioActual;
                for (Salon s : m.getSalones()) {
                    if (s.getIdSalon() == idSalon) {
                        s.getListaSolicitudes().remove(estudiante);
                        s.getListaEstudiantes().add(estudiante);
                        estudiante.setSalon(s); // Ahora sí enlazamos el objeto
                        break;
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //YANKE
    
    public void llenarDatosCrearSalon(JComboBox grado, JComboBox seccion){
        int num = 1;
        while (num<=6){
            grado.addItem(num);
            num = num+1;
        }
        seccion.addItem('A');
        seccion.addItem('B');
        seccion.addItem('C');
        seccion.addItem('D');
    }
    
    private Salon registrarSalonEnFirebase(Maestro maestro, int grado, char seccion) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference contadorRef = db.collection("config").document("contadores");

            // Ejecutamos la transacción
            Salon nuevoSalon = db.runTransaction(new Transaction.Function<Salon>() {
                @Override
                public Salon updateCallback(Transaction transaction) throws Exception {
                    // 1. Leer último ID
                    DocumentSnapshot snapshot = transaction.get(contadorRef).get();
                    Long ultimoId = snapshot.getLong("ultimoIdSalon");
                    if (ultimoId == null) ultimoId = 0L;

                    Long siguienteId = ultimoId + 1;

                    // 2. Referencia al nuevo documento
                    DocumentReference salonRef = db.collection("salones").document(String.valueOf(siguienteId));

                    // 3. Preparar datos
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("idSalon", siguienteId);
                    datos.put("grado", grado);
                    datos.put("seccion", String.valueOf(seccion));
                    datos.put("idMaestro", maestro.getIdUsuario()); // Guardamos el ID del creador
                    datos.put("nombreMaestro", maestro.getNombre() + " " + maestro.getApellido());
                    
                    // Listas vacías iniciales
                    datos.put("listaEstudiantesIds", new ArrayList<Integer>());
                    datos.put("solicitudesIds", new ArrayList<Integer>());

                    // 4. Escribir en BD
                    transaction.set(salonRef, datos);
                    transaction.update(contadorRef, "ultimoIdSalon", siguienteId);

                    // 5. Devolver el objeto para uso local
                    // Asegúrate de tener este constructor en Salon.java
                    //int grado, char seccion, Maestro maestro, int idSalon
                    return new Salon(grado, seccion, maestro, siguienteId.intValue());
                }
            }).get();

            return nuevoSalon;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void crearSalon(JComboBox grado, JComboBox seccion) {
        // 1. Verificación de seguridad
        if (!(usuarioActual instanceof Maestro)) return;
        
        Maestro maestroActual = (Maestro) usuarioActual;

        // 2. Obtener datos de la vista
        int g = (int) grado.getSelectedItem();
        char s = (char) seccion.getSelectedItem();

        // 3. LLAMADA A FIREBASE (Aquí es donde cambia)
        // En lugar de calcular el ID a mano, dejamos que el método de arriba lo haga
        Salon nuevoSalon = registrarSalonEnFirebase(maestroActual, g, s);

        if (nuevoSalon != null) {
            // 4. Actualizar listas LOCALES (para que se vea en la app sin reiniciar)
            // (Asegurarse de que las listas no sean null)
            if (listaSalones == null) listaSalones = new ArrayList<>();
            listaSalones.add(nuevoSalon);
            
            if (maestroActual.getSalones() == null) maestroActual.setSalones(new ArrayList<>());
            maestroActual.getSalones().add(nuevoSalon);

            JOptionPane.showMessageDialog(null, 
                "Nuevo Salon creado: " + nuevoSalon.getGrado() + "° '" + nuevoSalon.getSeccion() + "'\nID Asignado: " + nuevoSalon.getIdSalon(), 
                "Creacion exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la nube.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean eliminarSalon(JTextField idSalonField) {
        try {
            Maestro maestroActual = (Maestro) usuarioActual;
            int idSalon = Integer.parseInt(idSalonField.getText());
            
            // 1. Buscar el salón en la lista local
            Salon salonAEliminar = null;
            for (Salon s : maestroActual.getSalones()) {
                if (s.getIdSalon() == idSalon) {
                    salonAEliminar = s;
                    break;
                }
            }

            if (salonAEliminar != null) {
                Firestore db = FirestoreClient.getFirestore();
                
                // 2. BORRAR DE FIREBASE
                // Borramos el documento del salón
                db.collection("salones").document(String.valueOf(idSalon)).delete();

                // 3. ACTUALIZAR ESTUDIANTES (Opcional pero recomendado)
                // En teoría deberíamos buscar a todos los estudiantes de ese salón 
                // en la BD y ponerles idSalon = null. 
                // Por ahora, lo haremos solo a nivel local para no complicar el código hoy.
                for (Estudiante est : salonAEliminar.getListaEstudiantes()) {
                    est.setSalon(null);
                    est.setGrado(0);
                    est.setSeccion(' ');
                    // AQUÍ FALTARÍA: Actualizar cada estudiante en Firebase
                }

                // 4. BORRAR DE LISTAS LOCALES
                if (listaSalones != null) listaSalones.remove(salonAEliminar);
                maestroActual.getSalones().remove(salonAEliminar);
                
                return true;
            } else {
                return false; // No se encontró o no pertenece al maestro
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /* SUPRIMIR SUPRIMIR
    public void crearSalon(JComboBox grado, JComboBox seccion){
        Maestro maestroActual = (Maestro) usuarioActual;
        Salon nuevoSalon = new Salon();
        nuevoSalon.setGrado((int) grado.getSelectedItem());
        nuevoSalon.setSeccion((char) seccion.getSelectedItem());
        nuevoSalon.setMaestro(maestroActual);
        
        Salon ultimoSalonAgregado = listaSalones.get(listaSalones.size()-1);
        if (ultimoSalonAgregado==null){
            nuevoSalon.setIdSalon(0);
        }
        else {
            nuevoSalon.setIdSalon(ultimoSalonAgregado.getIdSalon()+1);
        }
        
        listaSalones.add(nuevoSalon);
        maestroActual.getSalones().add(nuevoSalon);
        JOptionPane.showMessageDialog(null,"Nuevo Salon creado: "+nuevoSalon.getGrado()+"° '"+nuevoSalon.getSeccion()+"'","Creacion exitosa",JOptionPane.INFORMATION_MESSAGE);
    }
    
    public boolean eliminarSalon(JTextField idSalon){
        boolean encontrado;
        Maestro maestroActual = (Maestro) usuarioActual;
        int id =  Integer.parseInt(idSalon.getText());
        Salon salonEliminar = new Salon();
        for (Salon s : listaSalones){
            if (s.getIdSalon()==id){
                salonEliminar = s;
            }
        }
        if (salonEliminar != null){
            for (Estudiante est : salonEliminar.getListaEstudiantes()){
                est.setSalon(null);
                est.setGrado(0);
                est.setSeccion(' ');
            }
            listaSalones.remove(salonEliminar);
            maestroActual.getSalones().remove(salonEliminar);
            encontrado = true;

        }
        else{
            encontrado = false;
        }
        return encontrado;
    }
    */
    
    
    private int getGrupoDeGrado(Estudiante est) { // obtener 1 si el estudiante es de 1er a 3er grado, 4 si es de 4to a 6to grado
        int gradoEspecifico = est.getGrado();
        if (gradoEspecifico >= 1 && gradoEspecifico <= 3) {
            return 1; // grupo 1
        } else {
            return 4; // grupo 4
        } 
    }
    
    public List<Tema> getTemasPadre() {
        List<Tema> temasPadre = new ArrayList<>();
        for (Tema t : this.listaTemas) {
            if (t.getTemaPadre() == null) {
                temasPadre.add(t);
            }
        }
        return temasPadre;
    }
    
    public boolean iniciarNuevaPractica() {
        if (this.usuarioActual == null || !(this.usuarioActual instanceof Estudiante)) { //el usuario es estduante
             JOptionPane.showMessageDialog(null, "Error: No hay un estudiante logueado.", "Error", JOptionPane.ERROR_MESSAGE);
             return false;
        }
        
        Estudiante est = (Estudiante) this.usuarioActual;
        if (est.getSalon() == null) { // si el estudiante no tiene salon no puede realizar la practica
            JOptionPane.showMessageDialog(null, 
                "¡Aún no perteneces a un salón!\nDebes unirte a un salón o ser aceptado por tu maestro para poder jugar.", 
                "Acción Requerida", 
                JOptionPane.INFORMATION_MESSAGE);
            return false; // NO DEJA JUGAR
        }
        
        if (this.temaSeleccionado == null || this.nivelSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Error: No se ha seleccionado Tema o Nivel para la práctica.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Tema tema = this.temaSeleccionado; // Usar el tema guardado
        NivelDificultad nivel = this.nivelSeleccionado; // Usar el nivel guardado
        //obtener grupo y nivel del estudiante
        int gradoGrupo = getGrupoDeGrado(est); 
       
        List<Ejercicio> ejerciciosCompletos = this.filtroPractica.getEjerciciosFiltrados(tema, nivel, gradoGrupo);
        
        // resetear los contadores
        this.resultadosTemporales.clear();
        this.indicePreguntaActual = 0;
        
    
        if (ejerciciosCompletos.isEmpty()) {
            JOptionPane.showMessageDialog(null,"¡Próximamente! No hay ejercicios de '" + tema.getNombre() + "'...", "Contenido Faltante",JOptionPane.INFORMATION_MESSAGE );
            return false;
        }
        
        this.practicaActual = new ArrayList<>(ejerciciosCompletos);
        Collections.shuffle(this.practicaActual); // mezcla la lista para no obtener los ejercicios siempre en el mismo orden

        this.resultadosTemporales.clear();
        this.ejerciciosFallados.clear();     // limpia la lista de fallos
        this.esRondaDeRepeticion = false;  // resetea el flag de repeticion

        return true;
    }

    public Ejercicio getSiguientePregunta() {
        if (!practicaActual.isEmpty()) {
            return practicaActual.remove(0);
        }    
        if (!esRondaDeRepeticion) {
            esRondaDeRepeticion = true;
            if (!ejerciciosFallados.isEmpty()) {
                this.practicaActual = new ArrayList<>(ejerciciosFallados);
                this.ejerciciosFallados.clear();
                Collections.shuffle(this.practicaActual);  
                return practicaActual.remove(0); 
            }
        }
        return null;
    }

//DADDY 2    
    private void verificarLogrosGanados(Tema tema) {
        Estudiante est = (Estudiante) this.usuarioActual;
        int puntosActuales = est.getProgreso().getPuntajeTotalPorTema(tema); // Asegúrate que este método funcione con lo que cargamos
        
        // Nota: Como no descargamos TODOS los resultados al login, el puntaje total
        // debería venir del campo "puntos" que cargamos en iniciarSesion.
        // Si 'getPuntajeTotalPorTema' calcula sumando la lista vacía, dará 0.
        // *Corrección rápida*: Usaremos los puntos totales del estudiante para logros generales
        // o asumiremos que la lógica local funciona para la sesión actual.
        
        for (Logro logro : this.listaLogros) { 
            // Verificamos si es del tema y si NO lo tiene ya
            if (logro.getTema().equals(tema)) {
                // Verificamos en la lista local (que llenaremos al login)
                boolean yaLoTiene = false;
                for(Logro l : (ArrayList<Logro>)est.getLogros()){ // Casteo si es necesario
                    if(l.getId() == logro.getId()){
                        yaLoTiene = true;
                        break;
                    }
                }

                if (!yaLoTiene) {
                    if (puntosActuales >= logro.getPuntosNecesarios()) {
                        // 1. Agregar Localmente
                        est.agregarLogro(logro);
                        
                        // 2. NUEVO: Guardar en Firebase (ArrayUnion para no repetir)
                        guardarLogroEnNube(est, logro);

                        JOptionPane.showMessageDialog(null, "¡Nuevo Logro Desbloqueado!\n\n" + logro.getNombre(), "¡Logro Obtenido!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }

    // Método pequeño para escribir en Firebase
    private void guardarLogroEnNube(Estudiante est, Logro logro) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            db.collection("usuarios").document(est.getCorreo())
              .update("logrosIds", FieldValue.arrayUnion(logro.getId())); // Guardamos solo el ID
        } catch (Exception e) {
            System.err.println("Error guardando logro: " + e.getMessage());
        }
    }
    
    private void guardarResultadoEnNube(Resultado res) {
        if (!(usuarioActual instanceof Estudiante)) return;

        Firestore db = FirestoreClient.getFirestore();
        try {
            // Referencia al documento del estudiante
            DocumentReference estudianteRef = db.collection("usuarios").document(usuarioActual.getCorreo());
            
            // Referencia a la nueva SUB-COLECCIÓN "resultados" (se crea sola)
            // Usamos un ID automático (.document()) porque son muchos resultados
            DocumentReference nuevoResultadoRef = estudianteRef.collection("resultados").document();

            Map<String, Object> datos = new HashMap<>();
            
            // Guardamos datos básicos
            datos.put("fecha", res.getFecha().toString()); // LocalDate a String (ISO-8601)
            datos.put("esCorrecto", res.isEsCorrecto());
            datos.put("puntos", res.getPuntos());
            datos.put("intentos", res.getIntentos());
            datos.put("tiempoSegundos", res.getTiempoTardado().getSeconds()); // Duration a segundos
            datos.put("respuestaUsuario", res.getRespuestaUsuario());
            
            // Guardamos info del ejercicio para saber qué respondió
            datos.put("idEjercicio", res.getEjercicio().getIdEjercicio()); // Asumiendo que Ejercicio tiene ID
            datos.put("tema", res.getEjercicio().getTema().getNombre());
            datos.put("nivel", res.getEjercicio().getDificultad().toString());

            // Escribir en la nube (Asíncrono, no bloqueamos la UI con .get())
            nuevoResultadoRef.set(datos);
            
            // TAMBIÉN: Actualizar puntos totales en el documento principal del estudiante
            // (Usamos FieldValue.increment para sumar de forma atómica y segura)
            if (res.getPuntos() > 0) {
                estudianteRef.update("puntos", FieldValue.increment(res.getPuntos()));
            }

            Map<String, Object> updatesRacha = new HashMap<>();
            updatesRacha.put("ultimaFechaPractica", LocalDate.now().toString());
            
            // Obtenemos la racha calculada localmente (que ahora sí tendrá datos de hoy)
            int rachaActual = ((Estudiante)usuarioActual).getProgreso().getDiasRacha();
            
            // Corrección: Si es el primer ejercicio del día y la lista estaba vacía al login,
            // getDiasRacha() podría dar 1 (hoy). Pero si teníamos racha acumulada de ayer, 
            // necesitamos sumarle.
            // Para simplificar: Confiaremos en que al login cargaremos el valor base.
            
            updatesRacha.put("diasRachaGuardada", rachaActual); 
            
            estudianteRef.update(updatesRacha);
            
        } catch (Exception e) {
            System.err.println("Error guardando resultado en nube: " + e.getMessage());
            // No mostramos JOptionPane para no interrumpir al estudiante en cada pregunta
        }
    }
    
    public Resultado registrarRespuesta(Ejercicio ejercicio, String respuestaUsuario) { 
        // 1. Lógica Original (Calculos Locales)
        boolean fueCorrecto = ejercicio.validarRespuesta(respuestaUsuario);
        int puntosGanados = fueCorrecto ? ejercicio.getValorPuntos() : 0;
        
        Duration tiempoTotal = Duration.ZERO;
        if (this.tiempoInicioPregunta != null) {
            tiempoTotal = java.time.Duration.between(this.tiempoInicioPregunta, java.time.Instant.now());
        }
    
        // Crear objeto Resultado
        Resultado res = new Resultado((Estudiante) usuarioActual, ejercicio, java.time.LocalDate.now(), 1, tiempoTotal, fueCorrecto, puntosGanados, respuestaUsuario);

        // Actualizar listas LOCALES
        this.resultadosTemporales.add(res);
        ((Estudiante)usuarioActual).getProgreso().agregarResultado(res);

        if (!fueCorrecto && !esRondaDeRepeticion) {
            this.ejerciciosFallados.add(ejercicio);
        }

        // 2. LOGICA NUEVA: GUARDAR EN FIREBASE (Subcolección)
        guardarResultadoEnNube(res); // <--- Llamamos a una función auxiliar para mantener limpio este método

        // 3. Verificar Logros y Retornar
        if (fueCorrecto) {
            verificarLogrosGanados(ejercicio.getTema());
        }  
        return res;
    }
    
    public Ejercicio mostrarSiguientePreguntaEnVista(
            java.awt.CardLayout cardLayoutPrincipal, 
            javax.swing.JPanel panelContenedor, 

            // 1. PREGUNTA: Usamos Labels Separados (Estrategia de Paneles)
            javax.swing.JLabel lblImagenPreguntaSeleccion, javax.swing.JPanel panelImagenSeleccion,
            javax.swing.JLabel lblImagenPreguntaEscrita, javax.swing.JPanel panelImagenEscrita,

            // 2. TEXTOS DE PREGUNTA
            javax.swing.JLabel lblPreguntaTexto, 
            javax.swing.JLabel lblPreguntaEscrita, 
            javax.swing.JTextField txtRespuestaEscrita,

            // 3. OPCIONES: Usamos JToggleButtons Reutilizados (Estrategia de Botones)
            javax.swing.JToggleButton btnA, javax.swing.JToggleButton btnB, 
            javax.swing.JToggleButton btnC, javax.swing.JToggleButton btnD,
            javax.swing.ButtonGroup grupoOpciones) {

        Ejercicio ej = getSiguientePregunta();
        if (ej == null) {
            finalizarPractica(); 
            return null;
        }

        // Guardar referencia
        // this.ejercicioActual = ej; 
        this.tiempoInicioPregunta = java.time.Instant.now();
        javax.swing.JToggleButton[] botones = {btnA, btnB, btnC, btnD};

        // --- ESTRATEGIA 1: PREGUNTA (Separada) ---
        // Si hay imagen en la pregunta, mostramos el panel dedicado.
        if (ej.tieneImagen()) {
            cargarImagen(ej.getRutaImagen(), lblImagenPreguntaSeleccion);
            cargarImagen(ej.getRutaImagen(), lblImagenPreguntaEscrita);
            panelImagenSeleccion.setVisible(true);
            panelImagenEscrita.setVisible(true);
        } else {
            panelImagenSeleccion.setVisible(false);
            panelImagenEscrita.setVisible(false);
        }

        // Decisión de Tipo de Ejercicio (Escrito vs Selección)
        java.util.Random rnd = new java.util.Random();
        boolean esEscrito = rnd.nextBoolean();
        // Regla de Negocio: Si el ejercicio dice "Solo Seleccion"
        if (ej.isForzarSeleccion()) {
            esEscrito = false; //debe ser de seleccion
        }
        if (ej.getOpciones() == null || ej.getOpciones().length < 4) esEscrito = true;

        if (esEscrito) {
            // MODO ESCRITO
            lblPreguntaEscrita.setText(ej.getPregunta());
            txtRespuestaEscrita.setText("");
            cardLayoutPrincipal.show(panelContenedor, "EjercicioEscrito");
        } else {
            // MODO SELECCION
            lblPreguntaTexto.setText(ej.getPregunta());
            grupoOpciones.clearSelection();

            if (ej.tieneOpcionesConImagen()) {
                // CASO: Opciones son imagenes
                String[] rutas = ej.getRutasOpciones();
                for (int i = 0; i < 4; i++) {
                    botones[i].setText(""); // Borramos texto
                    cargarImagen(rutas[i], botones[i]); // Ponemos icono

                    // centrar imagen
                    botones[i].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                }
            } else {
                // cuando las opciones son texto
                String[] textos = ej.getOpciones();
                for (int i = 0; i < 4; i++) {
                    botones[i].setIcon(null); // Borramos icono (IMPORTANTE)
                    botones[i].setText(textos[i]); // Ponemos texto
                }
            }
            cardLayoutPrincipal.show(panelContenedor, "EjercicioSeleccion");
        }

        return ej;
    }

    private void actualizarNivelEnNube(Estudiante est, Tema tema) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            // Obtenemos el nuevo nivel que acaba de ganar
            NivelDificultad nivelActual = est.getProgreso().getNivelActual(tema);
            
            // En Firebase, podemos guardar esto como un mapa: "niveles.MATEMATICAS" = "INTERMEDIO"
            // Nota: Asumo que en Firebase el campo se llamará "progresoNiveles"
            
            DocumentReference estRef = db.collection("usuarios").document(est.getCorreo());
            
            // Actualizamos el campo específico de ese tema
            // Usamos la sintaxis de punto para actualizar campos anidados en un mapa
            estRef.update("progresoNiveles." + tema.getNombre(), nivelActual.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void finalizarPractica() {
        if (this.resultadosTemporales.isEmpty()) return;

        Estudiante est = (Estudiante) this.usuarioActual;
        Tema tema = this.resultadosTemporales.get(0).getEjercicio().getTema();

        //LOGICA DEL 70%: si el 70% de los resultados de los ejercicios es correcto pasa de nivel, sino se mantiene
        double aciertos = 0;
        double totalIntentos = 0;

        for (Resultado r : this.resultadosTemporales) {
            // Opción A (Estricta): Contar todos los intentos (incluyendo los fallidos)
            totalIntentos++;
            if (r.isEsCorrecto()) {
                aciertos++;
            }
        }

        // Evitar división por cero
        if (totalIntentos == 0) return;

        double porcentaje = (aciertos / totalIntentos); 
        // Convertir a formato 0-100 para mostrar, pero usar 0.0-1.0 para lógica
        boolean aprobo = porcentaje >= 0.70; // 70% para aprobar

        // --- MENSAJES Y SUBIDA DE NIVEL ---

        // Formatear porcentaje para que se vea bonito (ej: "85%")
        String porcentajeTexto = String.format("%.0f%%", porcentaje * 100);

        if (aprobo) {
            // Lógica para subir de nivel en el Progreso del estudiante
            // (Asume que tu clase Progreso tiene este método)
            boolean subioNivel = est.getProgreso().subirNivel(tema);

            if (subioNivel) {
                actualizarNivelEnNube(est, tema); // <--- Llamada a función auxiliar
            }
            
            String mensaje = "¡Felicidades!\nAprobaste la práctica con " + porcentajeTexto + " de aciertos.";
            if (subioNivel) {
                mensaje += "\n\n¡HAS SUBIDO DE NIVEL EN " + tema.getNombre().toUpperCase() + "!";
            } else {
                mensaje += "\n\nYa estás en el nivel máximo o mantienes tu nivel.";
            }

            JOptionPane.showMessageDialog(null, mensaje, "¡Práctica Superada!", JOptionPane.INFORMATION_MESSAGE);

        } else {
            // Mensaje de ánimo
            // Calculamos cuántos aciertos hubieran sido necesarios para el 70%
            int necesarios = (int) Math.ceil(totalIntentos * 0.7);

            String mensaje = "¡Buen intento!\nObtuviste " + porcentajeTexto + " de aciertos.";
            mensaje += "\nNecesitas al menos 70% (" + necesarios + " aciertos) para avanzar de nivel.";
            mensaje += "\n\n¡Sigue practicando, tú puedes!";

            JOptionPane.showMessageDialog(null, mensaje, "Práctica Finalizada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

//YANKE 2    
    public List<Salon> getSalonesDelMaestro() {
        List<Salon> salonesMaestro = new ArrayList<>();
        if (usuarioActual instanceof Maestro) {
            for (Salon s : this.listaSalones) {
                if (s.getMaestro().equals(usuarioActual)) {
                    salonesMaestro.add(s);
                }
            }
        }
        return salonesMaestro;
    }
    
    public Salon buscarSalon(int grado, String seccion) {
        if (seccion == null || seccion.isEmpty()) {
            return null;
        }

        for (Salon s : this.listaSalones) {
            boolean gradoCoincide = (s.getGrado() == grado);
            boolean seccionCoincide = (Character.toUpperCase(s.getSeccion()) == Character.toUpperCase(seccion.charAt(0)));

            if (gradoCoincide && seccionCoincide) {
                return s; // se encontro
            }
        }
        return null;
    }
    
    private Reporte generarReporte(Salon salon, PeriodoReporte periodo, Estudiante est) {
        if (salon == null) return null;

        AnalizadorSalon analizador = new AnalizadorSalon(salon);
        Reporte datos = analizador.generarDatosReporte(periodo, est, this.listaTemas);

        if (datos == null) { 
            return null; 
        }
        if (est == null && (datos.getRanking() == null || datos.getRanking().isEmpty())) {
            return null; 
        }
        if (est != null && datos.getDatosIndividuales() == null) {
            return null; 
        }

        return datos;
    }

    public boolean mostrarRankingSalon(Salon salon, PeriodoReporte periodo, JTable tabla) {
        Reporte datos = generarReporte(salon, periodo, null);
        if (datos == null) return false;
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Posición", "Nombre", "Apellido", "Puntos (Período)"}, 0
        );
        int posicion = 1;
        for (RankingEntry entry : datos.getRanking()) {
            model.addRow(new Object[]{
                posicion,
                entry.getEstudiante().getNombre(),
                entry.getEstudiante().getApellido(),
                entry.getPuntaje()
            });
            posicion++;
        }
        tabla.setModel(model);
        return true;
    }
    
    public boolean mostrarTablaDetalladaSalon(Salon salon, PeriodoReporte periodo, JTable tabla) {
        // 1. Obtiene los datos
        Reporte datos = generarReporte(salon, periodo, null);
        if (datos == null || datos.getDatosPorTema() == null || datos.getDatosPorTema().isEmpty()) return false;

        // 2. Construye la tabla (coincide con tu imagen)
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Tema", "Intentos Totales", "% Aciertos", "Puntos Generados"}, 0
        );

        for (ReporteDatosPorTema d : datos.getDatosPorTema().values()) {
            String porcFormateado = String.format("%.2f%%", d.getPorcentajeAciertos());
            model.addRow(new Object[]{
                d.getNombreTema(),
                d.getIntentosTotales(),
                porcFormateado,
                d.getPuntosTotales()
            });
        }
        tabla.setModel(model);
        return true;
    }
    
    public boolean mostrarGraficoDetalladoSalon(Salon salon, PeriodoReporte periodo, JPanel panelContenedor) {
        // 1. Obtiene los datos
        Reporte datos = generarReporte(salon, periodo, null);
        if (datos == null || datos.getDatosPorTema() == null || datos.getDatosPorTema().isEmpty()) return false;

        // 2. CREAR EL SET DE DATOS
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ReporteDatosPorTema d : datos.getDatosPorTema().values()) {
            dataset.addValue(d.getPorcentajeAciertos(), "% Aciertos", d.getNombreTema());
        }

        // 3. GENERAR EL GRÁFICO (como en tu GeneradorExcel)
        JFreeChart barChart = ChartFactory.createBarChart(
            "Desempeño Promedio: Salón " + salon.getGrado() + "-" + salon.getSeccion(),
            "Temas", "% Aciertos", dataset,
            PlotOrientation.VERTICAL, true, true, false
        );

        // 4. MOSTRAR EL PANEL
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setMouseWheelEnabled(true);
        panelContenedor.removeAll();
        panelContenedor.add(chartPanel, BorderLayout.CENTER); // (Tu panel debe tener BorderLayout)
        panelContenedor.revalidate();
        panelContenedor.repaint();
        return true;
    }
    
    public boolean mostrarTablaDetalladaEstudiante(Salon salon, PeriodoReporte periodo, Estudiante est, JTable tabla) {
        // 1. Obtiene los datos
        Reporte datos = generarReporte(salon, periodo, est); // ¡Pasa el Estudiante!

        if (datos == null || datos.getDatosIndividuales() == null) return false;

        // 2. Obtiene los resultados del progreso del estudiante (ya filtrados por el Analizador)
        List<Resultado> historial = est.getProgreso().getResultados(
                                        datos.getFechaInicio(), 
                                        datos.getFechaFin()
                                    );

        if (historial.isEmpty()) return false;

        // 3. Construye la tabla (solo UI)
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Fecha", "Tema", "Pregunta", "Respuesta", "Correcta", "Puntos"}, 0
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Resultado res : historial) {
            model.addRow(new Object[]{
                res.getFecha().format(formatter),
                res.getEjercicio().getTema().getNombre(),
                res.getEjercicio().getPregunta(),
                res.getRespuestaUsuario(), 
                res.isEsCorrecto() ? "Sí" : "No",
                res.getPuntos() 
            });
        }
        tabla.setModel(model);
        return true;
    }
    
    public boolean mostrarGraficoIndividual(Salon salon, PeriodoReporte periodo, Estudiante est, JPanel panelContenedor) {
    
        // 1. Obtiene los datos (esta vez SÍ pasamos el estudiante)
        Reporte datos = generarReporte(salon, periodo, est);

        if (datos == null || datos.getDatosIndividuales() == null) return false;

        Map<String, Double> promedios = datos.getDatosIndividuales().getPromedioPorTema();

        if (promedios == null || promedios.isEmpty()) {
            return false; // No hay datos de temas para graficar
        }

        // 2. CREAR EL SET DE DATOS
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : promedios.entrySet()) {
            dataset.addValue(entry.getValue(), "% Aciertos", entry.getKey());
        }

        // 3. GENERAR EL GRÁFICO
        JFreeChart barChart = ChartFactory.createBarChart(
            "Desempeño por Tema de " + est.getNombre(), // Título
            "Temas", "% Aciertos", dataset,
            PlotOrientation.VERTICAL, true, true, false
        );

        // 4. MOSTRAR EL PANEL
        // (Asegúrate que tu panel 'graficoEstReportesMaestro' tenga BorderLayout)
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setMouseWheelEnabled(true);
        panelContenedor.removeAll();
        panelContenedor.add(chartPanel, BorderLayout.CENTER); 
        panelContenedor.revalidate();
        panelContenedor.repaint();
        return true;
    }
    
    public void exportarReporte(
        Salon salon, 
        PeriodoReporte periodo, 
        Estudiante est, 
        FormatoArchivo formato, 
        FormatoVisual visual
) {
    
        // 1. Genera los datos (usando el helper que YA tenemos)
        Reporte datos = generarReporte(salon, periodo, est);

        if (datos == null) {
            JOptionPane.showMessageDialog(null, "No se encontraron datos para exportar en este período.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Crea el 'trabajador' (Generador)
        IGeneradorReporte generador;

        if (formato == FormatoArchivo.PDF) {
            generador = new GeneradorPDF(visual);
        } else {
            generador = new GeneradorExcel(visual);
        }

        // 3. Guarda el archivo (y maneja cualquier error)
        try {
            generador.guardar(datos);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al exportar el archivo: " + e.getMessage(), "Error de Exportación", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Muestra el error en la consola
        }
    }
    
    public void generarReporteAvanzado(ConfiguracionReporte config) throws Exception {
        AnalizadorSalon analizador = new AnalizadorSalon(config.getSalon());

        Reporte datosDelReporte = analizador.generarDatosReporte(
                config.getPeriodo(), 
                config.getEstudiante(),
                this.listaTemas
            );

        IGeneradorReporte generador;
        if (config.getFormatoArchivo() == FormatoArchivo.EXCEL) {
            generador = new GeneradorExcel(config.getFormatoVisual());
        } else {
            generador = new GeneradorPDF(config.getFormatoVisual());
        }

        generador.guardar(datosDelReporte);
    }
    
    public void cargarNivelesPermitidos(Tema tema, javax.swing.JComboBox<String> cboDificultad) {
        // 1. Limpiar el combo
        cboDificultad.removeAllItems();

        if (tema == null || this.usuarioActual == null) return;

        Estudiante est = (Estudiante) this.usuarioActual;

        // 2. Obtener el nivel ACTUAL del estudiante en ese tema
        // (Asumo que Progreso.getNivelActual devuelve el nivel máximo alcanzado)
        NivelDificultad nivelMaximoAlcanzado = est.getProgreso().getNivelActual(tema);

        // Si el estudiante nunca ha tocado este tema, su nivel máximo es BAJO
        if (nivelMaximoAlcanzado == null) {
            nivelMaximoAlcanzado = NivelDificultad.BAJO;
        }

        // 3. Iterar sobre TODOS los niveles posibles
        for (NivelDificultad nivelPosible : NivelDificultad.values()) {

            // 4. LÓGICA DE ORO: Comparar los "ordinales"
            // Si el nivel posible es MENOR o IGUAL al nivel máximo alcanzado, se agrega.
            // Ej: Si estoy en OPTIMO (2), agrego: BAJO(0), MEDIO(1) y OPTIMO(2).
            if (nivelPosible.ordinal() <= nivelMaximoAlcanzado.ordinal()) {
                cboDificultad.addItem(nivelPosible.toString());
            }
        }

        // 5. Seleccionar por defecto el nivel máximo (el último de la lista agregada)
        cboDificultad.setSelectedItem(nivelMaximoAlcanzado.toString());
    }
    
    private void cargarImagen(String rutaImagen, javax.swing.JComponent componente) {
        if (rutaImagen == null || rutaImagen.isEmpty()) {
            // Limpiar si no hay ruta
            if (componente instanceof javax.swing.JLabel) {
                ((javax.swing.JLabel) componente).setIcon(null);
            } else if (componente instanceof javax.swing.AbstractButton) {
                ((javax.swing.AbstractButton) componente).setIcon(null);
            }
            return;
        }

        try {
            // Carga robusta usando recursos (funciona en NetBeans y en el JAR final)
            java.net.URL url = getClass().getResource("/imagenes/" + rutaImagen);

            if (url != null) {
                javax.swing.ImageIcon icono = new javax.swing.ImageIcon(url);

                // OPCIONAL: Redimensionar imagen si es para un BOTÓN (Opciones)
                if (componente instanceof javax.swing.AbstractButton) {
                     // Ajusta este tamaño (ej: 100x100) según el tamaño de tus botones azules
                    java.awt.Image imgEscalada = icono.getImage().getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH);
                    icono = new javax.swing.ImageIcon(imgEscalada);
                }
                // OPCIONAL: Redimensionar si es para la PREGUNTA (JLabel)
                else if (componente instanceof javax.swing.JLabel) {
                    // Las preguntas suelen ser más grandes
                    java.awt.Image imgEscalada = icono.getImage().getScaledInstance(250, 200, java.awt.Image.SCALE_SMOOTH);
                    icono = new javax.swing.ImageIcon(imgEscalada);
                }

                // Asignar el icono
                if (componente instanceof javax.swing.JLabel) {
                    ((javax.swing.JLabel) componente).setIcon(icono);
                } else if (componente instanceof javax.swing.AbstractButton) {
                    ((javax.swing.AbstractButton) componente).setIcon(icono);
                }
            } else {
                System.err.println("Imagen no encontrada: " + rutaImagen);
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
        }
    }
    
    public void configurarPractica(Tema tema, String nombreNivel) {
        this.temaSeleccionado = tema;

        // convertir el string del ComboBox al enum NivelDificultad
        try {
            this.nivelSeleccionado = NivelDificultad.valueOf(nombreNivel); 
        } catch (IllegalArgumentException e) {
            System.err.println("Error al convertir nivel: " + nombreNivel);
            this.nivelSeleccionado = NivelDificultad.BAJO; 
        }
    }
    
    // Método auxiliar para buscar un objeto Tema por su nombre
    public Tema buscarTemaPorNombre(String nombre) {
        // Recorremos tu lista maestra de temas
        // (Ajusta 'this.listaTemas' al nombre real de tu lista de temas)
        if (this.listaTemas != null) {
            for (Tema t : this.listaTemas) {
                if (t.getNombre().equalsIgnoreCase(nombre)) {
                    return t;
                }
                // Si tienes subtemas (temas hijos), quizás debas buscar dentro de ellos también
                // Pero para empezar, busquemos en el nivel principal.
            }
        }
        return null; // No se encontró
    }
    
    // Método auxiliar para buscar un Logro por su ID numérico
    public Logro buscarLogroPorId(int id) {
        // Asumo que tienes una lista maestra llamada 'listaLogros' en el controlador
        if (this.listaLogros != null) {
            for (Logro l : this.listaLogros) {
                if (l.getId() == id) {
                    return l;
                }
            }
        }
        return null;
    }
    
}