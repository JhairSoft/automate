package reporte.sn.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import reporte.sn.utils.Waiter;

public class ReportePage {
    private final WebDriver driver;

    public ReportePage(WebDriver driver) {
        this.driver = driver;
    }

    public void navegarA(String url) {
        Waiter.clickable(driver, By.xpath("//span[text()='Vista ITSM']"), 10).click();
        driver.get(url);
    }
}