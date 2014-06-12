package dk.pfrandsen;

import dk.pfrandsen.GenerateConfigFile;
import dk.pfrandsen.Runner;
import dk.pfrandsen.wsdl.wsi.Helper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class GenerateConfigFileTest {
    private Path toolsRoot;
    private Path outputPath;
    private Path output;
    private Path wsdl;
    private Path report;

    @Before
    public void setUp() {
        toolsRoot = Paths.get("WS-I", "wsi-test-tools");
        outputPath = Paths.get("target", "config_file");
        output = outputPath.resolve("config.xml"); // Paths.get(outputPath.toString(), "config.xml");
        wsdl = Paths.get("target", "test-classes", "wsdl", "wsdl_1.wsdl");
        report = outputPath.resolve("report.xml"); //Paths.resget(outputPath.toString(), "report.xml");
        outputPath.toFile().mkdirs();
    }

    @After
    public void cleanUp() {
        if (outputPath.toString().startsWith("target")) {
            Helper.deleteFolder(outputPath);
        }
    }

    @Test
    public void testGenerateConfigFileMain() {
        String[] args = {arg(Runner.OPTION_CONFIG_FILE),
                arg(GenerateConfigFile.OPTION_ROOT), toolsRoot.toString(),
                arg(GenerateConfigFile.OPTION_OUTPUT), output.toString(),
                arg(GenerateConfigFile.OPTION_WSDL), wsdl.toString(),
                arg(GenerateConfigFile.OPTION_REPORT), report.toString()};
        GenerateConfigFile.main(args);
        assertTrue("File " + output + " does not exist", Files.exists(output));

    }

    String arg(String arg) {
        return "-" + arg;
    }

}
