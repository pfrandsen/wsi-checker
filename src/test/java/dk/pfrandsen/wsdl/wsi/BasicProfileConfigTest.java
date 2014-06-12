package dk.pfrandsen.wsdl.wsi;

import dk.pfrandsen.wsdl.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class BasicProfileConfigTest {
    private Path outputPath;

    @Before
    public void setUp() {
        outputPath = Paths.get("target", "config_file");
    }

    @After
    public void cleanUp() {
        if (outputPath.toString().startsWith("target")) {
            Helper.deleteFolder(outputPath);
        }
    }

    @Test
    public void testStylesheetDefaultPath() {
        Path root = Paths.get("target", "tool_root");
        Path relPath = Paths.get("common", "xsl", "report.xsl");
        String expected = Paths.get(System.getProperty("user.dir")).resolve(root).resolve(relPath).toUri().toString();

        Path stylesheet = BasicProfileConfig.appendDefaultStylesheet(root);
        assertEquals(expected, stylesheet.toUri().toString());
    }

    @Test
    public void testStylesheetPath() {
        Path root = Paths.get("target", "tool_root");
        String stylesheetName = "stylesheet.xsl";
        Path relPath = Paths.get("common", "xsl", stylesheetName);
        String expected = Paths.get(System.getProperty("user.dir")).resolve(root).resolve(relPath).toUri().toString();

        Path stylesheet = BasicProfileConfig.appendStylesheet(root, stylesheetName);
        assertEquals(expected, stylesheet.toUri().toString());
    }

    @Test
    public void testProfilePath() {
        Path root = Paths.get("target", "tool_root");
        String testAssertionDocument = "TAD.xml";
        Path relPath = Paths.get("common", "profiles", testAssertionDocument);
        String expected = Paths.get(System.getProperty("user.dir")).resolve(root).resolve(relPath).toUri().toString();

        Path stylesheet = BasicProfileConfig.appendProfile(root, testAssertionDocument);
        assertEquals(expected, stylesheet.toUri().toString());
    }

    @Test
    public void testProfileFilename() {
        assertEquals("BasicProfile_1.1_TAD.xml", BasicProfileConfig.profileFilename("BASIC_PROFILE_11"));
    }

    @Test
    public void testTemplateName() {
        List<String> templates = BasicProfileConfig.templateFilename(); // .templates();
        assertTrue("Expected to find template 'wsi_binding_config_template.xml'",
                templates.contains("wsi_binding_config_template.xml"));
        assertTrue("Expected to find template 'wsi_service_config_template.xml'",
                templates.contains("wsi_service_config_template.xml"));
    }

    @Test
    public void testInvalidTemplateName() throws IOException {
        Path config = outputPath.resolve("config.xml");
        boolean success = BasicProfileConfig.generateBindingConfigFile("invalid-template", config,
                outputPath.resolve("report.xml"), "url", Paths.get("tool-root"), Paths.get("profile.xml"), "binding",
                "namespace", true, Paths.get("stylesheet.xsl"), "description");
        assertFalse(success);
        assertFalse(outputPath.toFile().exists());
    }

    @Test
    public void testValidTemplateName() throws IOException {
        Path config = outputPath.resolve("config.xml");
        boolean success = BasicProfileConfig.generateBindingConfigFile("wsi_binding_config_template.xml", config,
                outputPath.resolve("report.xml"), "url", Paths.get("tool-root"), Paths.get("profile.xml"), "binding",
                "namespace", true, Paths.get("stylesheet.xsl"), "description");
        assertTrue(success);
        assertTrue(outputPath.toFile().exists());
        assertTrue(config.toFile().exists());
        // TODO: check content of file
    }

    @Test
    public void testBindingTemplate() throws Exception {
        String text = "<analyze:wsdlElement type=\"binding\" namespace=\"%NAMESPACE%\">%BINDING%</analyze:wsdlElement>";
        String template = BasicProfileConfig.getTemplateContent("wsi_binding_config_template.xml");
        assertTrue("Template should contain wsdlElement with type binding", template.contains(text));
    }

    private static String getLocation(String wsdlFile) throws URISyntaxException {
        return BasicProfileConfigTest.class.getResource(wsdlFile).toURI().getPath();
    }

    private static String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    @Test
    public void testValid() throws Exception {
        Path config = Files.createTempFile("config-", ".xml");
        Path report = Files.createTempFile("report-", ".xml");
        String namespace = "http://service.schemas/domain/service/v1";
        String binding = "EntityBinding";
        try {
            String url = getLocation("/wsdl/wsdl_1.wsdl");
            // String template = Util.getWsiTemplate("wsi_binding_config_template.xml");
            String templateFilename = "wsi_binding_config_template.xml";
            BasicProfileConfig.generateBindingConfigFile(templateFilename, config, report, url, Paths.get("tools-root"),
                    Paths.get("profile.xml"), binding, namespace, true, Paths.get("styles.xsl"), "description");
            // WsiBasicProfileChecker.generateBindingConfigFile(template, config, report, url, binding, namespace, true);
            String configContent = readFile(config);
            assertTrue("Verbose should be true", configContent.contains("<analyze:verbose>true</analyze:verbose>"));
            assertTrue("Report file location should be '" + report.toString() + "'",
                    configContent.contains("location=\"" + report.toString() + "\""));
            assertTrue("Binding should be '" + binding + "'",
                    configContent.contains(">" + binding + "</analyze:wsdlElement>"));
            assertTrue("Binding namespace should be '" + namespace + "'",
                    configContent.contains("namespace=\"" + namespace + "\""));
            assertTrue("WSDL URI should be '" + url + "'",
                    configContent.contains("<analyze:wsdlURI>" + url + "</analyze:wsdlURI>"));
        } finally {
            Files.delete(config);
            Files.delete(report);
        }
    }


}
