package reporte.sn.service;

import org.openqa.selenium.WebDriver;
import reporte.sn.config.Config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;

public class ProcesoReporte {
    private final WebDriver driver;
    private final String tipo;
    private final String nombreArchivo;

    public ProcesoReporte(WebDriver driver, String tipo, String nombreArchivo) {
        this.driver = driver;
        this.tipo = tipo;
        this.nombreArchivo = nombreArchivo;
    }

    public void ejecutar() {
        String rutaDescarga = Config.get("ruta.descarga");
        String rutaFinal = Config.get("ruta.final");
        int timeout = Integer.parseInt(Config.get("timeout.descarga.segundos"));

        ExportadorCSV exportador = new ExportadorCSV(driver, rutaDescarga);
        Optional<Path> archivo = exportador.exportar(Duration.ofSeconds(timeout));

        archivo.ifPresent(origen -> {
            Path destino = Paths.get(rutaFinal, nombreArchivo);
            new ConversorCSV().convertirAUTF8(origen, destino);
            System.out.println("✅ [" + tipo + "] Archivo convertido y guardado como: " + destino);
        });
    }
}