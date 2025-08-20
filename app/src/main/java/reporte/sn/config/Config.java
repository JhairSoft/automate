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

    public static String get(String clave){
        return props.getProperty(clave);
    }
}
