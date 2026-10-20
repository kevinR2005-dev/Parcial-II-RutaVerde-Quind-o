package rutaverde.modelo;

public class Agencia {

    private final String nombreComercial;
    private final String nit;
    private final String rnt;
    private final String direccion;
    private final String telefono;
    private final String correo;
    private final String paginaWeb;

    public Agencia(String nombreComercial, String nit, String rnt, String direccion,
                   String telefono, String correo, String paginaWeb) {
        if (nombreComercial == null || nombreComercial.isBlank()) {
            throw new IllegalArgumentException("El nombre comercial es obligatorio");
        }
        if (nit == null || nit.isBlank()) {
            throw new IllegalArgumentException("El NIT es obligatorio");
        }
        if (rnt == null || rnt.isBlank()) {
            throw new IllegalArgumentException("El RNT es obligatorio");
        }
        this.nombreComercial = nombreComercial;
        this.nit = nit;
        this.rnt = rnt;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.paginaWeb = paginaWeb;
    }

    public String getNombreComercial() {

        return nombreComercial;
    }

    public String getNit() {

        return nit;
    }

    public String getRnt() {
        return rnt;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }
}