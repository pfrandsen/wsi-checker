package dk.pfrandsen.wsdl.wsi;

import dk.pfrandsen.check.AnalysisInformationCollector;
import dk.pfrandsen.wsdl.Util;
import org.uddi4j.transport.TransportFactory;
import org.wsi.WSIException;
import org.wsi.test.analyzer.BasicProfileAnalyzer;
import org.wsi.util.WSIProperties;

import java.nio.file.Path;

public class WsiBasicProfileChecker {
    public static String ASSERTION_ID = "WS-I WSDL Basic Profile Validation";

    public static void checkWsiBasicProfile(Path configFile, Path reportFile, Path wsiToolsRoot,
                                            AnalysisInformationCollector collector) {

        String[] analyzerArgs = {"-config", configFile.toAbsolutePath().toString()};
        System.setProperty("wsi.home", wsiToolsRoot.toAbsolutePath().toString());
        // Set document builder factory class
        System.setProperty(WSIProperties.PROP_JAXP_DOCUMENT_FACTORY,
                WSIProperties.getProperty(WSIProperties.PROP_JAXP_DOCUMENT_FACTORY));
        // Set the system property for UDDI4J transport
        System.setProperty(TransportFactory.PROPERTY_NAME,
                WSIProperties.getProperty(TransportFactory.PROPERTY_NAME));

        BasicProfileAnalyzer analyzer;
        try {
            analyzer = new BasicProfileAnalyzer(analyzerArgs);
            // Have it process the conformance validation functions
            int statusCode = analyzer.validateConformance();
            collector.addInfo(ASSERTION_ID, "Analyzer status code " + statusCode,
                    AnalysisInformationCollector.SEVERITY_LEVEL_UNKNOWN);
            ReportParser parser = new ReportParser();
            try {
                parser.parse(reportFile);
                if (!parser.isPassed()) {
                    String description = "<not found>";
                    try {
                        description = Util.getDescriptionFromConfigFile(configFile);
                    } catch (Exception e) {
                        // ignore
                    }
                    collector.addError(ASSERTION_ID, "Validation failed",
                            AnalysisInformationCollector.SEVERITY_LEVEL_CRITICAL,
                            "Profile description: " + description);
                }
                for (String error : parser.getErrors()) {
                    collector.addError(ASSERTION_ID, error, AnalysisInformationCollector.SEVERITY_LEVEL_UNKNOWN);
                }
                for (String warning : parser.getWarnings()) {
                    collector.addError(ASSERTION_ID, warning, AnalysisInformationCollector.SEVERITY_LEVEL_UNKNOWN);
                }
            } catch (Exception e) {
                collector.addError(ASSERTION_ID, "Exception while parsing report file '" + reportFile + "'",
                        AnalysisInformationCollector.SEVERITY_LEVEL_CRITICAL, e.getMessage());
            }
        } catch (WSIException e) {
            collector.addError(ASSERTION_ID, e.getMessage(), AnalysisInformationCollector.SEVERITY_LEVEL_CRITICAL);
        }
    }

}