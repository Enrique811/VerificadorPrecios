/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Metodos;

import Ventanas.VentanaBuscarArticulo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class FormatoTablaBArt extends DefaultTableCellRenderer {

    private static final Color COLOR_AZUL = new Color(45, 111, 214);
    private static final Color COLOR_AZUL_SUAVE = new Color(239, 246, 255);
    private static final Color COLOR_BORDE = new Color(229, 231, 235);
    private static final Color COLOR_TEXTO = new Color(31, 41, 55);

    JTableHeader encabezadoTabla = new JTableHeader();

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object value, boolean selected, boolean focused, int row, int column) {
        encabezadoTabla = tabla.getTableHeader();
        encabezadoTabla.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        encabezadoTabla.setBackground(Color.WHITE);
        encabezadoTabla.setForeground(COLOR_TEXTO);

        setAnchoColumnas();
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tabla.setRowHeight(34);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setSelectionBackground(COLOR_AZUL_SUAVE);
        tabla.setSelectionForeground(COLOR_TEXTO);

        super.getTableCellRendererComponent(tabla, value, selected, focused, row, column);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
        if (selected) {
            setForeground(COLOR_TEXTO);
            setBackground(COLOR_AZUL_SUAVE);
        } else {
            setForeground(COLOR_TEXTO);
            setBackground(Color.WHITE);
        }
        return this;
    }

    public static void setAnchoColumnas() {
        JViewport scroll = (JViewport) VentanaBuscarArticulo.listaArticulos.getParent();
        int ancho = scroll.getWidth();
        int anchoColumna = 0;
        TableColumnModel modeloColumna = VentanaBuscarArticulo.listaArticulos.getColumnModel();
        TableColumn columnaTabla;

        DefaultTableCellRenderer modeloizquierda = new DefaultTableCellRenderer();
        modeloizquierda.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer modelocentrar = new DefaultTableCellRenderer();
        modelocentrar.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer modeloderecha = new DefaultTableCellRenderer();
        modeloderecha.setHorizontalAlignment(SwingConstants.RIGHT);

        VentanaBuscarArticulo.listaArticulos.getColumnModel().getColumn(0).setCellRenderer(modeloizquierda);
        VentanaBuscarArticulo.listaArticulos.getColumnModel().getColumn(1).setCellRenderer(modeloizquierda);
        VentanaBuscarArticulo.listaArticulos.getColumnModel().getColumn(2).setCellRenderer(modeloderecha);
        VentanaBuscarArticulo.listaArticulos.getColumnModel().getColumn(3).setCellRenderer(modeloderecha);
        
        for (int i = 0; i < VentanaBuscarArticulo.listaArticulos.getColumnCount(); i++) {
            columnaTabla = modeloColumna.getColumn(i);
            switch (i) {
                case 0:
                    anchoColumna = (25 * ancho) / 100;
                    break;
                case 1:
                    anchoColumna = (45 * ancho) / 100;
                    break;
                case 2:
                    anchoColumna = (15 * ancho) / 100;
                    break;
                case 3:
                    anchoColumna = (15 * ancho) / 100;
                    break;           
            }
            columnaTabla.setPreferredWidth(anchoColumna);
        }
    }
}

