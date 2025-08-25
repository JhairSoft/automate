package reporte.sn.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import reporte.sn.utils.Waiter;

public class LoginPage {
    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String usuario, String clave) {
        driver.get(reporte.sn.config.Config.get("url.login"));
        Waiter.visible(driver, By.id("username"), 10).sendKeys(usuario);
        Waiter.visible(driver, By.id("password"), 10).sendKeys(clave);
        Waiter.clickable(driver, By.name("login"), 10).submit();
        System.out.println("    1.1 -> Login Satisfactorio.");
        Waiter.clickable(driver, By.xpath("//span[text()='Vista ITSM']"), 10).click();
    }
}