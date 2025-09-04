package reporte.sn.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import reporte.sn.config.Config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcesoReporteTest {

    @Mock
    private WebDriver mockDriver;

    @Test
    void ejecutar_deberiaExportarYConvertirArchivo() {
        // 1. Arrange
        String tipo = "TEST";
        String nombreArchivo = "reporte_test.csv";
        String rutaDescarga = "C:\\temp\\descargas";
        String rutaFinal = "C:\\temp\\final";
        int timeout = 30;
        Path mockPathOrigen = Paths.get(rutaDescarga, "temp.csv");
        Path expectedPathDestino = Paths.get(rutaFinal, nombreArchivo);

        // Mockear la clase estática Config
        try (MockedStatic<Config> mockedConfig = mockStatic(Config.class)) {
            mockedConfig.when(() -> Config.get("ruta.descarga")).thenReturn(rutaDescarga);
            mockedConfig.when(() -> Config.get("ruta.final")).thenReturn(rutaFinal);
            mockedConfig.when(() -> Config.get("timeout.descarga.segundos")).thenReturn(String.valueOf(timeout));

            // Mockear la construcción de ExportadorCSV y ConversorCSV
            try (MockedConstruction<ExportadorCSV> mockedExportador = mockConstruction(ExportadorCSV.class,
                    (mock, context) -> {
                        // Definir comportamiento del mock de ExportadorCSV
                        when(mock.exportar(Duration.ofSeconds(timeout))).thenReturn(Optional.of(mockPathOrigen));
                    });
                 MockedConstruction<ConversorCSV> mockedConversor = mockConstruction(ConversorCSV.class)) {

                // Instanciar la clase a probar
                ProcesoReporte procesoReporte = new ProcesoReporte(mockDriver, tipo, nombreArchivo);

                // 2. Act
                procesoReporte.ejecutar();

                // 3. Assert
                // Verificar que se creó una instancia de ExportadorCSV
                verify(mockedExportador.constructed().get(0)).exportar(Duration.ofSeconds(timeout));

                // Verificar que se creó una instancia de ConversorCSV y se llamó a su método
                verify(mockedConversor.constructed().get(0)).convertirAUTF8(mockPathOrigen, expectedPathDestino);
            }
        }
    }
}
