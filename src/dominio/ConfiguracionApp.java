package dominio;

public final class ConfiguracionApp {

    private final String ipEmpresa;
    private final String rutaEmpresa;
    private final String usuario;
    private final String password;
    private final String clave;
    private final String informacion;
    private final String impresora;
    private final String ambiente;
    private final String formatoPrecio;
    private final String reporte;

    public ConfiguracionApp(String ipEmpresa, String rutaEmpresa, String usuario, String password,
            String clave, String informacion, String impresora, String ambiente,
            String formatoPrecio, String reporte) {
        this.ipEmpresa = ipEmpresa;
        this.rutaEmpresa = rutaEmpresa;
        this.usuario = usuario;
        this.password = password;
        this.clave = clave;
        this.informacion = informacion;
        this.impresora = impresora;
        this.ambiente = ambiente;
        this.formatoPrecio = formatoPrecio;
        this.reporte = reporte;
    }

    public String getIpEmpresa() {
        return ipEmpresa;
    }

    public String getRutaEmpresa() {
        return rutaEmpresa;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }

    public String getClave() {
        return clave;
    }

    public String getInformacion() {
        return informacion;
    }

    public String getImpresora() {
        return impresora;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public String getFormatoPrecio() {
        return formatoPrecio;
    }

    public String getReporte() {
        return reporte;
    }
}
