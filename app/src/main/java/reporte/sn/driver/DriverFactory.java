package reporte.sn.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {
    public static WebDriver crearChrome(String rutaDescarga) {

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();

        prefs.put("download.default_directory", rutaDescarga);
        prefs.put("download.prompt_for_download", false);
        prefs.put("credentials_enable_service", false); // Inhabilita el servicio de credenciales
        prefs.put("profile.password_manager_enabled", false); // Inhabilita el gestos de contraseñas
        prefs.put("profile.password_manager_leak_detection", false); // Evita el mensaje de "Cambiar Contraseña"

        options.setExperimentalOption("prefs", prefs);

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }
}