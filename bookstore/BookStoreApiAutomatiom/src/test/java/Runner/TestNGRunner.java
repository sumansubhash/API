package Runner;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;

@CucumberOptions(
        features = "src/test/resources/Features",
        glue = {"Steps","Hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-results/cucumberReport.html",
                "json:target/cucumber-results/cucumberReport.json",
        },
        tags = "")
public class TestNGRunner extends AbstractTestNGCucumberTests {
}

