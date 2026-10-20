package rutaverde.modelo;


public class Cliente {

    private final String documento;
    private final String nombreCompleto;
    private final String telefono;
    private final String correo;
    private final String paisOrigen;
    private final String idiomaPreferido;

    public Cliente(String documento, String nombreCompleto, String telefono, String correo,
                   String paisOrigen, String idiomaPreferido) {
        this.documento = requerido(documento, "El documento");
        this.nombreCompleto = requerido(nombreCompleto, "El nombre completo");
        this.telefono = requerido(telefono, "El teléfono");
        this.correo = requerido(correo, "El correo");
        this.paisOrigen = requerido(paisOrigen, "El país de origen");
        this.idiomaPreferido = requerido(idiomaPreferido, "El idioma preferido");
        if (!correo.contains("@")) {
            throw new IllegalArgumentException("El correo no es válido");
        }
    }

    private static String requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        return valor;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public String getIdiomaPreferido() {
        return idiomaPreferido;
    }
}