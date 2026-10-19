package rutaverde.servicio;

import rutaverde.modelo.EstadoReserva;
import rutaverde.modelo.Reserva;
import rutaverde.repositorio.Repositorio;

import java.util.List;
import java.util.stream.Collectors;


public class ReservaServicio {
    private final Repositorio<Reserva, Integer> reservas;
    public ReservaServicio(Repositorio<Reserva, Integer> reservas) { this.reservas = reservas; }
    public void registrar(Reserva r) { reservas.guardar(r); }
    public Reserva buscarPorNumero(int n) { return reservas.buscarPorId(n); }
    public List<Reserva> listarConfirmadas() {
        return reservas.listar().stream()
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA).collect(Collectors.toList());
    }
}
