package reporte.sn.config;

import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config/config.properties")) {
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar configuración: " + e.getMessage());
        }
    }

    public static String get(String clave) {
        String valor = props.getProperty(clave);
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Clave no definida en configuración: " + clave);
        }
        return valor.trim();
    }

    public static InputStream getFile(String claveRuta) {
        String rutaRelativa = get(claveRuta); // usa el método get para obtener la ruta

        InputStream input = Config.class.getClassLoader().getResourceAsStream(rutaRelativa);
        if (input == null) {
            throw new IllegalArgumentException("No se encontró el recurso en el classpath: " + rutaRelativa);
        }

        return input;
    }
}
