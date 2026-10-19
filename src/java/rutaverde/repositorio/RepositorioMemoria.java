package rutaverde.repositorio;

import rutaverde.excepcion.EntidadNoEncontradaException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class RepositorioMemoria<T, ID> implements Repositorio<T, ID> {

    private final Function<T, ID> extractorId;
    // LinkedHashMap conserva el orden de inserción al listar
    private final Map<ID, T> datos = new LinkedHashMap<>();

    public RepositorioMemoria(Function<T, ID> extractorId) {
        if (extractorId == null) {
            throw new IllegalArgumentException("El extractor de id es obligatorio");
        }
        this.extractorId = extractorId;
    }

    @Override
    public void guardar(T t) {
        if (t == null) {
            throw new IllegalArgumentException("No se puede guardar una entidad nula");
        }
        datos.put(extractorId.apply(t), t);
    }

    @Override
    public T buscarPorId(ID id) {
        T encontrado = datos.get(id);
        if (encontrado == null) {
            throw new EntidadNoEncontradaException("No se encontró la entidad con id: " + id);
        }
        return encontrado;
    }

    @Override
    public List<T> listar() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public void eliminar(ID id) {
        if (!datos.containsKey(id)) {
            throw new EntidadNoEncontradaException("No se encontró la entidad con id: " + id);
        }
        datos.remove(id);
    }
}
