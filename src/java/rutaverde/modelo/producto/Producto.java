package rutaverde.modelo.producto;

import java.util.List;


public interface Producto {

    String getCodigo();

    String getNombre();


    double getPrecioPorPersona();


    int getDuracionHoras();


    List<Actividad> getActividades();
}