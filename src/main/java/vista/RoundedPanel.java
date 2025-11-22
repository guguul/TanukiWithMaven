package vista;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {

    private int cornerRadius = 80; // Radio de las esquinas (puedes ajustarlo)

    public RoundedPanel() {
        // Establecer el panel como no opaco es CRUCIAL para que se vea el fondo redondeado
        setOpaque(false); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Convertir a Graphics2D para usar características avanzadas
        Graphics2D g2 = (Graphics2D) g;
        
        // Habilitar el antialiasing para que los bordes se vean suaves y no pixelados
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar el fondo redondeado
        g2.setColor(getBackground()); // Usa el color que le hayas asignado al panel
        
        // g2.fillRoundRect(x, y, ancho, alto, radio_ancho, radio_alto);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
    }
}
