import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConversorMonedas {

    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/7f2a09a7a499528b5cd1c1ef/latest/";

    public static void main(String[] args) {
        try {
            // Obtener las tasas de cambio de la API
            JsonObject tasas = obtenerTasasDeCambio("USD");
            System.out.println("Tasas de cambio obtenidas: " + tasas);

            // Filtrar las monedas según los códigos especificados
            List<String> codigosMoneda = Arrays.asList("ARS", "BOB", "BRL", "CLP", "COP", "USD");
            JsonObject tasasFiltradas = filtrarTasasPorCodigos(tasas, codigosMoneda);
            System.out.println("Tasas de cambio filtradas: " + tasasFiltradas);

            // Realizar la conversión entre monedas
            double montoAConvertir = Double.parseDouble(obtenerEntradaUsuario("Ingrese el monto a convertir: "));
            String monedaOrigen = obtenerEntradaUsuario("Ingrese la divisa de origen (por ejemplo, USD): ");
            String monedaDestino = obtenerEntradaUsuario("Ingrese la divisa de destino (por ejemplo, EUR): ");
            double montoConvertido = convertirMoneda(montoAConvertir, monedaOrigen, monedaDestino, tasasFiltradas);

            // Mostrar el resultado de la conversión
            System.out.printf("%.2f %s equivale a %.2f %s\n", montoAConvertir, monedaOrigen, montoConvertido, monedaDestino);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String obtenerEntradaUsuario(String mensaje) {
        System.out.print(mensaje);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static JsonObject obtenerTasasDeCambio(String monedaBase) throws IOException {
        URL url = new URL(API_BASE_URL + monedaBase);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder respuesta = new StringBuilder();
        String linea;
        while ((linea = reader.readLine()) != null) {
            respuesta.append(linea);
        }
        reader.close();

        return JsonParser.parseString(respuesta.toString()).getAsJsonObject().getAsJsonObject("conversion_rates");
    }

    private static JsonObject filtrarTasasPorCodigos(JsonObject tasas, List<String> codigosMoneda) {
        JsonObject tasasFiltradas = new JsonObject();
        for (String codigoMoneda : codigosMoneda) {
            if (tasas.has(codigoMoneda)) {
                tasasFiltradas.addProperty(codigoMoneda, tasas.get(codigoMoneda).getAsDouble());
            }
        }
        return tasasFiltradas;
    }

    private static double convertirMoneda(double monto, String monedaOrigen, String monedaDestino, JsonObject tasas) {
        double tasaOrigen = tasas.get(monedaOrigen).getAsDouble();
        double tasaDestino = tasas.get(monedaDestino).getAsDouble();
        return monto * (tasaDestino / tasaOrigen);
    }
}
