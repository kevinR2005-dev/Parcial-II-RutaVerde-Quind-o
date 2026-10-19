package rutaverde.modelo.pago ;

public class ValidadorTarjeta {

    private static final int MIN_DIGITOS = 13;
    private static final int MAX_DIGITOS = 19;

    public boolean esValida(String numeroTarjeta) {
        if (numeroTarjeta == null) {
            return false;
        }
        String numero = numeroTarjeta.replace(" ", "");
        if (numero.length() < MIN_DIGITOS || numero.length() > MAX_DIGITOS) {
            return false;
        }
        for (int i = 0; i < numero.length(); i++) {
            if (!Character.isDigit(numero.charAt(i))) {
                return false;
            }
        }
        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
            if (duplicar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            duplicar = !duplicar;
        }
        return suma % 10 == 0;
    }
}
