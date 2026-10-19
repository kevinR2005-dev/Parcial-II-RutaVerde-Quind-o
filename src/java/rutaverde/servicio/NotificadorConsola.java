package rutaverde.servicio;

public class NotificadorConsola implements Notificador {

    @Override
    public void enviar(String destinatario, String asunto, String mensaje) {
        System.out.println("[Correo] Para: " + destinatario);
        System.out.println("[Correo] Asunto: " + asunto);
        System.out.println(mensaje);
        System.out.println("----------------------------------------");
    }
}
