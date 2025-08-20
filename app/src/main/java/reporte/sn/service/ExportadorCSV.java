package reporte.sn.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import reporte.sn.utils.DescargaUtils;
import reporte.sn.utils.Waiter;

public class ExportadorCSV {
    private final WebDriver driver;
    private final Path carpetaDescarga;

    public ExportadorCSV(WebDriver driver, String rutaDescarga) {
        this.driver = driver;
        this.carpetaDescarga = Paths.get(rutaDescarga);
    }

    public Optional<Path> exportar(Duration timeout) {
        WebElement intro = Waiter.visible(driver, By.cssSelector("table.data_list_table.list_table.table.table-hover.list_header_search_disabled > thead > tr > th.text-align-left.list_header_cell.list_hdr"), 30);
        new Actions(driver).contextClick(intro).build().perform();

        WebElement exportar = Waiter.visible(driver, By.cssSelector("div.context_item[data-context-menu-label='Exportar']"), 5);
        new Actions(driver).moveToElement(exportar).perform();

        Waiter.clickable(driver, By.xpath("//div[text()='CSV']"), 5).click();
        aceptarAlertaSiExiste(5);

        Optional<WebElement> botonEspera = Waiter.elementoSiExiste(driver, By.cssSelector("button.web#export_wait"), 5);
        botonEspera.ifPresent(WebElement::click);

        Set<String> existentes = DescargaUtils.snapshotArchivosExistentes(carpetaDescarga);
        Waiter.clickable(driver, By.cssSelector("button.web.btn.btn-primary#download_button"), 300).click();

        return DescargaUtils.esperarNuevoArchivo(carpetaDescarga, ".csv", existentes, timeout);
    }

    private void aceptarAlertaSiExiste(int segundos) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(segundos))
                .until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (TimeoutException ignored) {}
    }
}