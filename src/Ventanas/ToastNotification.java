package Ventanas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.RenderingHints;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class ToastNotification {

    public enum Type {
        SUCCESS(new Color(220, 252, 231), new Color(21, 128, 61)),
        WARNING(new Color(254, 249, 195), new Color(161, 98, 7)),
        ERROR(new Color(254, 226, 226), new Color(185, 28, 28));

        private final Color background;
        private final Color foreground;

        Type(Color background, Color foreground) {
            this.background = background;
            this.foreground = foreground;
        }
    }

    private final Window owner;

    public ToastNotification(Window owner) {
        this.owner = owner;
    }

    public void showMessage(String mensaje, int duration) {
        showMessage(mensaje, duration, Type.WARNING);
    }

    public void showMessage(String mensaje, int duration, Type type) {
        final JWindow ventana = new JWindow(owner);
        final float[] opacity = new float[]{0.0f};

        RoundedPanel panel = new RoundedPanel(22, type.background);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(type.foreground),
                new EmptyBorder(14, 20, 14, 20)));

        JLabel texto = new JLabel(mensaje, SwingConstants.CENTER);
        texto.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        texto.setForeground(type.foreground);
        panel.add(texto, BorderLayout.CENTER);

        ventana.setBackground(new Color(0, 0, 0, 0));
        ventana.setContentPane(panel);
        ventana.pack();

        Dimension size = ventana.getPreferredSize();
        int width = Math.max(size.width, 280);
        int height = Math.max(size.height, 62);
        ventana.setSize(width, height);
        posicionarVentana(ventana);
        ventana.setAlwaysOnTop(true);

        final boolean fadeSupported = aplicarOpacidad(ventana, 0.0f);
        ventana.setVisible(true);

        Timer fadeIn = new Timer(30, null);
        fadeIn.addActionListener(e -> {
            if (fadeSupported) {
                opacity[0] = Math.min(0.96f, opacity[0] + 0.12f);
                aplicarOpacidad(ventana, opacity[0]);
            }
            if (!fadeSupported || opacity[0] >= 0.96f) {
                fadeIn.stop();
                Timer espera = new Timer(duration, ev -> iniciarFadeOut(ventana, opacity, fadeSupported));
                espera.setRepeats(false);
                espera.start();
            }
        });
        fadeIn.start();
    }

    private void iniciarFadeOut(final JWindow ventana, final float[] opacity, final boolean fadeSupported) {
        Timer fadeOut = new Timer(30, null);
        fadeOut.addActionListener(e -> {
            if (fadeSupported) {
                opacity[0] = Math.max(0.0f, opacity[0] - 0.12f);
                aplicarOpacidad(ventana, opacity[0]);
            }
            if (!fadeSupported || opacity[0] <= 0.01f) {
                fadeOut.stop();
                ventana.setVisible(false);
                ventana.dispose();
            }
        });
        fadeOut.start();
    }

    private boolean aplicarOpacidad(JWindow ventana, float valor) {
        try {
            ventana.setOpacity(valor);
            return true;
        } catch (UnsupportedOperationException ex) {
            return false;
        } catch (IllegalComponentStateException ex) {
            return false;
        }
    }

    private void posicionarVentana(JWindow ventana) {
        if (owner != null && owner.isShowing()) {
            int x = owner.getX() + ((owner.getWidth() - ventana.getWidth()) / 2);
            int y = owner.getY() + 40;
            ventana.setLocation(x, y);
        } else {
            ventana.setLocationRelativeTo(null);
        }
    }

    public static void showSuccess(Window owner, String mensaje, int duration) {
        new ToastNotification(owner).showMessage(mensaje, duration, Type.SUCCESS);
    }

    public static void showWarning(Window owner, String mensaje, int duration) {
        new ToastNotification(owner).showMessage(mensaje, duration, Type.WARNING);
    }

    public static void showError(Window owner, String mensaje, int duration) {
        new ToastNotification(owner).showMessage(mensaje, duration, Type.ERROR);
    }

    private static final class RoundedPanel extends JPanel {

        private final int radio;
        private final Color colorFondo;

        private RoundedPanel(int radio, Color colorFondo) {
            this.radio = radio;
            this.colorFondo = colorFondo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class RoundedBorder extends AbstractBorder {

        private final Color color;

        private RoundedBorder(Color color) {
            this.color = color;
        }

        @Override
        public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(15, 23, 42, 18));
            g2.fillRoundRect(x + 2, y + 4, width - 4, height - 4, 22, 22);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, 22, 22);
            g2.dispose();
        }

        @Override
        public java.awt.Insets getBorderInsets(java.awt.Component c) {
            return new java.awt.Insets(6, 6, 6, 6);
        }

        @Override
        public java.awt.Insets getBorderInsets(java.awt.Component c, java.awt.Insets insets) {
            insets.left = 6;
            insets.right = 6;
            insets.top = 6;
            insets.bottom = 6;
            return insets;
        }
    }
}
