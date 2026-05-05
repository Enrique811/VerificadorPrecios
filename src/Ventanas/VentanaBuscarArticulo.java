package Ventanas;

import App.ApplicationContext;

public class VentanaBuscarArticulo extends BusquedaDialog {

    public VentanaBuscarArticulo(java.awt.Frame owner, boolean modal) {
        this(owner, modal, new ApplicationContext());
    }

    public VentanaBuscarArticulo(java.awt.Frame owner, boolean modal, ApplicationContext applicationContext) {
        super(owner, modal, applicationContext);
    }
}
