import org.testng.annotations.Test;
import rutaverde.modelo.pago.ValidadorTarjeta;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class ValidadorTarjetaTest {

    private final ValidadorTarjeta validador = new ValidadorTarjeta();

    @Test
    public void tarjetaValidaDelEnunciado() {
        assertTrue(validador.esValida("4539 1488 0343 6467"));
    }

    @Test
    public void tarjetaInvalidaDelEnunciado() {
        assertFalse(validador.esValida("4539 1488 0343 6468"));
    }

    @Test
    public void ignoraLosEspacios() {
        assertTrue(validador.esValida("4539148803436467"));
    }

    @Test
    public void rechazaMenosDe13Digitos() {
        assertFalse(validador.esValida("453914880343")); // 12 dígitos
    }

    @Test
    public void rechazaMasDe19Digitos() {
        assertFalse(validador.esValida("45391488034364670000")); // 20 dígitos
    }

    @Test
    public void rechazaCaracteresNoNumericos() {
        assertFalse(validador.esValida("4539 1488 0343 64AB"));
    }

    @Test
    public void rechazaNuloYVacio() {
        assertFalse(validador.esValida(null));
        assertFalse(validador.esValida(""));
    }
}
