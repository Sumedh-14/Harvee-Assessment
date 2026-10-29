package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Saves a PNG to target/screenshots and links it in the TestNG HTML report. */
public final class ScreenshotUtil {

    private static final Path DIR = Paths.get("target", "screenshots");

    private ScreenshotUtil() {
    }

    public static Path capture(WebDriver driver, String name) {
        try {
            Files.createDirectories(DIR);
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            Path target = DIR.resolve(name.replaceAll("[^A-Za-z0-9_-]", "_") + "_" + stamp + ".png");
            Path source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            Reporter.log("Screenshot: <a href=\"" + target.toAbsolutePath().toUri() + "\">" + target.getFileName() + "</a>");
            return target;
        } catch (IOException e) {
            Reporter.log("Could not save screenshot: " + e.getMessage());
            return null;
        }
    }
}
