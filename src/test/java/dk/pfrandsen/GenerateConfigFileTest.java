package dk.pfrandsen;

import dk.pfrandsen.wsdl.wsi.Helper;
import dk.pfrandsen.wsdl.wsi.WsiProfile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

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
        output = outputPath.resolve("config.xml");
        wsdl = Paths.get("target", "test-classes", "wsdl", "wsdl_1.wsdl");
        report = outputPath.resolve("report.xml");
        outputPath.toFile().mkdirs();
    }

    @After
    public void cleanUp() {
        if (outputPath.toString().startsWith("target")) {
            Helper.deleteFolder(outputPath);
        }
    }

    @Test
    public void testGenerateConfigFileMain() throws IOException {
        WsiProfile profile = WsiProfile.BASIC_PROFILE_11_SOAP_10;
        String assertionFile = profile.getAssertionFilename();
        String[] args = {arg(Runner.OPTION_CONFIG_FILE),
                arg(Runner.OPTION_ROOT), toolsRoot.toString(),
                arg(GenerateConfigFile.OPTION_OUTPUT), output.toString(),
                arg(GenerateConfigFile.OPTION_WSDL), wsdl.toString(),
                arg(GenerateConfigFile.OPTION_REPORT), report.toString()};
        GenerateConfigFile.main(args);
        assertTrue("File " + output + " does not exist", Files.exists(output));
        String contents = FileUtils.readFileToString(output.toFile(), "UTF-8");
        assertTrue(contents.contains("<analyze:description>" + profile.getDescription() + "<"));
        assertTrue(contents.contains("location=\"" + report + "\""));
        assertTrue(contents.contains("<analyze:testAssertionsFile>" +
                toolsRoot.resolve(Paths.get("common", "profiles", assertionFile))
                + "</analyze:testAssertionsFile>"));
        assertTrue(contents.contains("<analyze:wsdlElement type=\"binding\""));
        assertTrue(contents.contains("namespace=\"http://service.schemas/domain/service/v1\""));
        assertTrue(contents.contains("<analyze:wsdlURI>" + wsdl + "</analyze:wsdlURI>"));
    }

    @Test
    public void testProfileName() throws IOException {
        WsiProfile profile = WsiProfile.BASIC_PROFILE_11;
        String[] args = {arg(Runner.OPTION_CONFIG_FILE),
                arg(Runner.OPTION_ROOT), toolsRoot.toString(),
                arg(GenerateConfigFile.OPTION_OUTPUT), output.toString(),
                arg(GenerateConfigFile.OPTION_WSDL), wsdl.toString(),
                arg(GenerateConfigFile.OPTION_REPORT), report.toString(),
                arg(GenerateConfigFile.OPTION_PROFILE_NAME), profile.name()};
        GenerateConfigFile.main(args);
        assertTrue("File " + output + " does not exist", Files.exists(output));
        String contents = FileUtils.readFileToString(output.toFile(), "UTF-8");
        assertTrue(contents.contains("<analyze:description>" + profile.getDescription() + "<"));
        assertTrue(contents.contains("<analyze:testAssertionsFile>" +
                toolsRoot.resolve(Paths.get("common", "profiles", profile.getAssertionFilename()))
                + "</analyze:testAssertionsFile>"));
    }

    @Test
    public void testBinding() throws IOException {
        String[] args = {arg(Runner.OPTION_CONFIG_FILE),
                arg(Runner.OPTION_ROOT), toolsRoot.toString(),
                arg(GenerateConfigFile.OPTION_OUTPUT), output.toString(),
                arg(GenerateConfigFile.OPTION_WSDL), wsdl.toString(),
                arg(GenerateConfigFile.OPTION_REPORT), report.toString(),
                arg(GenerateConfigFile.OPTION_BINDING), "1"};
        GenerateConfigFile.main(args);
        assertTrue("File " + output + " does not exist", Files.exists(output));
        String contents = FileUtils.readFileToString(output.toFile(), "UTF-8");
        assertTrue(contents.contains("namespace=\"http://service.schemas/domain/service/v1\""));
        assertTrue(contents.contains(">EntityBinding</analyze:wsdlElement>"));
    }

    @Test
    public void testInvalidBinding() throws IOException, ParseException {
        String[] args = {arg(Runner.OPTION_CONFIG_FILE),
                arg(Runner.OPTION_ROOT), toolsRoot.toString(),
                arg(GenerateConfigFile.OPTION_OUTPUT), output.toString(),
                arg(GenerateConfigFile.OPTION_WSDL), wsdl.toString(),
                arg(GenerateConfigFile.OPTION_REPORT), report.toString(),
                arg(GenerateConfigFile.OPTION_BINDING), "2"};
        GenerateConfigFile tool = new GenerateConfigFile();
        CommandLine cmd = tool.parseCommandLine(args);
        assertFalse(tool.run(cmd));
        assertFalse("File " + output + " should not exist", Files.exists(output));
        assertEquals("WSDL error: Binding index too big, max index is 1", tool.getErrorMessage());
    }

    @Test
    public void testCustomTemplateFile() throws IOException {
        WsiProfile profile = WsiProfile.BASIC_PROFILE_11_SOAP_10;
        Path template = Paths.get("target", "test-classes", "wsi", "custom_binding_template.xml");
        String[] args = {arg(Runner.OPTION_CONFIG_FILE),
                arg(Runner.OPTION_ROOT), toolsRoot.toString(),
                arg(GenerateConfigFile.OPTION_OUTPUT), output.toString(),
                arg(GenerateConfigFile.OPTION_WSDL), wsdl.toString(),
                arg(GenerateConfigFile.OPTION_REPORT), report.toString(),
                arg(GenerateConfigFile.OPTION_TEMPLATE), template.toString()};
        GenerateConfigFile.main(args);
        assertTrue("File " + output + " does not exist", Files.exists(output));
        String contents = FileUtils.readFileToString(output.toFile(), "UTF-8");
        assertTrue(contents.contains("<analyze:description>custom template file</analyze:description>"));
    }

    @Test
    public void testCustomTestAssertionFile() throws IOException {
        String assertions = "assertions_file.xml";
        String[] args = {arg(Runner.OPTION_CONFIG_FILE),
                arg(Runner.OPTION_ROOT), toolsRoot.toString(),
                arg(GenerateConfigFile.OPTION_OUTPUT), output.toString(),
                arg(GenerateConfigFile.OPTION_WSDL), wsdl.toString(),
                arg(GenerateConfigFile.OPTION_REPORT), report.toString(),
                arg(GenerateConfigFile.OPTION_PROFILE_FILE), assertions};
        GenerateConfigFile.main(args);
        assertTrue("File " + output + " does not exist", Files.exists(output));
        String contents = FileUtils.readFileToString(output.toFile(), "UTF-8");
        assertTrue(contents.contains("<analyze:description>No description (custom file " + assertions +
                ")</analyze:description>"));
    }

    @Test
    public void testStylesheetArg() throws ParseException, IOException {
        String stylesheet = "custom_stylesheet.xsl";
        String[] args = {arg(Runner.OPTION_CONFIG_FILE),
                arg(Runner.OPTION_ROOT), toolsRoot.toString(),
                arg(GenerateConfigFile.OPTION_OUTPUT), output.toString(),
                arg(GenerateConfigFile.OPTION_WSDL), wsdl.toString(),
                arg(GenerateConfigFile.OPTION_REPORT), report.toString(),
                arg(GenerateConfigFile.OPTION_STYLESHEET), stylesheet};
        GenerateConfigFile.main(args);
        String contents = FileUtils.readFileToString(output.toFile(), "UTF-8");
        assertTrue(contents.contains("<analyze:addStyleSheet href=\"" + stylesheet + "\""));
    }

    String arg(String arg) {
        return "-" + arg;
    }

}
