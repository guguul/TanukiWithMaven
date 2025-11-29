//controlador
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

import java.util.stream.Collectors;
import modelo_tanuki.ConfiguracionReporte;
import modelo_tanuki.FormatoVisual;
import modelo_tanuki.PeriodoReporte;
import modelo_tanuki.RankingEntry;
import modelo_tanuki.Reporte;
import modelo_tanuki.ReporteDatosIndividual;
import modelo_tanuki.ReporteDatosPorTema;
import modelo_tanuki.ReporteDetalleTemaEstudiante;

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
import java.util.HashSet;
import java.util.Set;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

import javax.sound.sampled.*; 
import java.io.IOException;
import java.net.URL;

import org.jfree.chart.ChartPanel;

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
    private Reporte reporteActualEnPantalla;
    
    public SistemaControlador(){
        DatosPrecargados datos = new DatosPrecargados();
        
        
            
        this.listaUsuarios = datos.getUsuarios();
        this.listaTemas = datos.getTemas();
        this.listaLogros = datos.getLogros();
        this.listaSalones = datos.getSalones();
        this.filtroPractica = new FiltroPractica();
        this.listaLogros = new ArrayList<>(); // Inicializar por seguridad
        cargarLogrosMaestros();
        
        for (Usuario user: listaUsuarios){
            if (user instanceof Estudiante){
                this.estudiantesRegistrados.add((Estudiante)user);
            }
        }
        
    }

    public Reporte getReporteActualEnPantalla() {
        return reporteActualEnPantalla;
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

    public boolean registrarEstudiante(JTextField nombre, JTextField apellido, JTextField username, JPasswordField clave){
        boolean avanzar;
        if (esVacio(nombre,"Debe indicar su nombre")==false && esVacio(apellido,"Debe indicar su apellido")==false && esVacio(username,"Debe indicar un usuario")==false && esVacio(clave,"Debe indicar una contraseña")==false && validarCampoTexto(nombre)==true && validarCampoTexto(apellido)==true && validarContrasena(clave)==true){
            String contrasena = new String(clave.getPassword());
            registrarUsuarioFirebase(nombre.getText(),apellido.getText(),username.getText(), contrasena, "estudiante");

            if (existe==false){
                Estudiante estudianteNuevo = new Estudiante();
                estudianteNuevo.setNombre(nombre.getText());
                estudianteNuevo.setApellido(apellido.getText());
                estudianteNuevo.setUsername(username.getText());
                estudianteNuevo.setContrasena(contrasena);
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
    
    boolean existe;
    public boolean registrarUsuarioFirebase(String nombre, String apellido, String username, String password, String rolSeleccionado){
        Firestore db = FirestoreClient.getFirestore();
        
        try {
            // 1. Referencia al contador y al futuro documento del usuario
            DocumentReference contadorRef = db.collection("config").document("contadores");
            DocumentReference usuarioRef = db.collection("usuarios").document(username);

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

                    // C. Verificar que el username no exista ya (opcional pero recomendado)
                    DocumentSnapshot usuarioSnapshot = transaction.get(usuarioRef).get();
                    if (usuarioSnapshot.exists()) {
                        existe=true;
                        throw new Exception("El username ya está registrado.");
                        
                    }
                    existe = false;

                    // D. Preparar los datos del usuario para guardar
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("idUsuario", siguienteId); // ¡Aquí va el ID numérico!
                    datos.put("nombre", nombre);
                    datos.put("apellido", apellido);
                    datos.put("username", username);
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
                // Constructor: id, nombre, apellido, username, pass, grado, seccion, salon, progreso, logros
                Estudiante nuevoEst = new Estudiante(
                    nuevoId.intValue(), nombre, apellido, username, password, 
                    0, ' ', null, null, new ArrayList<>()
                );
                // Inicializar progreso (tu clase lo pide en constructor o setter)
                // nuevoEst.setProgreso(new Progreso(nuevoEst)); 
                
                this.usuarioActual = nuevoEst;

            } else {
                Maestro nuevoMstro = new Maestro(
                    nuevoId.intValue(), nombre, apellido, username, password
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
    
    public boolean registrarMaestro(JTextField nombre, JTextField apellido, JTextField username, JPasswordField clave){
        boolean avanzar;
        if (esVacio(nombre,"Debe indicar su nombre")==false && esVacio(apellido,"Debe indicar su apellido")==false && esVacio(username,"Debe indicar un nombre de usuario")==false && esVacio(clave,"Debe indicar una contraseña")==false && validarCampoTexto(nombre)==true && validarCampoTexto(apellido)==true  && validarContrasena(clave)==true){
            String contrasena = new String(clave.getPassword());
            registrarUsuarioFirebase(nombre.getText(),apellido.getText(),username.getText(), contrasena, "maestro");

            if (existe==false){
                Maestro maestroNuevo = new Maestro();
                maestroNuevo.setNombre(nombre.getText());
                maestroNuevo.setApellido(apellido.getText());
                maestroNuevo.setUsername(username.getText());
                maestroNuevo.setContrasena(contrasena);
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
    
    public Usuario iniciarSesionFirebase(String usernameLogin, String passLogin) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection("usuarios").document(usernameLogin);

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
                    est.setUsername(usernameLogin);
                    est.setContrasena(passReal);
                    
                    // Llenar datos de Estudiante
                    Long pts = documento.getLong("puntos");
                    int puntosTotales = (pts != null) ? pts.intValue() : 0;
                    
                    // GUARDAMOS EN LA NUEVA VARIABLE
                    est.getProgreso().setPuntosAcumulados(puntosTotales);
                    
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
                            //mInfo.setusername("Contactar maestro"); // Opcional

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
                                Tema temaObjeto = buscarTemaPorNombre(nombreTemaGuardado);
                                
                                NivelDificultad nivelEnum = NivelDificultad.valueOf(nombreNivel);
                                
                                if (temaObjeto != null) {
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
                    
                    cargarHistorialDeResultados(est);

                } else if ("maestro".equals(rol)) {
                    Maestro mstro = new Maestro();
                    mstro.setIdUsuario(idLeido);
                    mstro.setNombre(documento.getString("nombre"));
                    mstro.setApellido(documento.getString("apellido"));
                    mstro.setUsername(usernameLogin);
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
    
    public Usuario iniciarSesionUsuario(JTextField username, JPasswordField clave){
        if (esVacio(username,"Debe indicar su nombre de usuario")==false && esVacio(clave,"Debe indicar su contraseña")==false){
            String contrasena = new String(clave.getPassword());
            Usuario user = iniciarSesionFirebase(username.getText(),contrasena);
            if (user == null){
                JOptionPane.showMessageDialog(null,"Verifique los datos ingresados","Usuario no encontrado",JOptionPane.ERROR_MESSAGE);
                return user;
            }
            else {
                this.usuarioActual = user;
                return user;
                
            }
        }
        else {
            return null;
        }   
        
    }
    
    public void MostrarPerfilEstudiante(JLabel nombre, JLabel apellido, JLabel username, JLabel clave, JLabel grado, JLabel seccion){
        Estudiante estudianteActual = (Estudiante) usuarioActual;
        nombre.setText(estudianteActual.getNombre());
        apellido.setText(estudianteActual.getApellido());
        username.setText(estudianteActual.getUsername());
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
    
    
    public void MostrarPerfilMaestro(JLabel nombre, JLabel apellido, JLabel username, JLabel clave, JTable salones){
        Maestro maestroActual = (Maestro) usuarioActual;
        nombre.setText(maestroActual.getNombre());
        apellido.setText(maestroActual.getApellido());
        username.setText(maestroActual.getUsername());
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
    
    public void establecerDatosDelPerfil(JTextField nombre, JTextField apellido, JTextField username, JTextField contrasena){
        nombre.setText(usuarioActual.getNombre());
        apellido.setText(usuarioActual.getApellido());
        username.setText(usuarioActual.getUsername());
        contrasena.setText(String.valueOf(usuarioActual.getContrasena()));
    }
    
    public void establecerDatosDelPerfil(JLabel nombre, JLabel apellido, JLabel username, JLabel contrasena){
        nombre.setText(usuarioActual.getNombre());
        apellido.setText(usuarioActual.getApellido());
        username.setText(usuarioActual.getUsername());
        contrasena.setText(String.valueOf(usuarioActual.getContrasena()));
    }
    
    public boolean guardarCambiosPerfil(JTextField txtNombre, JTextField txtApellido, JTextField txtUser, JTextField txtClave) {
    
            if (esVacio(txtNombre, "Debe indicar su nombre") ||
                esVacio(txtApellido, "Debe indicar su apellido") ||
                esVacio(txtUser, "Debe indicar un usuario") ||
                esVacio(txtClave, "Debe indicar una contraseña")) {
                return false;
            }

            String nuevoNombre = txtNombre.getText().trim();
            String nuevoApellido = txtApellido.getText().trim();
            String nuevoUser = txtUser.getText().trim();
            String nuevaClave = txtClave.getText().trim();


            String viejoUser = this.usuarioActual.getUsername(); 
            Firestore db = FirestoreClient.getFirestore();

            try {

                if (nuevoUser.equals(viejoUser)) {


                    Map<String, Object> actualizaciones = new HashMap<>();
                    actualizaciones.put("nombre", nuevoNombre);
                    actualizaciones.put("apellido", nuevoApellido);
                    actualizaciones.put("password", nuevaClave);
                    
                    db.collection("usuarios").document(viejoUser).update(actualizaciones).get();

                    // Actualizamos la memoria RAM
                    actualizarObjetoLocal(nuevoNombre, nuevoApellido, nuevaClave, nuevoUser);

                    javax.swing.JOptionPane.showMessageDialog(null, "Datos actualizados correctamente.");
                    return true;
                }
                else {

                      
                        DocumentReference refNuevo = db.collection("usuarios").document(nuevoUser);
                        DocumentSnapshot checkNuevo = refNuevo.get().get();

                        if (checkNuevo.exists()) {
                            // ¡ALERTA ROJA! El documento ya existe. FRENAMOS TODO.
                            javax.swing.JOptionPane.showMessageDialog(null, 
                                "El usuario '" + nuevoUser + "' ya está registrado por otra persona.\nNo puedes usar ese nombre.", 
                                "Usuario Ocupado", 
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                            return false;
                        }

                       
                        DocumentReference docViejo = db.collection("usuarios").document(viejoUser);

                     
                        DocumentSnapshot snapshotViejo = docViejo.get().get();

                        if (snapshotViejo.exists()) {
                            Map<String, Object> datosClonados = snapshotViejo.getData();

                            datosClonados.put("nombre", nuevoNombre);
                            datosClonados.put("apellido", nuevoApellido);
                            datosClonados.put("password", nuevaClave);
                            datosClonados.put("username", nuevoUser); 

                            refNuevo.set(datosClonados).get(); 

                            docViejo.delete();

                            actualizarObjetoLocal(nuevoNombre, nuevoApellido, nuevaClave, nuevoUser);

                            javax.swing.JOptionPane.showMessageDialog(null, 
                                "¡Perfil registrado a: " + nuevoUser + "!");
                            return true;
                        } else {
                             javax.swing.JOptionPane.showMessageDialog(null, "Error: No se encontraron tus datos originales.");
                             return false;
                        }
                }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Error al guardar en la nube: " + e.getMessage(), "Error", 0);
            return false;
        }
    }   
    
    private void actualizarObjetoLocal(String n, String a, String c, String u) {
        if (this.usuarioActual != null) {
            this.usuarioActual.setNombre(n);
            this.usuarioActual.setApellido(a);
            this.usuarioActual.setContrasena(c);
            this.usuarioActual.setUsername(u);
            
        }
    }

    public void mostrarMensaje(String mensaje, String titulo, String nombreIcono) {
    
        java.awt.Color blanco = java.awt.Color.WHITE;
        java.awt.Color azulTanuki = new java.awt.Color(40, 66, 119);

        javax.swing.JPanel panelPrincipal = new javax.swing.JPanel();
        panelPrincipal.setLayout(new javax.swing.BoxLayout(panelPrincipal, javax.swing.BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(blanco); // Panel blanco
        panelPrincipal.setPreferredSize(new java.awt.Dimension(350, 150)); 
        panelPrincipal.setOpaque(false); // Importante: transparente para no tapar el fondo blanco
        //panelPrincipal.add(texto, java.awt.BorderLayout.CENTER);
        
    
    
        javax.swing.JLabel etiquetaTitulo = new javax.swing.JLabel(titulo);

        etiquetaTitulo.setFont(new java.awt.Font("Cy Grotesk Key", java.awt.Font.BOLD, 18)); 
        etiquetaTitulo.setForeground(azulTanuki);
        etiquetaTitulo.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT); // Alinear a la izquierda

        javax.swing.JTextArea areaTexto = new javax.swing.JTextArea(mensaje);
        areaTexto.setFont(new java.awt.Font("Cy Grotesk Key", java.awt.Font.PLAIN, 14));
        areaTexto.setForeground(azulTanuki);
        areaTexto.setBackground(blanco); 

        areaTexto.setEditable(false);
        areaTexto.setHighlighter(null);
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT); // Alinear a la izquierda
        
        // Tamaño fijo para que no se deforme
        areaTexto.setSize(new java.awt.Dimension(350, 1)); 
        // Un pequeño margen interno en el texto
        areaTexto.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));
        areaTexto.setPreferredSize(new java.awt.Dimension(350, 150));
        // ARMAR EL PANEL 
        panelPrincipal.add(etiquetaTitulo);
        panelPrincipal.add(javax.swing.Box.createVerticalStrut(10)); // Espacio de 10px entre título y texto
        panelPrincipal.add(areaTexto);
        
        
        
        panelPrincipal.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        javax.swing.ImageIcon icono = null;
        try {
            java.net.URL url = getClass().getResource("/imagenes/iconos/" + nombreIcono);
            if (url != null) {
                java.awt.Image img = new javax.swing.ImageIcon(url).getImage();
                icono = new javax.swing.ImageIcon(img.getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {}

        
        javax.swing.JOptionPane.showMessageDialog(
            null, 
            panelPrincipal, 
            "",             
            javax.swing.JOptionPane.PLAIN_MESSAGE, 
            icono
        );
    }
    
    public void mostrarProgreso(JTable logros, JLabel puntosE, JLabel racha) {
        Estudiante estudianteActual = (Estudiante) usuarioActual;

        // NUEVO: Descargar historial si está vacío
        // (Esto asegura que los cálculos de abajo funcionen)
        if (estudianteActual.getProgreso().getResultados().isEmpty()) {
            cargarHistorialDeResultados(estudianteActual);
            
            // También asegurarnos de que la racha y puntos totales estén sincronizados
            // (Si ya los cargaste en iniciarSesion, esto es redundante pero seguro)
        }

        int puntosTotales = estudianteActual.getProgreso().getPuntajeTotalGeneral();
        puntosE.setText(String.valueOf(puntosTotales));

        // Verificar Logros 
        verificarYAsignarLogros(estudianteActual);

        // Llenar la Tabla 
        List<Logro> listaL = estudianteActual.getLogros();


        String[] columna = {"MEDALLA","TEMA","NOMBRE","DESCRIPCIÓN","PUNTOS"};

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
        for (Logro logro : listaL) {
            String nombreArchivo = logro.getRutaIcono(); 
            String rutaCompleta = "/imagenes/iconos/" + nombreArchivo;
            
            System.out.println("--------------------------------------------------");
            System.out.println("Logro: " + logro.getNombre());
            System.out.println("Intentando cargar: " + rutaCompleta);
            
            java.net.URL imgUrl = getClass().getResource(rutaCompleta);
            
            if (imgUrl != null) {
                System.out.println("IMAGEN ENCONTRADA: " + imgUrl.toString());
                
                ImageIcon iconoOriginal = new ImageIcon(imgUrl);
               
                if (iconoOriginal.getIconWidth() > 0) {
                     java.awt.Image img = iconoOriginal.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
                     row[0] = new ImageIcon(img);
                } else {
                     System.err.println("La imagen existe pero parece estar vacía o corrupta.");
                     row[0] = null;
                }
            } else {
                System.err.println("ERROR: Java no encuentra el archivo en esa ruta.");
                System.err.println("   Consejo: Verifica mayúsculas/minúsculas o haz Clean & Build.");
                row[0] = null; 
            }

            row[1] = logro.getTema().getNombre(); 
            row[2] = logro.getNombre();
            row[3] = logro.getDescripcion();
            row[4] = logro.getPuntosNecesarios();

            dtm.addRow(row);
        }
        logros.setModel(dtm);

        // Estética de la tabla
        JTableHeader header = logros.getTableHeader();
        Font fuenteHeader = new Font("Cy Grotesk Key", Font.BOLD, 14);
        header.setFont(fuenteHeader);
        logros.getTableHeader().setForeground(new Color(40,66,119));
        
        // 5. Racha (Usamos el getter que arreglamos antes)
        int diasDeRacha = estudianteActual.getProgreso().getDiasRacha();
        racha.setText(Integer.toString(diasDeRacha));
    }
    
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
            if (logro.getTema() == null) continue;
            
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
                        mostrarMensaje("¡Nuevo Logro Desbloqueado!\n\n" + logro.getNombre(), "¡Logro Obtenido!",logro.getRutaIcono());
                        //JOptionPane.showMessageDialog(null, "¡Nuevo Logro Desbloqueado!\n\n" + logro.getNombre(), "¡Logro Obtenido!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
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
                //JOptionPane.showMessageDialog(null, "¡Nuevo logro desbloqueado: " + logro.getNombre() + "!");
            }
        }
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
            
            // Verificar si el salón existe
            DocumentReference salonRef = db.collection("salones").document(String.valueOf(idSalonSolicitado));
            DocumentSnapshot salonDoc = salonRef.get().get();
            
            if (!salonDoc.exists()) {
                JOptionPane.showMessageDialog(null, "No se encontró el salón ID: " + idSalonSolicitado, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // AGREGAR SOLICITUD (Atomicamente)
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
    
    public void consultarInfoSalon(JLabel grado, JLabel seccion, JLabel maestro, JLabel usernameMaestro){
        Estudiante est = (Estudiante) usuarioActual;
        if (est.getGrado()==0){
            grado.setFont(new Font("Cy Grotesk Key", Font.ITALIC, 18));
            grado.setText("No asignado");
            seccion.setFont(new Font("Cy Grotesk Key", Font.ITALIC, 18));
            seccion.setText("No asignado");
            maestro.setFont(new Font("Cy Grotesk Key", Font.ITALIC, 18));
            maestro.setText("No asignado");
            usernameMaestro.setText(" ");
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
            usernameMaestro.setText(est.getSalon().getMaestro().getUsername());
        }
    }
    
    public void llenarComboSalones(JComboBox idSalones){
        Maestro maestroActual = (Maestro) usuarioActual;
        List<Salon> salones = maestroActual.getSalones();
        for (Salon s : salones){
            idSalones.addItem(s.getIdSalon());
        }
    }
    
    public void mostrarSolicitudes(JList listaSolicitudes) {
        Firestore db = FirestoreClient.getFirestore();
        DefaultListModel<Estudiante> dtm = new DefaultListModel<>();
        dtm.clear();
        
        Set<Integer> idsYaAgregados = new HashSet<>();
        
        try {
            List<QueryDocumentSnapshot> estudiantesLibres = db.collection("usuarios")
                    .whereEqualTo("rol", "estudiante")
                    .whereEqualTo("idSalon", null)
                    .get().get().getDocuments();

            for (DocumentSnapshot doc : estudiantesLibres) {
                Long idLong = doc.getLong("idUsuario");
                if (idLong == null) continue;
                
                int idEst = idLong.intValue();

                // Solo lo agregamos si NO estaba ya en la lista de solicitudes
                // (Para evitar duplicados si alguien solicitó y a la vez es null)
                if (!idsYaAgregados.contains(idEst)) {
                    Estudiante est = reconstruirEstudianteDesdeDoc(doc);
                    dtm.addElement(est);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error cargando lista de candidatos: " + e.getMessage());
        }
        listaSolicitudes.setModel(dtm);
    }
    
    public void mostrarSolicitudes(int idSalon, JList listaSolicitudes) {
        Firestore db = FirestoreClient.getFirestore();
        DefaultListModel<Estudiante> dtm = new DefaultListModel<>();
        dtm.clear();
        
        // Usamos un Set para guardar los IDs que ya metimos a la lista visual
        // y así evitar que un estudiante salga repetido.
        Set<Integer> idsYaAgregados = new HashSet<>();

        try {
            // TRAER LOS QUE HICIERON SOLICITUD EXPLÍCITA
            DocumentSnapshot salonDoc = db.collection("salones").document(String.valueOf(idSalon)).get().get();
            
            List<Long> idsSolicitantes = (List<Long>) salonDoc.get("solicitudesIds");

            if (idsSolicitantes != null && !idsSolicitantes.isEmpty()) {
                for (Long idLong : idsSolicitantes) {
                    int idEst = idLong.intValue();
                    
                    // Buscar al estudiante
                    QuerySnapshot q = db.collection("usuarios").whereEqualTo("idUsuario", idEst).get().get();
                    if (!q.isEmpty()) {
                        DocumentSnapshot userDoc = q.getDocuments().get(0);
                        Estudiante est = reconstruirEstudianteDesdeDoc(userDoc); // (Ver nota abajo*)
                        
                        // Agregar a la lista y al Set de control
                        dtm.addElement(est);
                        idsYaAgregados.add(est.getIdUsuario());
                    }
                }
            }

            

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error cargando lista de candidatos: " + e.getMessage());
        }

        listaSolicitudes.setModel(dtm);
    }

    // *NOTA: He creado este pequeño método auxiliar para no repetir código 
    // al sacar los datos del DocumentSnapshot. Puedes ponerlo privado en el controlador.
    private Estudiante reconstruirEstudianteDesdeDoc(DocumentSnapshot doc) {
        Estudiante est = new Estudiante();
        Long idVal = doc.getLong("idUsuario");
        est.setIdUsuario(idVal != null ? idVal.intValue() : 0);
        
        est.setNombre(doc.getString("nombre"));
        est.setApellido(doc.getString("apellido"));
        est.setUsername(doc.getString("username"));
        
        
        return est;
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
            // Buscamos el documento del salón para obtener la lista actualizada de IDs
            DocumentSnapshot salonDoc = db.collection("salones").document(String.valueOf(salon.getIdSalon())).get().get();
            List<Long> idsEstudiantes = (List<Long>) salonDoc.get("listaEstudiantesIds");

            // Limpiamos la lista local para no duplicar
            salon.getListaEstudiantes().clear();

            if (idsEstudiantes != null && !idsEstudiantes.isEmpty()) {
                // Por cada ID, buscamos al estudiante real
                for (Long idLong : idsEstudiantes) {
                    QuerySnapshot q = db.collection("usuarios").whereEqualTo("idUsuario", idLong).get().get();
                    if (!q.isEmpty()) {
                        DocumentSnapshot userDoc = q.getDocuments().get(0);
                        
                        Estudiante est = new Estudiante();
                        est.setIdUsuario(idLong.intValue());
                        est.setNombre(userDoc.getString("nombre"));
                        est.setApellido(userDoc.getString("apellido"));
                        String usernameLeido = userDoc.getString("username");
                        if (usernameLeido == null || usernameLeido.isEmpty()) {
                            // Si el campo está vacío, usamos el ID del documento como respaldo
                            usernameLeido = userDoc.getId(); 
                        }
                        est.setUsername(usernameLeido);
                        est.setSalon(salon);
                        
                        // --- CORRECCIÓN PARA EL RANKING ---
                        Long puntosCloud = userDoc.getLong("puntos");
                        // Inyectamos los puntos directamente al Progreso
                        est.getProgreso().setPuntosAcumuladosCloud(puntosCloud != null ? puntosCloud.intValue() : 0);
                        
                        
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
            DocumentReference estudianteRef = db.collection("usuarios").document(est.getUsername()); // Usamos username como ID del documento usuario

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
            DocumentReference estudianteRef = db.collection("usuarios").document(estudiante.getUsername());
            
            // 1. Obtener datos del salón para saber grado y sección correctos
            DocumentSnapshot salonSnap = salonRef.get().get();
            if (!salonSnap.exists()) return false;
            
            Long grado = salonSnap.getLong("grado");
            String seccion = salonSnap.getString("seccion");

            // 2. ACTUALIZAR FIREBASE 
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
    
    public boolean existeSalon(int grado, char seccion) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            String seccionStr = String.valueOf(seccion);
            
            // Buscamos los salones que cumplan LAS DOS condiciones al mismo tiempo
            Query query = db.collection("salones")
                    .whereEqualTo("grado", grado)
                    .whereEqualTo("seccion", seccionStr);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

            // Si la lista NO está vacía, significa que encontramos al menos uno -> YA EXISTE
            return !documents.isEmpty();

        } catch (Exception e) {
            System.err.println("Error al validar existencia del salón: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
    
    public boolean crearSalon(JComboBox grado, JComboBox seccion) {
        Maestro maestroActual = (Maestro) usuarioActual;
        int g = (int) grado.getSelectedItem();
        char s = (char) seccion.getSelectedItem();
        
        if (existeSalon(g, s)) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                "El salón de " + g + "° grado sección '" + s + "' ya existe.\nNo se puede crear duplicado.", 
                "Salón Duplicado", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return false; 
        }
      
        Salon nuevoSalon = registrarSalonEnFirebase(maestroActual, g, s);

        if (nuevoSalon != null) {
            
            if (maestroActual.getSalones() == null) maestroActual.setSalones(new ArrayList<>());
            maestroActual.getSalones().add(nuevoSalon);

            JOptionPane.showMessageDialog(null, 
                "Nuevo Salon creado: " + nuevoSalon.getGrado() + "° '" + nuevoSalon.getSeccion() + "'\nID Asignado: " + nuevoSalon.getIdSalon(), 
                "Creacion exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            return true;
            
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la nube.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
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

   


    // Método pequeño para escribir en Firebase
    private void guardarLogroEnNube(Estudiante est, Logro logro) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            db.collection("usuarios").document(est.getUsername())
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
            DocumentReference estudianteRef = db.collection("usuarios").document(usuarioActual.getUsername());
            
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
            finalizarPractica(cardLayoutPrincipal, panelContenedor); 
            return null;
        }

        this.tiempoInicioPregunta = java.time.Instant.now();
        javax.swing.JToggleButton[] botones = {btnA, btnB, btnC, btnD};
        boolean mostrarImagenAhora = ej.tieneImagen() && ej.isImagenEnPregunta();

        int alineacionVertical;
        String margenHtml;
        String tamanoLetra;

        if (mostrarImagenAhora) {
           
            cargarImagen(ej.getRutaImagen(), lblImagenPreguntaSeleccion);
            cargarImagen(ej.getRutaImagen(), lblImagenPreguntaEscrita);

            panelImagenSeleccion.setVisible(true);
            panelImagenEscrita.setVisible(true);

            // Alineación ARRIBA
            alineacionVertical = javax.swing.SwingConstants.TOP;
            margenHtml = "margin-top: 5px;";

            // CAMBIO: Letra más pequeña para que quepa con la imagen
            tamanoLetra = "font-size: 16px;"; 

        } else {
            panelImagenSeleccion.setVisible(false);
            panelImagenEscrita.setVisible(false);

           
            alineacionVertical = javax.swing.SwingConstants.CENTER;
            margenHtml = ""; 

            tamanoLetra = "font-size: 22px;"; 
        }

        String preguntaHtml = "<html><div style='width: 280px; text-align: center; " + tamanoLetra + " " + margenHtml + " color: black;'>" 
                              + ej.getPregunta() + "</div></html>";

        // --- TIPO DE EJERCICIO ---
        java.util.Random rnd = new java.util.Random();
        boolean esEscrito = rnd.nextBoolean();

        if (ej.isForzarSeleccion()) {
            esEscrito = false; 
        }
        if (ej.getOpciones() == null || ej.getOpciones().length < 4) esEscrito = true;

        if (esEscrito) {
            // MODO ESCRITO
            lblPreguntaEscrita.setText(preguntaHtml);
            lblPreguntaEscrita.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            lblPreguntaEscrita.setVerticalAlignment(alineacionVertical);

            txtRespuestaEscrita.setText("");
            cardLayoutPrincipal.show(panelContenedor, "EjercicioEscrito");
        } else {
            // MODO SELECCIÓN
            lblPreguntaTexto.setText(preguntaHtml);
            lblPreguntaTexto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            lblPreguntaTexto.setVerticalAlignment(alineacionVertical);

            grupoOpciones.clearSelection();

            if (ej.tieneOpcionesConImagen()) {
                String[] rutas = ej.getRutasOpciones();
                for (int i = 0; i < 4; i++) {
                    botones[i].setText(""); 
                    cargarImagen(rutas[i], botones[i]); 
                    botones[i].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                }
            } else {
                String[] textos = ej.getOpciones();
                for (int i = 0; i < 4; i++) {
                    botones[i].setIcon(null); 
                    botones[i].setText("<html><center>" + textos[i] + "</center></html>");
                }
            }
            cardLayoutPrincipal.show(panelContenedor, "EjercicioSeleccion");
        }

        return ej;
    }
    private void actualizarNivelEnNube(Estudiante est, Tema tema) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            NivelDificultad nivelActual = est.getProgreso().getNivelActual(tema);

            DocumentReference estRef = db.collection("usuarios").document(est.getUsername());

            // Si el tema se llama "Núm. Decimales", le quitamos el punto para que quede "Núm Decimales"
            // Esto evita que Firebase crea que el punto es un separador de carpetas.
            String nombreSanitizado = tema.getNombre().replace(".", ""); 
            // ---------------------------------------

            ApiFuture<WriteResult> future = estRef.update("progresoNiveles." + nombreSanitizado, nivelActual.toString());

            System.out.println("✅ Nube actualizada: " + nombreSanitizado + " -> " + nivelActual);

        } catch (Exception e) {
            System.err.println("Error guardando en Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void finalizarPractica(java.awt.CardLayout cardLayout, javax.swing.JPanel panelContenedor) {
        if (this.resultadosTemporales.isEmpty()) {
            // Si no hubo respuestas, regresamos directo
            if (cardLayout != null && panelContenedor != null) {
                cardLayout.show(panelContenedor, "interfazEstudiante"); 
            }
            return;
        }

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

        if (aprobo) {
            // Lógica para subir de nivel en el Progreso del estudiante
            // (Asume que tu clase Progreso tiene este método)
            boolean subioNivel = est.getProgreso().subirNivel(tema);

            if (subioNivel) {
                actualizarNivelEnNube(est, tema); // <--- Llamada a función auxiliar
            }
            
            String mensaje = "¡Felicidades!\nAprobaste la práctica.";
            if (subioNivel) {
                mensaje += "\n\n¡HAS SUBIDO DE NIVEL EN " + tema.getNombre().toUpperCase() + "!";
            } else {
                mensaje += "\n\nYa estás en el nivel máximo o mantienes tu nivel.";
            }

            JOptionPane.showMessageDialog(null, mensaje, "¡Práctica Superada!", JOptionPane.INFORMATION_MESSAGE);

        } else {
            // Mensaje de ánimo

            String mensaje = "¡Buen intento!";
            mensaje += "\n\n¡Sigue practicando, tú puedes!";

            JOptionPane.showMessageDialog(null, mensaje, "Práctica Finalizada", JOptionPane.INFORMATION_MESSAGE);
        }
        
        if (cardLayout != null && panelContenedor != null) {
            cardLayout.show(panelContenedor, "interfazEstudiante");
        }
    }

 
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
    
    
    public Salon buscarSalon(int grado, String seccionStr) {
        if (usuarioActual instanceof Maestro) {
            Maestro m = (Maestro) usuarioActual;
            char seccion = seccionStr.charAt(0);
            for (Salon s : m.getSalones()) {
                if (s.getGrado() == grado && s.getSeccion() == seccion) {
                    return s;
                }
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

    //DADDY
    
    // ==========================================
    // MÉTODOS PUENTE PARA LA VISTA (MainJFrame)
    // ==========================================

    /**
     * CASO 1: RANKING SALÓN
     * Genera el reporte, guarda en memoria y llena la tabla.
     */
    public boolean mostrarRankingSalon(Salon salon, PeriodoReporte periodo, JTable tablaRanking) {
        // 1. Crear configuración
        ConfiguracionReporte config = ConfiguracionReporte.paraReporteSalon(salon)
                .conPeriodo(periodo)
                .conFormatoVisual(FormatoVisual.TABLA);

        // 2. Generar reporte desde Firebase
        if (prepararReporte(config)) {
            // 3. Llenar la tabla visualmente
            List<RankingEntry> ranking = this.reporteActualEnPantalla.getRanking();
            
            DefaultTableModel dtm = new DefaultTableModel(new Object[]{"Puesto", "Estudiante", "Puntos"}, 0);
            int puesto = 1;
            for (RankingEntry r : ranking) {
                dtm.addRow(new Object[]{
                    puesto++, 
                    r.getEstudiante().getNombre() + " " + r.getEstudiante().getApellido(),
                    r.getPuntaje()
                });
            }
            tablaRanking.setModel(dtm);
            return true;
        }
        return false;
    }

    /**
     * CASO 2: TABLA DETALLADA SALÓN
     */
    public boolean mostrarTablaDetalladaSalon(Salon salon, PeriodoReporte periodo, JTable tablaDetalle) {
        // Pedimos GRAFICO para obligar al motor a calcular las estadísticas por tema,
        // en lugar de darnos un Ranking de alumnos.
        ConfiguracionReporte config = ConfiguracionReporte.paraReporteSalon(salon)
                .conPeriodo(periodo)
                .conFormatoVisual(FormatoVisual.GRAFICO);

        if (prepararReporte(config)) {
            Map<String, ReporteDatosPorTema> datos = this.reporteActualEnPantalla.getDatosPorTema();
            
            DefaultTableModel dtm = new DefaultTableModel(new Object[]{"Tema", "Intentos", "Aciertos", "Puntos", "% Efectividad"}, 0);
            
            if (datos != null) {
                for (ReporteDatosPorTema d : datos.values()) {
                    double valorRaw = d.getPorcentajeAciertos(); // Obtenemos el valor crudo

                    // Corrección automática de escala
                    double valorFinal;
                    if (valorRaw <= 1.0) {
                        // Si es 0.5, 0.8, etc. -> Multiplicamos por 100
                        valorFinal = valorRaw * 100.0;
                    } else {
                        // Si ya viene como 50, 80, etc. -> Lo dejamos así
                        valorFinal = valorRaw;
                    }
                    
                    dtm.addRow(new Object[]{
                        d.getNombreTema(),
                        d.getIntentosTotales(),
                        d.getAciertosTotales(),
                        d.getPuntosTotales(),
                        String.format("%.1f%%", valorFinal)
                    });
                }
            }
            tablaDetalle.setModel(dtm);
            return true;
        }
        return false;
    }

    /**
     * CASO 3: GRÁFICO SALÓN
     */
    public boolean mostrarGraficoDetalladoSalon(Salon salon, PeriodoReporte periodo, JPanel panelContenedor) {
        ConfiguracionReporte config = ConfiguracionReporte.paraReporteSalon(salon)
                .conPeriodo(periodo)
                .conFormatoVisual(FormatoVisual.GRAFICO);

        if (prepararReporte(config)) {
            Map<String, ReporteDatosPorTema> datos = this.reporteActualEnPantalla.getDatosPorTema();
            
            // Crear Dataset para JFreeChart
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            if (datos != null) {
                for (ReporteDatosPorTema d : datos.values()) {
                    double valorRaw = d.getPorcentajeAciertos();

                    double valorGrafico;
                    if (valorRaw <= 1.0) {
                        valorGrafico = valorRaw * 100.0;
                    } else {
                        valorGrafico = valorRaw;
                    }
                    dataset.addValue(valorGrafico, "Efectividad", d.getNombreTema());
                }
            }

            // Crear Gráfico de Barras
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Desempeño Promedio por Tema",
                    "Tema",
                    "Porcentaje de Aciertos (%)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false);

            // Mostrar en el Panel
            ChartPanel chartPanel = new ChartPanel(barChart);
            chartPanel.setPreferredSize(new java.awt.Dimension(panelContenedor.getWidth(), panelContenedor.getHeight()));
            
            panelContenedor.removeAll();
            panelContenedor.setLayout(new BorderLayout());
            panelContenedor.add(chartPanel, BorderLayout.CENTER);
            panelContenedor.validate();
            
            return true;
        }
        return false;
    }

    /**
     * CASO 4: TABLA ESTUDIANTE INDIVIDUAL
     */
    public boolean mostrarTablaDetalladaEstudiante(Salon salon, PeriodoReporte periodo, Estudiante est, JTable tabla) {
        ConfiguracionReporte config = ConfiguracionReporte.paraReporteEstudiante(salon, est) 
                .conPeriodo(periodo)
                .conFormatoVisual(FormatoVisual.TABLA);

        if (prepararReporte(config)) {
            ReporteDatosIndividual datos = this.reporteActualEnPantalla.getDatosIndividuales();
            
            DefaultTableModel dtm = new DefaultTableModel(new Object[]{"Tema", "Nivel", "Intentos", "Puntos", "% Aciertos"}, 0);
            
            if (datos != null && datos.getDetallePorTema() != null) {
                for (ReporteDetalleTemaEstudiante d : datos.getDetallePorTema()) {
                    double valorRaw = d.getPorcentajeAciertos(); // Obtenemos el valor crudo

                    // Corrección automática de escala
                    double valorFinal;
                    if (valorRaw <= 1.0) {
                        // Si es 0.5, 0.8, etc. -> Multiplicamos por 100
                        valorFinal = valorRaw * 100.0;
                    } else {
                        // Si ya viene como 50, 80, etc. -> Lo dejamos así
                        valorFinal = valorRaw;
                    }
                    
                    dtm.addRow(new Object[]{
                        d.getNombreTema(),
                        d.getNivelActual(),
                        d.getIntentosEnPeriodo(),
                        d.getPuntosEnPeriodo(),
                        String.format("%.1f%%", valorFinal)
                    });
                }
            }
            tabla.setModel(dtm);
            return true;
        }
        return false;
    }

    /**
     * CASO 5: GRÁFICO ESTUDIANTE INDIVIDUAL
     */
    public boolean mostrarGraficoIndividual(Salon salon, PeriodoReporte periodo, Estudiante est, JPanel panelContenedor) {
        ConfiguracionReporte config = ConfiguracionReporte.paraReporteEstudiante(salon, est)
                .conPeriodo(periodo)
                .conFormatoVisual(FormatoVisual.GRAFICO);

        if (prepararReporte(config)) {
            ReporteDatosIndividual datos = this.reporteActualEnPantalla.getDatosIndividuales();
            
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            if (datos != null && datos.getPromedioPorTema() != null) {
                for (Map.Entry<String, Double> entry : datos.getPromedioPorTema().entrySet()) {
                    double valorRaw = entry.getValue();

                    double valorGrafico;
                    if (valorRaw <= 1.0) {
                        valorGrafico = valorRaw * 100.0;
                    } else {
                        valorGrafico = valorRaw;
                    }
                    dataset.addValue(valorGrafico, "Estudiante", entry.getKey());
                }
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Desempeño de " + est.getNombre(),
                    "Tema",
                    "Aciertos (%)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    false, true, false);

            ChartPanel chartPanel = new ChartPanel(barChart);
            chartPanel.setPreferredSize(new java.awt.Dimension(panelContenedor.getWidth(), panelContenedor.getHeight()));
            
            panelContenedor.removeAll();
            panelContenedor.setLayout(new BorderLayout());
            panelContenedor.add(chartPanel, BorderLayout.CENTER);
            panelContenedor.validate();
            
            return true;
        }
        return false;
    }

    /**
     * CASO 6: EXPORTAR (Genérico)
     * Este lo llamarán tus botones de exportar PDF/Excel.
     */
    public void exportarReporte(Salon s, PeriodoReporte p, Estudiante e, FormatoArchivo arch, FormatoVisual vis) {
        // Si el reporte en pantalla coincide con lo que piden, lo usamos.
        // Si no (o es null), lo regeneramos rápido.
        if (this.reporteActualEnPantalla == null) {
            ConfiguracionReporte config;
            if (e != null) {
                config = ConfiguracionReporte.paraReporteEstudiante(s,e).conPeriodo(p);
            } else {
                config = ConfiguracionReporte.paraReporteSalon(s).conPeriodo(p);
            }
            prepararReporte(config);
        }
        
        if (this.reporteActualEnPantalla != null) {
            try {
                IGeneradorReporte gen;
                if (arch == FormatoArchivo.EXCEL) {
                    gen = new GeneradorExcel(vis);
                } else {
                    gen = new GeneradorPDF(vis);
                }
                gen.guardar(this.reporteActualEnPantalla);
                JOptionPane.showMessageDialog(null, "Archivo exportado con éxito.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al exportar: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "No hay datos para exportar.");
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
    
    public void cargarImagen(String rutaImagen, javax.swing.JComponent componente) {
        // 1. SEGURIDAD Y VISIBILIDAD
        // Si el componente es nulo, no hacemos nada
        if (componente == null) return;
        componente.setOpaque(false); 
        componente.setBackground(new java.awt.Color(0,0,0,0));

        // Si no hay ruta, limpiamos y ocultamos el componente
        if (rutaImagen == null || rutaImagen.isEmpty()) {
            if (componente instanceof javax.swing.JLabel) {
                ((javax.swing.JLabel) componente).setIcon(null);
            } else if (componente instanceof javax.swing.AbstractButton) {
                ((javax.swing.AbstractButton) componente).setIcon(null);
            }
            componente.setVisible(false); // SE OCULTA
            return;
        }

        try {
            String rutaFinal = "/imagenesejercicios/" + rutaImagen;
            java.net.URL url = getClass().getResource(rutaFinal);

            if (url != null) {
                javax.swing.ImageIcon iconoOriginal = new javax.swing.ImageIcon(url);

                // 2. OBTENER LAS DIMENSIONES DEL CONTENEDOR REAL
                int anchoMax = componente.getWidth();
                int altoMax = componente.getHeight();
                if (anchoMax <= 0) anchoMax = 250; // Valor por defecto seguro
                if (altoMax <= 0) altoMax = 200;  // Valor por defecto seguro

                // 3. CÁLCULO PROPORCIONAL (Matemática para no deformar)
                float propImagen = (float) iconoOriginal.getIconWidth() / iconoOriginal.getIconHeight();
                float propDestino = (float) anchoMax / altoMax;

                int anchoFinal = anchoMax;
                int altoFinal = altoMax;

                // Si el destino es más "apaisado" que la imagen, la altura es el límite.
                if (propDestino > propImagen) {
                    anchoFinal = (int) (altoMax * propImagen);
                } 
                // Si el destino es más "alto" que la imagen, el ancho es el límite.
                else {
                    altoFinal = (int) (anchoMax / propImagen);
                }

                // 4. ESCALAR LA IMAGEN
                java.awt.Image imgEscalada = iconoOriginal.getImage().getScaledInstance(anchoFinal, altoFinal, java.awt.Image.SCALE_SMOOTH);
                javax.swing.ImageIcon iconoFinal = new javax.swing.ImageIcon(imgEscalada);

                // 5. ASIGNAR, CENTRAR Y MOSTRAR
                if (componente instanceof javax.swing.JLabel) {
                    ((javax.swing.JLabel) componente).setIcon(iconoFinal);
                    // Centrado horizontal y vertical
                    ((javax.swing.JLabel) componente).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    ((javax.swing.JLabel) componente).setVerticalAlignment(javax.swing.SwingConstants.CENTER);
                } else if (componente instanceof javax.swing.AbstractButton) {
                    ((javax.swing.AbstractButton) componente).setIcon(iconoFinal);
                    // Los botones suelen centrar el icono por defecto, pero esto asegura
                     ((javax.swing.AbstractButton) componente).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                }

                componente.setVisible(true); // SE MUESTRA

            } else {
                System.err.println("❌ Imagen no encontrada: " + rutaFinal);
                componente.setVisible(false); // Si falla, se oculta
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
            componente.setVisible(false);
        }
    }
    
    public void configurarPractica(Tema tema, String nombreNivel) {
        this.temaSeleccionado = tema;

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
    
    
    
    public void cargarDatosPresentacion(
        javax.swing.JLabel lblSubtema, 
        javax.swing.JLabel lblNombrePersonaje, 
        javax.swing.JLabel lblSaludoEstudiante,
        javax.swing.JLabel lblImagenPersonaje) {
    
        if (this.temaSeleccionado == null || this.usuarioActual == null) return;

        if (lblSubtema != null) {
            lblSubtema.setText("<html><div style='text-align: left; width: 350px;'>" + this.temaSeleccionado.getNombre()+ "</div></html>");
        }

        if (lblNombrePersonaje != null) {
            lblNombrePersonaje.setText(this.temaSeleccionado.getPersonajeNombre());
        }
        if (lblSaludoEstudiante != null) {
            String nombreCompleto = this.usuarioActual.getNombre();
            String primerNombre = "";

            if (nombreCompleto != null && !nombreCompleto.isEmpty()) {
                primerNombre = nombreCompleto.split(" ")[0]; 
            }

            lblSaludoEstudiante.setText(primerNombre);
        }
        if (lblImagenPersonaje != null) {
            cargarImagenPersonaje(this.temaSeleccionado.getPersonajeRutaImagenPresentacion(), lblImagenPersonaje, 320, 460);
        }
    }
    
    public void cargarDatosDescripcionTema(
        javax.swing.JLabel lblTitulo, 
        javax.swing.JLabel lblDescripcion, 
        javax.swing.JLabel lblImagen) {
    
        if (this.temaSeleccionado == null) return;

        // 1. Título
        if (lblTitulo != null) {
            String nombreTema = this.temaSeleccionado.getNombre();
            lblTitulo.setText("<html><div style='text-align: center; width: 350px;'>" + nombreTema + "</div></html>");
        }

        // 2. DESCRIPCIÓN
        if (lblDescripcion != null) {
            String textoDesc = this.temaSeleccionado.getDescripcion();
            if (textoDesc == null) textoDesc = "Sin descripción disponible.";

            
            // - width: 360px (Un poco más angosto para que no roce los bordes)
            // - font-size: 14px (Controlamos el tamaño de la letra)
            // - text-align: justify (Se ve más ordenado)
            String htmlTexto = "<html><div style='text-align: justify; width: 340px; font-size: 24px;'>" 
                               + textoDesc + "</div></html>";

            lblDescripcion.setText(htmlTexto);

            //Forzar que el texto empiece desde ARRIBA del label
            lblDescripcion.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        }

        // 3. Imagen del Personaje
        if (lblImagen != null) {
            cargarImagenPersonaje(this.temaSeleccionado.getPersonajeRutaImagenPresentacion(), lblImagen, 320, 460);
        }
    }
    
    public void cargarDatosPreviaPractica(javax.swing.JLabel lblImagenCelebrando) {
        if (this.temaSeleccionado == null) return;

        if (lblImagenCelebrando != null) {
            // CAMBIO AQUÍ:
            // Usamos cargarImagenPersonaje con medidas GRANDES (320 ancho, 460 alto)
            // para que se vea imponente antes de empezar a jugar.
            cargarImagenPersonaje(
                this.temaSeleccionado.getPersonajeRutaImagenCelebrando(), 
                lblImagenCelebrando, 
                410, 
                500
            );
        }
    }
    
    public void cargarImagenPersonaje(String rutaImagen, javax.swing.JLabel lblImagen, int ancho, int alto) {
        // 1. Validaciones básicas
        if (rutaImagen == null || rutaImagen.isEmpty() || lblImagen == null) {
            if (lblImagen != null) lblImagen.setIcon(null);
            return;
        }

        try {
            // 2. Lógica de ruta
            String rutaFinal;
            if (rutaImagen.startsWith("/")) {
                rutaFinal = rutaImagen; 
            } else {
                rutaFinal = "/imagenesPersonajes/" + rutaImagen; 
            }

            java.net.URL url = getClass().getResource(rutaFinal);

            if (url != null) {
                javax.swing.ImageIcon icono = new javax.swing.ImageIcon(url);

                // 3. AQUÍ USAMOS TUS VARIABLES DE TAMAÑO
                java.awt.Image imgEscalada = icono.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_SMOOTH);

                lblImagen.setIcon(new javax.swing.ImageIcon(imgEscalada));
                lblImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

            } else {
                System.err.println("❌ Imagen personaje no encontrada: " + rutaFinal);
                lblImagen.setIcon(null);
            }
        } catch (Exception e) {
            System.err.println("Error cargando personaje: " + e.getMessage());
        }
    }
    
    public void cargarDatosFeedback(boolean esCorrecto, javax.swing.JLabel lblPersonaje) {
    if (this.temaSeleccionado == null || lblPersonaje == null) return;

        int w = 360; 
        int h = 350;
        // -----------------------------

        if (esCorrecto) {
            cargarImagenPersonaje(this.temaSeleccionado.getPersonajeRutaImagenCorrecto(), lblPersonaje, w, h);
        } else {
            cargarImagenPersonaje(this.temaSeleccionado.getPersonajeRutaImagenIncorrecto(), lblPersonaje, w, h);
        }
    }
    public void cargarImagenAjustada(String rutaImagen, javax.swing.JLabel lblImagen) {
        if (rutaImagen == null || rutaImagen.isEmpty()) {
            System.out.println("⚠️ Aviso: Ruta de imagen nula o vacía.");
            if (lblImagen != null) lblImagen.setIcon(null);
            return;
        }

        try {
            String rutaFinal;

            // Lógica de rutas
            if (rutaImagen.startsWith("/")) {
                // Viene del Excel de Temas (ej: /imagenesPersonajes/...)
                rutaFinal = rutaImagen; 
            } else {
                // Viene del Excel de Ejercicios (ej: triangulo.png)
                rutaFinal = "/imagenesejercicios/" + rutaImagen; 
            }

          
            System.out.println("🔍 Buscando imagen en: [" + rutaFinal + "]");

            java.net.URL url = getClass().getResource(rutaFinal);

            if (url != null) {
                System.out.println("   ✅ ¡ENCONTRADA!: " + url);
                // ... (Código de escalado normal) ...
                javax.swing.ImageIcon iconoOriginal = new javax.swing.ImageIcon(url);

                // Si el label aún no tiene tamaño (0), usamos un default
                int anchoDestino = (lblImagen.getWidth() > 0) ? lblImagen.getWidth() : 250;
                int altoDestino = (lblImagen.getHeight() > 0) ? lblImagen.getHeight() : 300;

                // Escalar manteniendo proporción
                float propImagen = (float) iconoOriginal.getIconWidth() / iconoOriginal.getIconHeight();
                float propDestino = (float) anchoDestino / altoDestino;
                int anchoFinal = anchoDestino;
                int altoFinal = altoDestino;

                if (propDestino > propImagen) {
                    anchoFinal = (int) (altoDestino * propImagen);
                } else {
                    altoFinal = (int) (anchoDestino / propImagen);
                }

                java.awt.Image imgEscalada = iconoOriginal.getImage().getScaledInstance(anchoFinal, altoFinal, java.awt.Image.SCALE_SMOOTH);
                lblImagen.setIcon(new javax.swing.ImageIcon(imgEscalada));
                lblImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                lblImagen.setVisible(true); // Forzar visibilidad

            } else {
                System.err.println("   ❌ NO ENCONTRADA. Verifica que el archivo exista en src/main/resources" + rutaFinal);
                if (lblImagen != null) lblImagen.setIcon(null);
            }
        } catch (Exception e) {
            System.err.println("   ☠️ Error cargando imagen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void cargarLogrosMaestros() {
        try {
            // 1. Crear el mapa de temas para búsqueda rápida
            // (IMPORTANTE: Asegúrate de que 'this.listaTemas' ya esté cargada antes de llamar a esto)
            Map<Integer, Tema> mapaTemas = crearMapaDeTemas();
            
            // 2. Cargar el JSON de Logros
            String ruta = "/data/logros.json"; 
            Reader reader = new InputStreamReader(getClass().getResourceAsStream(ruta), "UTF-8");
            
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<Logro>>(){}.getType();
            
            this.listaLogros = gson.fromJson(reader, tipoLista);
            
            // 3. VINCULACIÓN AUTOMÁTICA
            // Recorremos los logros y usamos el 'idTema' para encontrar su pareja
            for (Logro l : this.listaLogros) {
                int idBuscado = l.getIdTema(); // El número que vino del JSON (ej: 34)
                
                Tema temaEncontrado = mapaTemas.get(idBuscado);
                
                if (temaEncontrado != null) {
                    l.setTema(temaEncontrado); // ¡Conexión exitosa!
                } else {
                    System.err.println("Advertencia: El logro '" + l.getNombre() + "' apunta al tema ID " + idBuscado + " pero no existe.");
                }
            }
            
            System.out.println("Logros cargados y vinculados dinámicamente: " + this.listaLogros.size());
            reader.close();
            
        } catch (Exception e) {
            System.err.println("Error cargando logros.json: " + e.getMessage());
            e.printStackTrace();
            this.listaLogros = new ArrayList<>();
        }
    }
    
    // Este método aplana la estructura de árbol en un Mapa simple: ID -> Objeto Tema
    private Map<Integer, Tema> crearMapaDeTemas() {
        Map<Integer, Tema> mapa = new HashMap<>();
        
        // Recorremos la lista maestra de temas (suponiendo que ya está cargada)
        if (this.listaTemas != null) {
            for (Tema t : this.listaTemas) {
                agregarTemaYHijosAlMapa(t, mapa);
            }
        }
        return mapa;
    }

    // Método recursivo para buscar dentro de los hijos, nietos, etc.
    private void agregarTemaYHijosAlMapa(Tema t, Map<Integer, Tema> mapa) {
        mapa.put(t.getId(), t); // Guardamos el tema actual
        
        // Si tiene hijos, nos metemos en cada uno (Recursividad)
        if (t.getTemasHijos() != null) {
            for (Tema hijo : t.getTemasHijos()) {
                agregarTemaYHijosAlMapa(hijo, mapa);
            }
        }
    }
    
    // Método para descargar el historial completo de ejercicios
    public void cargarHistorialDeResultados(Estudiante est) {
        if (est == null) return;
        
        Firestore db = FirestoreClient.getFirestore();
        try {
            // Buscamos en la subcolección: usuarios -> username -> resultados
            List<QueryDocumentSnapshot> docs = db.collection("usuarios")
                    .document(est.getUsername())
                    .collection("resultados")
                    .get().get().getDocuments(); // .get() futuro -> .get() lista
            
            // Limpiamos la lista local para no duplicar si llamamos esto dos veces
            // (Asumiendo que Progreso tiene un getter para la lista o un método clear)
            // Si 'resultados' es privado, necesitas agregar un método public void limpiarResultados() en Progreso.java
            est.getProgreso().getResultados().clear(); 

            for (DocumentSnapshot doc : docs) {
                // Reconstruir objeto Resultado
                Resultado res = new Resultado();
                
                // Fecha
                String fechaStr = doc.getString("fecha");
                if (fechaStr != null) res.setFecha(LocalDate.parse(fechaStr));
                
                // Datos básicos
                Boolean esCorrecto = doc.getBoolean("esCorrecto");
                res.setEsCorrecto(esCorrecto != null ? esCorrecto : false);
                
                Long ptos = doc.getLong("puntos");
                res.setPuntos(ptos != null ? ptos.intValue() : 0);
                
                res.setRespuestaUsuario(doc.getString("respuestaUsuario"));
                
                // Reconstruir Ejercicio (Parcial)
                // No necesitamos todo el ejercicio, solo el TEMA para que funcionen los cálculos de logros
                String nombreTema = doc.getString("tema");
                Tema temaObj = buscarTemaPorNombre(nombreTema); // Usamos tu método de búsqueda
                
                Ejercicio ejFalso = new Ejercicio();
                ejFalso.setTema(temaObj);
                // Si guardaste el ID del ejercicio, podrías buscarlo completo si quisieras
                
                res.setEjercicio(ejFalso);
                
                // Agregar al progreso local
                est.getProgreso().agregarResultado(res);
            }
            
            System.out.println("Historial cargado: " + docs.size() + " ejercicios.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Calcula la fecha de inicio según el Enum seleccionado
    private LocalDate calcularFechaInicio(PeriodoReporte periodo) {
        LocalDate hoy = LocalDate.now();
        switch (periodo) {
            case SEMANA:
                return hoy.minusWeeks(1);
            case MES:
                return hoy.minusMonths(1);
            case TRIMESTRE:
                return hoy.minusMonths(3);
            case COMPLETO:
                return LocalDate.of(2020, 1, 1); // Una fecha muy antigua
            default:
                return hoy.minusMonths(1);
        }
    }
    
    /**
     * MOTOR DE REPORTES:
     * Toma una configuración, va a Firebase, descarga los datos necesarios,
     * hace los cálculos matemáticos y devuelve el objeto Reporte listo.
     */
    public Reporte generarReporteDesdeFirebase(ConfiguracionReporte config) {
        Firestore db = FirestoreClient.getFirestore();
        
        // 1. Preparar Fechas
        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = calcularFechaInicio(config.getPeriodo()); // Usamos el helper
        
        // Creamos el objeto Reporte base
        Reporte reporteFinal = new Reporte(config.getSalon(), fechaInicio, fechaFin);

        try {
            
            // REPORTE INDIVIDUAL (Un solo estudiante)
            
            if (config.esReporteIndividual()) {
                Estudiante est = config.getEstudiante();
                
                if (est.getUsername() == null || est.getUsername().isEmpty()) {
                    System.err.println("Error: Estudiante sin correo, no se puede generar reporte.");
                    return null; 
                }
                
                // Descargamos el mapa "progresoNiveles" de este estudiante específico
                DocumentSnapshot userDoc = db.collection("usuarios").document(est.getUsername()).get().get();
                Map<String, Object> nivelesMap = (Map<String, Object>) userDoc.get("progresoNiveles");

                if (nivelesMap != null) {
                    for (Map.Entry<String, Object> entry : nivelesMap.entrySet()) {
                        String nombreTema = entry.getKey();
                        String nombreNivel = (String) entry.getValue();
                        
                        Tema t = buscarTemaPorNombre(nombreTema);
                        if (t != null) {
                            try {
                                NivelDificultad n = NivelDificultad.valueOf(nombreNivel);
                                est.getProgreso().desbloquearNivel(t, n);
                            } catch (Exception ex) { /* Ignorar error de conversión */ }
                        }
                    }
                }
                
                // 1. Descargar resultados filtrados por fecha
                // Nota: Guardamos fecha como String ISO-8601 ("2023-11-29"), así que la comparación de Strings funciona
                List<QueryDocumentSnapshot> docs = db.collection("usuarios")
                        .document(est.getUsername())
                        .collection("resultados")
                        .whereGreaterThanOrEqualTo("fecha", fechaInicio.toString())
                        .get().get().getDocuments();

                // 2. Agrupar datos por tema
                // Mapa temporal: "Matemáticas" -> {puntos: 50, intentos: 5, aciertos: 3}
                Map<String, ReporteDatosPorTema> acumulador = new HashMap<>();
                int puntajeTotalPeriodo = 0;
                
                for (DocumentSnapshot doc : docs) {
                    String nombreTema = doc.getString("tema");
                    boolean esCorrecto = doc.getBoolean("esCorrecto");
                    int puntos = doc.getLong("puntos").intValue();
                    
                    // Inicializar si no existe el tema en el mapa
                    acumulador.putIfAbsent(nombreTema, new ReporteDatosPorTema(nombreTema));
                    
                    // Crear un objeto Resultado temporal para usar tu método 'agregarResultado'
                    Resultado resTemp = new Resultado();
                    resTemp.setEsCorrecto(esCorrecto);
                    resTemp.setPuntos(puntos);
                    
                    acumulador.get(nombreTema).agregarResultado(resTemp);
                    puntajeTotalPeriodo += puntos;
                }

                // 3. Convertir al formato que pide ReporteDatosIndividual
                List<ReporteDetalleTemaEstudiante> detalles = new ArrayList<>();
                Map<String, Double> promedios = new HashMap<>();
                String temaMasDificil = "Ninguno";
                double menorPorcentaje = 101.0;

                for (ReporteDatosPorTema datos : acumulador.values()) {
                    datos.calcularPorcentaje(); // Método de tu clase
                    
                    // Buscar nivel actual (lo sacamos del estudiante en memoria)
                    Tema tObj = buscarTemaPorNombre(datos.getNombreTema());
                    NivelDificultad nivel = est.getProgreso().getNivelActual(tObj);

                    detalles.add(new ReporteDetalleTemaEstudiante(
                            datos.getNombreTema(),
                            nivel,
                            datos.getPuntosTotales(),
                            datos.getPorcentajeAciertos(),
                            datos.getIntentosTotales()
                    ));
                    
                    promedios.put(datos.getNombreTema(), datos.getPorcentajeAciertos());

                    // Calcular tema más difícil
                    if (datos.getPorcentajeAciertos() < menorPorcentaje) {
                        menorPorcentaje = datos.getPorcentajeAciertos();
                        temaMasDificil = datos.getNombreTema();
                    }
                }
                
                // Calcular porcentaje global
                double porcentajeGlobal = docs.isEmpty() ? 0 : 
                        (double) docs.stream().filter(d -> d.getBoolean("esCorrecto")).count() / docs.size();

                // 4. Empaquetar todo
                ReporteDatosIndividual datosInd = new ReporteDatosIndividual(
                        puntajeTotalPeriodo,
                        porcentajeGlobal,
                        temaMasDificil,
                        promedios,
                        detalles
                );
                
                reporteFinal.setDatosIndividuales(est, datosInd);


            
            // REPORTE DE SALÓN (Todos los estudiantes de un salon)
           
            } else {
                // Primero necesitamos la lista de estudiantes REALES del salón
                // (Usamos el método que hicimos antes para asegurar que la lista esté llena)
                cargarEstudiantesDelSalon(config.getSalon()); 
                List<Estudiante> estudiantesDelSalon = config.getSalon().getListaEstudiantes();

                // --- SUB-CASO B1: RANKING (Tabla de Posiciones) ---
                if (config.getFormatoVisual() == FormatoVisual.TABLA) { // Asumimos que Tabla = Ranking en tu lógica
                    
                    List<RankingEntry> ranking = new ArrayList<>();
                    
                    for (Estudiante e : estudiantesDelSalon) {
                        // Para ranking usamos los puntos TOTALES (históricos) que ya están cargados en el perfil
                        ranking.add(new RankingEntry(e, e.getProgreso().getPuntajeTotalGeneral())); // O e.getPuntos() si tienes el atributo directo
                    }
                    
                    // Ordenar de Mayor a Menor
                    ranking.sort((r1, r2) -> Integer.compare(r2.getPuntaje(), r1.getPuntaje()));
                    
                    reporteFinal.setRanking(ranking);
                    
                } 
                //  DETALLADO/GRÁFICO (Estadísticas del Salón) ---
                else {
                    Map<String, ReporteDatosPorTema> acumuladorSalon = new HashMap<>();
                    
                    
                    for (Estudiante e : estudiantesDelSalon) {
                        if (e.getUsername() == null || e.getUsername().isEmpty()) {
                            System.err.println("Saltando estudiante sin correo (ID: " + e.getIdUsuario() + ")");
                            continue; // Saltamos al siguiente sin romper el programa
                        }
                        
                        List<QueryDocumentSnapshot> resultadosEst = db.collection("usuarios")
                                .document(e.getUsername())
                                .collection("resultados")
                                .whereGreaterThanOrEqualTo("fecha", fechaInicio.toString())
                                .get().get().getDocuments();
                        
                        for (DocumentSnapshot doc : resultadosEst) {
                            String tema = doc.getString("tema");
                            boolean correcto = doc.getBoolean("esCorrecto");
                            int puntos = doc.getLong("puntos").intValue();
                            
                            acumuladorSalon.putIfAbsent(tema, new ReporteDatosPorTema(tema));
                            
                            Resultado r = new Resultado();
                            r.setEsCorrecto(correcto);
                            r.setPuntos(puntos);
                            
                            acumuladorSalon.get(tema).agregarResultado(r);
                        }
                    }
                    
                    // Calcular porcentajes finales por tema
                    for (ReporteDatosPorTema dt : acumuladorSalon.values()) {
                        dt.calcularPorcentaje();
                    }
                    
                    reporteFinal.setDatosPorTema(acumuladorSalon);
                }
            }

            return reporteFinal;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error generando reporte: " + e.getMessage());
            return null;
        }
    }
    
    public boolean prepararReporte(ConfiguracionReporte config) {
        try {
            // Llamamos a la lógica pesada que te pasé antes
            // (Asegúrate de que el método generarReporteDesdeFirebase devuelva un Reporte)
            this.reporteActualEnPantalla = generarReporteDesdeFirebase(config);
            
            return this.reporteActualEnPantalla != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}