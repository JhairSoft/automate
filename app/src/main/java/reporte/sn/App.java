package reporte.sn;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebDriver;

import reporte.sn.config.Config;
import reporte.sn.db.OrquestadorCarga;
import reporte.sn.driver.DriverFactory;
import reporte.sn.pages.LoginPage;
import reporte.sn.pages.ReportePage;
import reporte.sn.service.ConversorCSV;
import reporte.sn.service.ExportadorCSV;

public class App {
    public static void main(String[] args) {
        WebDriver driver = DriverFactory.crearChrome(Config.get("ruta.descarga"));

        // 1. Procesos Web
        try {
            new LoginPage(driver).login(Config.get("usuario"), Config.get("clave"));

            List<String> tipos = List.of("INC", "RITM");
            for (String tipo : tipos) {
                String reportId = Config.get("report.id." + tipo.toLowerCase());
                String urlReporte = Config.get("url.reporte.base") + reportId;
                String nombreArchivo = Config.get("archivo.nombre." + tipo.toLowerCase());

                new ReportePage(driver).navegarA(urlReporte);

                ExportadorCSV exportador = new ExportadorCSV(driver, Config.get("ruta.descarga"));
                Optional<Path> archivo = exportador.exportar(Duration.ofSeconds(60));

                archivo.ifPresent(origen -> {
                    Path destino = Paths.get(Config.get("ruta.final"), nombreArchivo);
                    new ConversorCSV().convertirAUTF8(origen, destino);
                    System.out.println("[" + tipo + "] Archivo convertido y guardado como: " + destino);
                });
            }
        } finally {
            driver.quit();
        }

        // 2. Procesos de base de datos
        try {
            new OrquestadorCarga().ejecutar();
            System.out.println("Proceso finalizado correctamente.");
        } catch (Exception e) {
            System.err.println("Error en el proceso: " + e.getMessage());
            e.printStackTrace();
        }
    }
}