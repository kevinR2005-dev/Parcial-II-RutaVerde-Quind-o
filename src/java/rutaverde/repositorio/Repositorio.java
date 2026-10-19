package rutaverde.repositorio;

import java.util.List;


public interface Repositorio<T, ID> {


    void guardar(T t);

    @throws rutaverde.excepcion.EntidadNoEncontradaException
    T buscarPorId(ID id);


    List<T> listar();

     @throws rutaverde.excepcion.EntidadNoEncontradaException
 ;   void eliminar(ID id);
}