package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PantallaCarga extends JDialog {

    private Timer animacionTimer;
    private BarraPersonalizada barra; // Usamos nuestro componente propio

    public PantallaCarga(JFrame ventanaPadre) {
        super(ventanaPadre, true);
        setUndecorated(true);
        setSize(400, 120);
        
        JPanel panel = new JPanel(new BorderLayout());
        // 1. FONDO AZUL OSCURO
        panel.setBackground(new Color(40, 66, 119));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // 2. TEXTO BLANCO
        JLabel lblTexto = new JLabel("Cargando, espere un momento...", SwingConstants.CENTER);
        lblTexto.setFont(new Font("Cy Grotesk Key", Font.PLAIN, 16));
        lblTexto.setForeground(Color.WHITE);
        lblTexto.setBorder(new EmptyBorder(0, 0, 15, 0));

        // 3. NUESTRA BARRA MANUAL (Para efecto de rebote perfecto)
        barra = new BarraPersonalizada();
        barra.setPreferredSize(new Dimension(100, 20)); // Tamaño

        // 4. ANIMACIÓN CONTROLADA
        // Velocidad: 15ms (60 FPS aprox para que se vea súper fluido)
        animacionTimer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                barra.moverBloque(); // Movemos el cuadrito
                barra.repaint();     // Pintamos la nueva posición
            }
        });
        animacionTimer.start();

        panel.add(lblTexto, BorderLayout.NORTH);
        panel.add(barra, BorderLayout.CENTER);

        add(panel);
        setLocationRelativeTo(ventanaPadre);
    }

    public void cerrar() {
        if (animacionTimer != null && animacionTimer.isRunning()) {
            animacionTimer.stop();
        }
        this.dispose();
    }

    // ========================================================================
    // CLASE INTERNA: DIBUJAMOS LA BARRA NOSOTROS MISMOS
    // ========================================================================
    private class BarraPersonalizada extends JPanel {
        
        private int x = 0;           // Posición actual del bloque azul
        private int velocidadX = 2;  // Velocidad de movimiento (Píxeles por frame)
        private int anchoBloque = 80; // Tamaño del bloque que rebota

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Para que se vea nítido (antialiasing)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. PINTAR EL FONDO DEL RIEL (Gris claro)
            g2.setColor(new Color(230, 230, 230));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // 2. PINTAR EL BLOQUE AZUL QUE SE MUEVE
            g2.setColor(new Color(0, 120, 215)); // Azul Windows
            // fillRoundRect hace que tenga bordes redondeados suaves
            g2.fillRoundRect(x, 0, anchoBloque, getHeight(), 10, 10);
        }

        // Lógica matemática del rebote
        public void moverBloque() {
            x += velocidadX;

            // Si choca a la derecha -> Invierte dirección
            if (x + anchoBloque >= getWidth()) {
                x = getWidth() - anchoBloque;
                velocidadX = -2; // Rebota a la izquierda
            }
            // Si choca a la izquierda -> Invierte dirección
            if (x <= 0) {
                x = 0;
                velocidadX = 2; // Rebota a la derecha
            }
        }
    }
}