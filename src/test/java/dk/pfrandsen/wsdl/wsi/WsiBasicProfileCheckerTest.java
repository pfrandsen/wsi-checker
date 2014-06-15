package dk.pfrandsen.wsdl.wsi;

import dk.pfrandsen.check.AnalysisInformationCollector;
import dk.pfrandsen.wsdl.Util;
import org.junit.Before;
import org.junit.Test;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WsiBasicProfileCheckerTest {
    private AnalysisInformationCollector collector;
    private Path toolsRoot = Paths.get("WS-I", "wsi-test-tools");
    private WsiProfile profile = WsiProfile.BASIC_PROFILE_11_SOAP_10;

    private static String getLocation(String wsdlFile) throws URISyntaxException {
        return WsiBasicProfileCheckerTest.class.getResource(wsdlFile).toURI().getPath();
    }

    @Before
    public void setUp() {
        collector = new AnalysisInformationCollector();
    }

    @Test
    public void testValid() throws Exception {
        Path config = Files.createTempFile("config-", ".xml");
        Path report = Files.createTempFile("report-", ".xml");
        try {
            String url = getLocation("/wsdl/wsdl_1.wsdl");
            Definition definition = Util.getWsdlDefinition(url);
            List<Binding> bindings = Util.getBindings(definition);
            assertTrue(bindings.size() > 0);
            String binding = bindings.get(0).getQName().getLocalPart();
            String namespace = bindings.get(0).getQName().getNamespaceURI();
            String templateFilename = "wsi_binding_config_template.xml";
            Path profile = BasicProfileConfig.appendProfile(toolsRoot,
                    WsiProfile.BASIC_PROFILE_11_SOAP_10.getAssertionFilename());
            Path stylesheet = BasicProfileConfig.appendDefaultStylesheet(toolsRoot);
            BasicProfileConfig.generateBindingConfigFile(templateFilename, config, report, url, profile,
                    binding, namespace, true, stylesheet, "description");
            WsiBasicProfileChecker.checkWsiBasicProfile(config, report, toolsRoot, collector);
            assertEquals(0, collector.errorCount());
            assertEquals(0, collector.warningCount());
            assertEquals(1, collector.infoCount());
        } finally {
            Files.delete(config);
            Files.delete(report);
        }
    }

    @Test
    public void testWsdlOne() throws Exception {
        checkBinding("/wsdl/wsdl_1.wsdl", 0, 0, 0, 1);
    }

    private void checkBinding(String wsdlRelPath, int bindingIndex, int error, int warning, int info) throws Exception {
        Path config = Files.createTempFile("config-", ".xml");
        Path report = Files.createTempFile("report-", ".xml");
        try {
            String url = getLocation(wsdlRelPath);
            Definition definition = Util.getWsdlDefinition(url);
            List<Binding> bindings = Util.getBindings(definition);
            assertTrue(bindings.size() > bindingIndex);
            String binding = bindings.get(bindingIndex).getQName().getLocalPart();
            String namespace = bindings.get(bindingIndex).getQName().getNamespaceURI();
            String templateFilename = "wsi_binding_config_template.xml";
            Path profile = BasicProfileConfig.appendProfile(toolsRoot,
                    WsiProfile.BASIC_PROFILE_11_SOAP_10.getAssertionFilename());
            Path stylesheet = BasicProfileConfig.appendDefaultStylesheet(toolsRoot);
            BasicProfileConfig.generateBindingConfigFile(templateFilename, config, report, url, profile,
                    binding, namespace, true, stylesheet, "description");
            WsiBasicProfileChecker.checkWsiBasicProfile(config, report, toolsRoot, collector);
            assertEquals(error, collector.errorCount());
            assertEquals(warning, collector.warningCount());
            assertEquals(info, collector.infoCount());
        } finally {
            Files.delete(config);
            Files.delete(report);
        }
    }

}
