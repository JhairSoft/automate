package reporte.sn.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.Optional;

public class Waiter {
    public static WebElement visible(WebDriver driver, By locator, int segundos) {
        return new WebDriverWait(driver, Duration.ofSeconds(segundos))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement clickable(WebDriver driver, By locator, int segundos) {
        return new WebDriverWait(driver, Duration.ofSeconds(segundos))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static Optional<WebElement> elementoSiExiste(WebDriver driver, By locator, int segundos) {
        try {
            WebElement elemento = new WebDriverWait(driver, Duration.ofSeconds(segundos))
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
            return Optional.of(elemento);
        } catch (TimeoutException e) {
            return Optional.empty();
        }
    }
}