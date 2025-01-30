package TryPackage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.*;
import java.util.*;
import java.util.concurrent.*;

public class ParallelTest {

    // List of URLs to navigate through
    private static final List<String> urls = Arrays.asList(
        "https://www.google.com",
        "https://www.facebook.com",
        "https://www.amazon.com",
        "https://www.youtube.com",
        "https://www.wikipedia.org",
        "https://www.twitter.com",
        "https://www.linkedin.com",
        "https://www.instagram.com",
        "https://www.microsoft.com",
        "https://www.apple.com"
    );
    private static ExecutorService executorService;

    // ThreadLocal to ensure WebDriver instance is unique to each thread
    private static ThreadLocal<WebDriver> driverThreadLocal = ThreadLocal.withInitial(() -> new FirefoxDriver());

    @BeforeTest
    public void setup() {
        // Initialize ExecutorService with 5 threads to run tests in parallel for 5 URLs at a time
        executorService = Executors.newFixedThreadPool(5);

        // List to store tasks for parallel execution
        List<Callable<String>> tasks = new ArrayList<>();

        // Loop over the first 5 URLs and assign each thread a URL to test
        for (int i = 0; i < 5; i++) {
            String url = urls.get(i); // Get URL from array
            tasks.add(() -> {
                WebDriver driver = driverThreadLocal.get(); // Get WebDriver from ThreadLocal
                runTests(driver, url); // Call runTests for each URL
                driver.quit(); // Close the browser after tests
                return "Completed tests for: " + url;
            });
        }

        // Execute all tasks in parallel
        try {
            List<Future<String>> results = executorService.invokeAll(tasks);
            // Print out the results
            for (Future<String> result : results) {
                System.out.println(result.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // Shut down the ExecutorService properly after all tests
            if (executorService != null) {
                executorService.shutdown();
            }
        }
    }

    // This method should not have parameters; we will inject the URL manually
    @Test
    @Parameters({"url"})
    public void login(String url) {
        WebDriver driver = driverThreadLocal.get(); // Get WebDriver from ThreadLocal
        driver.get(url); // Navigate to the URL
        System.out.println(Thread.currentThread().getName() + " - Logging in to: " + driver.getTitle());
    }

    private void runTests(WebDriver driver, String url) {
        // Run the tests for the given URL with the provided driver
        login(url); // Pass URL directly to the login method
        // Add more test steps like redirect, placeOrder, signOut here
    }

    @AfterTest
    public void tearDown() {
        // Ensure that the ExecutorService is shut down properly
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
