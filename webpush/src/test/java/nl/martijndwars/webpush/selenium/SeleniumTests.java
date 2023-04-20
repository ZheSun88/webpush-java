package nl.martijndwars.webpush.selenium;

import java.io.IOException;
import java.net.URI;
import java.security.Security;
import java.util.Base64;
import java.util.stream.Stream;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * SeleniumTest performs integration testing.
 */
public class SeleniumTests {

    protected static final String GCM_SENDER_ID = "759071690750";
    protected static final String PUBLIC_KEY = "BNFDO1MUnNpx0SuQyQcAAWYETa2+W8z/uc5sxByf/UZLHwAhFLwEDxS5iB654KHiryq0AxDhFXS7DVqXDKjjN+8=";

    protected static TestingService testingService = new TestingService(URI.create("http://localhost:8090/api/"));
    protected static int testSuiteId;

    public SeleniumTests() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * End the test suite.
     */
    @AfterAll
    public static void tearDown() throws IOException, InterruptedException {
        testingService.endTestSuite(testSuiteId);
    }

    /**
     * Generate a stream of tests based on the configurations.
     */
    @TestFactory
    public Stream<DynamicTest> dynamicTests() throws IOException, InterruptedException {
        testSuiteId = testingService.startTestSuite();

        return getConfigurations().map(configuration -> {
            BrowserTest browserTest = new BrowserTest(testingService, configuration, testSuiteId);

            return DynamicTest.dynamicTest(browserTest.getDisplayName(), browserTest);
        });
    }

    /**
     * Get browser configurations to test.
     */
    protected Stream<Configuration> getConfigurations() {
        String PUBLIC_KEY_NO_PADDING = Base64.getUrlEncoder().withoutPadding().encodeToString(
            Base64.getUrlDecoder().decode(PUBLIC_KEY)
        );

        return Stream.of(
            new Configuration("chrome", "stable", null, GCM_SENDER_ID),
            new Configuration("chrome", "beta", null, GCM_SENDER_ID),
            //new Configuration("chrome", "unstable", null, GCM_SENDER_ID), See #90

            new Configuration("firefox", "stable", null, GCM_SENDER_ID),
            new Configuration("firefox", "beta", null, GCM_SENDER_ID),

            new Configuration("chrome", "stable", PUBLIC_KEY_NO_PADDING, null),
            new Configuration("chrome", "beta", PUBLIC_KEY_NO_PADDING, null),
            //new Configuration("chrome", "unstable", PUBLIC_KEY_NO_PADDING, null), See #90

            new Configuration("firefox", "stable", PUBLIC_KEY_NO_PADDING, null),
            new Configuration("firefox", "beta", PUBLIC_KEY_NO_PADDING, null)
        );
    }
}
