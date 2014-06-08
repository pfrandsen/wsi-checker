package dk.pfrandsen.wsdl;

import dk.pfrandsen.check.AnalysisInformationCollector;
import dk.pfrandsen.wsdl.wsi.WsiBasicProfileChecker;
import org.junit.Before;
import org.junit.Test;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WsiBasicProfileCheckerTest {
    private AnalysisInformationCollector collector;

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
            String template = Util.getWsiTemplate("wsi_binding_config_template.xml");
            Path toolsRoot = WsiBasicProfileChecker.getDefaultWsiTestToolsRoot();
            WsiBasicProfileChecker.WsiProfile profile = WsiBasicProfileChecker.getDefaultWsiProfile();
            WsiBasicProfileChecker.generateBindingConfigFile(template, config, report, url, toolsRoot, profile,
                    binding, namespace, true);
            WsiBasicProfileChecker.checkWsiBasicProfile(config, report, toolsRoot, profile, collector);
            assertEquals(0, collector.getErrorCount());
            assertEquals(0, collector.getWarningCount());
            assertEquals(0, collector.getInfoCount());
        } finally {
            Files.delete(config);
            Files.delete(report);
        }
    }

    @Test
    public void testWsdlOne() throws Exception {
        checkBinding("/wsdl/wsdl_1.wsdl", 0, 0, 0, 0);
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
            String template = Util.getWsiTemplate("wsi_binding_config_template.xml");
            Path toolsRoot = WsiBasicProfileChecker.getDefaultWsiTestToolsRoot();
            WsiBasicProfileChecker.WsiProfile profile = WsiBasicProfileChecker.getDefaultWsiProfile();
            WsiBasicProfileChecker.generateBindingConfigFile(template, config, report, url, toolsRoot, profile,
                    binding, namespace, true);
            WsiBasicProfileChecker.checkWsiBasicProfile(config, report, toolsRoot, profile, collector);
            assertEquals(error, collector.getErrorCount());
            assertEquals(warning, collector.getWarningCount());
            assertEquals(info, collector.getInfoCount());
        } finally {
            Files.delete(config);
            Files.delete(report);
        }
    }

}
