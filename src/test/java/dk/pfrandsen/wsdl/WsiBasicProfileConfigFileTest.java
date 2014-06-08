package dk.pfrandsen.wsdl;

import dk.pfrandsen.wsdl.wsi.WsiBasicProfileChecker;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class WsiBasicProfileConfigFileTest {

    private static String getLocation(String wsdlFile) throws URISyntaxException {
        return WsiBasicProfileConfigFileTest.class.getResource(wsdlFile).toURI().getPath();
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
            String template = Util.getWsiTemplate("wsi_binding_config_template.xml");
            WsiBasicProfileChecker.generateBindingConfigFile(template, config, report, url, binding, namespace, true);
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

    @Test
    public void testBindingTemplate() throws Exception {
        String text = "<analyze:wsdlElement type=\"binding\" namespace=\"%NAMESPACE%\">%BINDING%</analyze:wsdlElement>";
        String template = Util.getWsiTemplate("wsi_binding_config_template.xml");
        assertTrue("Template should contain wsdlElement with type biinding", template.contains(text));
    }

}
