package reporte.sn.pages;

import org.openqa.selenium.WebDriver;

public class ReportePage {
    private final WebDriver driver;

    public ReportePage(WebDriver driver) {
        this.driver = driver;
    }

    public void navegarA(String url) {
        driver.get(url);
        System.out.println("Dirigiendo a la ruta: " + url);
    }
}