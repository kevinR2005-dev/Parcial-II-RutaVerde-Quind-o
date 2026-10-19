package rutaverde.servicio;

public interface Notificador {
    void enviar(String destinatario, String asunto, String mensaje);
}
