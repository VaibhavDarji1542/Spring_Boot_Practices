package TryPackage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;
import java.util.*;

public class ParallelTest {

    private static final List<String> urls = Arrays.asList(
        "https://amazon.com/",
        "https://dropbox.com/",
        "https://slack.com/",
        "https://airbnb.com/",
        "https://paypal.com/",
        "https://nike.com/",
        "https://bmw.com/",
        "https://lg.com/",
        "https://facebook.com/",
        "https://microsoft.com/"
    );
    int ThreadCount = 3;

    // List to store all driver instances
    private static List<WebDriver> drivers = new ArrayList<>();

    @DataProvider(parallel = true)
    public Object[][] urlProvider() {
        Object[][] data = new Object[ThreadCount][1];
        for (int i = 0; i < ThreadCount; i++) {
            data[i][0] = urls.get(i);
        }
        return data;
    }

    // Initialize WebDriver for each test thread
    @BeforeMethod
    public void setup() {
        WebDriver driver = new FirefoxDriver();
        drivers.add(driver); // Store the driver in the list
    }

    @Test(dataProvider = "urlProvider", priority = 1)
    public void login(String url) {
        WebDriver driver = drivers.get(drivers.size() - 1); // Get the latest driver added
        if (driver == null) {
            throw new IllegalStateException("Driver is null! Check your setup.");
        }
        driver.get(url);
        System.out.println(Thread.currentThread().getName() + " - Logging in to: " + driver.getTitle());
    }

    // This will close all drivers after all tests are done
    @AfterSuite
    public void tearDown() {
        // Loop through and quit all WebDriver instances
        for (WebDriver driver : drivers) {
            if (driver != null) {
                driver.quit();
                System.out.println("Closed driver: " + driver);
            }
        }
    }
}
