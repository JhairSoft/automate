package reporte.sn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class App {

    private static WebDriver driver;

    private static String rutaDescarga = "C:\\SERVNOW\\prueba";
    private static String rutaFinal = "C:\\SERVNOW";

    public static void main(String[] args) {
        try {
            setupDriver();
            login();
            navegarAReporte();
            exportarCSV();
        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
        }
        /*
         * } finally {
         * cerrarDriver();
         * }
         */
    }

    private static void setupDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", rutaDescarga); // Ruta para almacenar la descarga
        prefs.put("download.prompt_for_download", false); // Evita la confirmación de donde se desea guardar
        prefs.put("credentials_enable_service", false); // Inhabilita el servicio de credenciales
        prefs.put("profile.password_manager_enabled", false); // Inhabilita el gestos de contraseñas
        prefs.put("profile.password_manager_leak_detection", false); // Evita el mensaje de "Cambiar Contraseña"
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    private static void login() {
        driver.get("https://sd.pacifico.com.pe/pacificoportal?id=landing_pacifico_2");

        esperarVisible(By.id("username"), 10).sendKeys("ChapterAcselX");
        esperarVisible(By.id("password"), 10).sendKeys("ChapterAcselX12345678");
        esperarClickable(By.name("login"), 10).submit();
        esperarClickable(By.xpath("//span[text()='Vista ITSM']"), 10).click();
    }

    private static void navegarAReporte() {
        driver.get("https://sd.pacifico.com.pe/sys_report_template.do?jvar_report_id=acc31c12337f1650db87d8123d5c7bfb");
    }

    private static void exportarCSV() {
        try {
            WebElement intro = esperarVisible(By.cssSelector(
                    "table.data_list_table.list_table.table.table-hover.list_header_search_disabled > thead > tr > th.text-align-left.list_header_cell.list_hdr"),
                    30);

            new Actions(driver).contextClick(intro).build().perform();

            WebElement opciones = esperarVisible(By.cssSelector(
                    "div.context_item[data-context-menu-label='Exportar']"), 5);
            new Actions(driver).moveToElement(opciones).perform();

            WebElement csv = esperarClickable(By.xpath("//div[@class='context_item' and text()='CSV']"), 5);
            csv.click();

            // cuando la descarga será grande sale la alerta
            aceptarAlertaSiExiste(5);

            // cuando la descarga será grande sale el boton
            Optional<WebElement> botonEspera = esperarElementoSiExiste(By.cssSelector("button.web#export_wait"), 5);
            botonEspera.ifPresent(WebElement::click);

            Set<String> existentes = snapshotArchivosExistentes(Paths.get("C:\\SERVNOW\\prueba"));
            esperarClickable(By.cssSelector("button.web.btn.btn-primary#download_button"), 300).click();

            Optional<Path> archivoDescargado = esperarNuevoArchivo(
                    Paths.get("C:\\SERVNOW\\prueba"), ".csv", existentes, Duration.ofSeconds(60));

            archivoDescargado.ifPresentOrElse(
                    path -> System.out.println("Archivo nuevo descargado: " + path),
                    () -> System.out.println("No se detectó archivo nuevo en el tiempo esperado."));

            archivoDescargado.ifPresent(origen -> {
                Path destinoUTF8 = Paths.get(rutaFinal, "INC.csv");
                convertirCSVaUTF8(origen, destinoUTF8);
                System.out.println("Archivo convertido y movido a: " + destinoUTF8);
            });

        } catch (NoSuchElementException | TimeoutException | NoSuchFrameException e) {
            System.out.println("Error durante exportación: " + e.getMessage());
        }
    }

    private static void aceptarAlertaSiExiste(int segundos) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(segundos));
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (TimeoutException e) {
            // No hay alerta, continuar sin error
        }
    }

    private static WebElement esperarVisible(By locator, int segundos) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(segundos));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private static WebElement esperarClickable(By locator, int segundos) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(segundos));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private static Optional<WebElement> esperarElementoSiExiste(By locator, int segundos) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(segundos));
            return Optional.of(wait.until(ExpectedConditions.elementToBeClickable(locator)));
        } catch (TimeoutException e) {
            return Optional.empty();
        }
    }

    private static long getLastModifiedSafe(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return 0L;
        }
    }

    private static boolean archivoCompleto(Path path) {
        try {
            long size1 = Files.size(path);
            Thread.onSpinWait(); // microespera sin sleep
            long size2 = Files.size(path);
            return size1 == size2 && size1 > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static Set<String> snapshotArchivosExistentes(Path carpeta) {
        try (Stream<Path> archivos = Files.list(carpeta)) {
            return archivos
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            return Set.of();
        }
    }

    private static Optional<Path> esperarNuevoArchivo(Path carpeta, String extension, Set<String> existentes,
            Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < deadline) {
            try (Stream<Path> archivos = Files.list(carpeta)) {
                Optional<Path> nuevo = archivos
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(extension))
                        .filter(p -> !existentes.contains(p.getFileName().toString()))
                        .filter(App::archivoCompleto)
                        .max(Comparator.comparingLong(App::getLastModifiedSafe));

                if (nuevo.isPresent())
                    return nuevo;
            } catch (IOException e) {
                // Ignorar errores transitorios
            }
        }

        return Optional.empty();
    }

    private static void convertirCSVaUTF8(Path origen, Path destino) {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(origen.toFile()), Charset.forName("Windows-1252")));
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(destino.toFile()), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                writer.write(linea);
                writer.newLine();
            }
            System.out.println("Archivo convertido a UTF-8: " + destino);
        } catch (IOException e) {
            throw new RuntimeException("Error al convertir CSV a UTF-8: " + e.getMessage());
        }
    }

    /*
     * private static void cerrarDriver() {
     * if (driver != null) {
     * driver.quit();
     * }
     * }
     */

}
