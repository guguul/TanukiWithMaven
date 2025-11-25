
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
        if (esVacio(nombre,"Debe indicar su nombre")==false && esVacio(apellido,"Debe indicar su apellido")==false && esVacio(correo,"Debe indicar un usuario")==false && esVacio(clave,"Debe indicar una contraseña")==false && validarCampoTexto(nombre)==true && validarCampoTexto(apellido)==true){
            Usuario estudianteRegistrado = buscarUsuario(correo.getText());
            if (estudianteRegistrado == null){
                Estudiante estudianteNuevo = new Estudiante();
                estudianteNuevo.setNombre(nombre.getText());
                estudianteNuevo.setApellido(apellido.getText());
                estudianteNuevo.setCorreo(correo.getText());
                String contrasena = new String(clave.getPassword());
                estudianteNuevo.setContrasena(contrasena);/// CONVERTIR A STRING
                listaUsuarios.add(estudianteNuevo);
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
    
    public boolean registrarMaestro(JTextField nombre, JTextField apellido, JTextField correo, JPasswordField clave){
        boolean avanzar;
        if (esVacio(nombre,"Debe indicar su nombre")==false && esVacio(apellido,"Debe indicar su apellido")==false && esVacio(correo,"Debe indicar un correo")==false && esVacio(clave,"Debe indicar una contraseña")==false && validarCampoTexto(nombre)==true && validarCampoTexto(apellido)==true){
            Usuario maestroRegistrado = buscarUsuario(correo.getText());
            if (maestroRegistrado == null){
                Maestro maestroNuevo = new Maestro();
                maestroNuevo.setNombre(nombre.getText());
                maestroNuevo.setApellido(apellido.getText());
                maestroNuevo.setCorreo(correo.getText());
                String contrasena = new String(clave.getPassword());
                maestroNuevo.setContrasena(contrasena);/// CONVERTIR A STRING
                listaUsuarios.add(maestroNuevo);
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
                
                String rol = documento.getString("rol");
                
                if (rol == null) {
                System.err.println("Error: El usuario " + correoLogin + " no tiene un 'rol' definido en la BD.");
                return null;
                }
                
                Usuario usuarioLogueado;
                
                if (rol.equals("estudiante")) {
                    Estudiante estudiante = new Estudiante();
                    estudiante.setNombre(documento.getString("nombre"));
                    estudiante.setCorreo(documento.getString("correo"));
                    estudiante.setApellido(documento.getString("apellido"));
                    estudiante.setApellido(documento.getString("contrasena"));
                    //se pueden poner datos especificos
                    this.usuarioActual = estudiante;
                    
                } else if (rol.equals("maestro")) {
                    Maestro maestro = new Maestro();
                    maestro.setNombre(documento.getString("nombre"));
                    maestro.setCorreo(documento.getString("correo"));
                    maestro.setApellido(documento.getString("apellido"));
                    maestro.setApellido(documento.getString("contrasena"));
                    //se pueden poner datos especificos
                    this.usuarioActual = maestro;
                } else {
                JOptionPane.showMessageDialog(null,"Error: Rol desconocido '" + rol + "' en la BD.","Error en el inicio de sesion",JOptionPane.ERROR_MESSAGE);
                
                return null;
                }
                JOptionPane.showMessageDialog(null,"Bienvenido a Tanuki "+documento.getString("nombre"),"",JOptionPane.INFORMATION_MESSAGE);
                return this.usuarioActual;
            } else {
                
                JOptionPane.showMessageDialog(null,"No se encontró usuario con correo: ","Error en el inicio de sesion",JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(null,"Error al intentar iniciar sesión: ","Error en el inicio de sesion",JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public Usuario iniciarSesionUsuario(JTextField correo, JPasswordField clave){
        if (esVacio(correo,"Debe indicar su correo")==false && esVacio(clave,"Debe indicar su contraseña")==false){
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
    
    public void mostrarProgreso(JTable logros, JLabel puntosE) {
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
    }
    
    public Salon buscarSalonID(int id){
        for (Salon s : listaSalones){
            if (s.getIdSalon()==id){
                return s;
            }
        }
        return null;
    }
     
    public boolean agregarSolicitud(JTextField id){
        Estudiante est = (Estudiante) usuarioActual;
        int idNum = Integer.parseInt(id.getText());
        Salon salonSolicitado = buscarSalonID(idNum);
        boolean enviado;
        
        if (salonSolicitado==null){
            JOptionPane.showMessageDialog(null,"No se encontró el salón solicitado","Error de Búsqueda",JOptionPane.ERROR_MESSAGE);
            enviado = false;
        }
        else{
            ArrayList<Estudiante> solicitudes = salonSolicitado.getListaSolicitudes();
            solicitudes.add(est);
            salonSolicitado.setListaSolicitudes(solicitudes);
            JOptionPane.showMessageDialog(null,"Tu solicitud ha sido enviada con éxito","Solicitud enviada",JOptionPane.INFORMATION_MESSAGE);
            enviado = true;
        }
        
        return enviado;
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
    
    public void mostrarSolicitudes(int id, JList listaSolicitudes){
        Salon salonIngresado = buscarSalonID(id);
        List<Estudiante> solicitudes = salonIngresado.getListaSolicitudes();
        
        DefaultListModel<Estudiante> dtm =  new DefaultListModel();
        dtm.clear();
        for (Estudiante est : solicitudes){
            dtm.addElement(est);
        }
        listaSolicitudes.setModel(dtm);
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
    
     public void llenarComboEstudiantes(JComboBox estudiantes,Salon salonSeleccionado){
        List<Estudiante> lista = salonSeleccionado.getListaEstudiantes();
        for (Estudiante est : lista){
            estudiantes.addItem(est);
        }
    }
    
    public boolean sacarEstudiante(JComboBox idSalon, JComboBox estudiante){
        Maestro maestroActual = (Maestro) usuarioActual;
        boolean encontrado;
        int id = (int) idSalon.getSelectedItem();
        Estudiante est = (Estudiante) estudiante.getSelectedItem();
        Salon salonEst = new Salon();
        for (Salon s : maestroActual.getSalones()){
            if (s.getIdSalon()==id){
                salonEst = s;
            }
        }
        if (est==null){
            encontrado = false;
        }
        else {
            
            est.setSalon(null);
            est.setGrado(0);
            est.setSeccion(' ');
            salonEst.getListaEstudiantes().remove(est);
            encontrado = true;
            
            
        }
        return encontrado;
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
    
    private void verificarLogrosGanados(Tema tema) {
        Estudiante est = (Estudiante) this.usuarioActual;
        int puntosActuales = est.getProgreso().getPuntajeTotalPorTema(tema);
        
        for (Logro logro : this.listaLogros) { 
            if (logro.getTema().equals(tema)) {
                if (!est.getLogros().contains(logro)) {
                    if (puntosActuales >= logro.getPuntosNecesarios()) {
                        est.agregarLogro(logro);
                        JOptionPane.showMessageDialog(null, "¡Nuevo Logro Desbloqueado!\n\n" + logro.getNombre(), "¡Logro Obtenido!", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }
    
    public Resultado registrarRespuesta(Ejercicio ejercicio, String respuestaUsuario) { 
        boolean fueCorrecto = ejercicio.validarRespuesta(respuestaUsuario);
        int puntosGanados = fueCorrecto ? ejercicio.getValorPuntos() : 0; // si fue correcta le da los puntos, si es incorrecta le da 0 puntos
        Duration tiempoTotal = Duration.ZERO;
        if (this.tiempoInicioPregunta != null) {
            tiempoTotal = java.time.Duration.between(this.tiempoInicioPregunta, java.time.Instant.now());
        }
    
        Resultado res = new Resultado((Estudiante) usuarioActual, ejercicio, java.time.LocalDate.now(), 1, tiempoTotal, fueCorrecto, puntosGanados, respuestaUsuario);

        this.resultadosTemporales.add(res);
        ((Estudiante)usuarioActual).getProgreso().agregarResultado(res);

        if (!fueCorrecto && !esRondaDeRepeticion) {
            this.ejerciciosFallados.add(ejercicio); // lo agrega a la lista de fallos
        }

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
    
    
}