package org.sonar.samples.php.checks;

import org.junit.Test;
import org.sonar.plugins.php.api.tests.PHPCheckTest;
import org.sonar.plugins.php.api.tests.PhpTestFile;

import java.io.File;

public class LaravelAppDebugCheckTest {
    @Test
    public void laravelAppDebugCheckTest() throws Exception {
        PHPCheckTest.check(new LaravelAppDebugCheck(), new PhpTestFile(new File("src/test/resources/checks/php.ini")));
    }
}
