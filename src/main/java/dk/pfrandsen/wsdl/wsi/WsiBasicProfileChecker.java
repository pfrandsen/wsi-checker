package dk.pfrandsen.wsdl.wsi;

import dk.pfrandsen.check.AnalysisInformationCollector;
import org.uddi4j.transport.TransportFactory;
import org.wsi.WSIException;
import org.wsi.test.analyzer.BasicProfileAnalyzer;
import org.wsi.util.WSIProperties;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WsiBasicProfileChecker {
    public static String ASSERTION_ID = "WS-I WSDL Basic Profile Validation";
    static private String VERBOSE = "%VERBOSE%";
    static private String REPORT_FILE = "%REPORT_FILE%";
    static private String NAMESPACE = "%NAMESPACE%";
    static private String BINDING = "%BINDING%";
    static private String WSDL_URI = "%WSDL_URI%";
    static private String ASSERTION_FILE = "%ASSERTION_FILE%";
    static private String STYLESHEET_FILE = "%STYLESHEET_FILE%";

    public static Path getDefaultWsiTestToolsRoot() {
        return Paths.get(System.getProperty("user.dir"), "WS-I", "wsi-test-tools");
    }

    public static Path getDefaultStylesheet(Path wsiTestToolsRoot) {
        String root = wsiTestToolsRoot.toAbsolutePath().toString();
        return Paths.get(root, "common", "xsl", "report.xsl");
    }

    public static Path getTestAssertionDocument(Path wsiTestToolsRoot, WsiProfile profile) {
        String root = wsiTestToolsRoot.toAbsolutePath().toString();
        return Paths.get(root, "common", "profiles", profile.getFilename());
    }

    public static WsiProfile getDefaultWsiProfile() {
        return WsiProfile.BASIC_PROFILE_11_SOAP_10;
    }

    public static Path getDefaultTestAssertionDocument(Path wsiTestToolsRoot) {
        return getTestAssertionDocument(wsiTestToolsRoot, getDefaultWsiProfile());
    }

    private static void appendLine(StringBuilder builder, String text) {
        builder.append(text).append(System.getProperty("line.separator"));
    }

    public static void generateBindingConfigFile(String template, Path config, Path reportFile, String url,
                                                 Path testToolsRoot, WsiProfile profile, String binding,
                                                 String namespace, boolean verbose) throws IOException {
        template = template.replaceAll(VERBOSE, verbose ? "true" : "false");
        template = template.replaceAll(REPORT_FILE, reportFile.toAbsolutePath().toString());
        template = template.replaceAll(NAMESPACE, namespace);
        template = template.replaceAll(BINDING, binding);
        template = template.replaceAll(WSDL_URI, url);
        template = template.replaceAll(ASSERTION_FILE,
                getTestAssertionDocument(testToolsRoot, profile).toString());
        template = template.replaceAll(STYLESHEET_FILE,
                getDefaultStylesheet(testToolsRoot).toString());
        BufferedWriter writer = Files.newBufferedWriter(config, StandardCharsets.UTF_8);
        writer.write(template);
        writer.flush();
        writer.close();
    }

    public static void generateBindingConfigFile(String template, Path config, Path reportFile, String url,
                                                 String binding, String namespace, boolean verbose) throws IOException {
        generateBindingConfigFile(template, config, reportFile, url, getDefaultWsiTestToolsRoot(), getDefaultWsiProfile(),
                binding, namespace, verbose);
    }

    public static void checkWsiBasicProfile(Path configFile, Path reportFile, Path wsiToolsRoot, WsiProfile profile,
                                            AnalysisInformationCollector collector) {

        String[] analyzerArgs = {"-config", configFile.toAbsolutePath().toString()};
        System.setProperty("wsi.home", wsiToolsRoot.toAbsolutePath().toString());
        // Set document builder factory class
        System.setProperty(WSIProperties.PROP_JAXP_DOCUMENT_FACTORY,
                WSIProperties.getProperty(WSIProperties.PROP_JAXP_DOCUMENT_FACTORY));
        // Set the system property for UDDI4J transport
        System.setProperty(TransportFactory.PROPERTY_NAME,
                WSIProperties.getProperty(TransportFactory.PROPERTY_NAME));

        BasicProfileAnalyzer analyzer = null;
        try {
            analyzer = new BasicProfileAnalyzer(analyzerArgs);
            // Have it process the conformance validation functions
            int statusCode = analyzer.validateConformance();
            System.out.println("Status code: " + statusCode);
            ReportParser parser = new ReportParser();
            try {
                parser.parse(reportFile);
                if (!parser.isPassed()) {
                    collector.addError(ASSERTION_ID, "Validation failed",
                            AnalysisInformationCollector.SEVERITY_LEVEL_CRITICAL,
                            "Profile: " + profile.getDescription());
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

    public enum WsiProfile {
        BASIC_PROFILE_11 {
            public String getFilename() {
                return "BasicProfile_1.1_TAD.xml";
            }

            public String getDescription() {
                return "BP11 Basic Profile version 1.1 Test Assertion Document (TAD), WG Draft";
            }

            public String getLocation() {
                return "http://ws-i.org/Profiles/BasicProfile-1.1-2004-06-11.html";
            }
        },

        BASIC_PROFILE_10 {
            public String getFilename() {
                return "BasicProfileTestAssertions.xml";
            }

            public String getDescription() {
                return "BP1 Basic Profile version 1.0 Test Assertion Document (TAD), Final";
            }

            public String getLocation() {
                return "http://www.ws-i.org/Profiles/BasicProfile-1.0-2004-04-16.html";
            }
        },

        BASIC_PROFILE_11_SOAP_10 {
            public String getFilename() {
                return "SSBP10_BP11_TAD.xml";
            }

            public String getDescription() {
                return "SSBP1+BP11 Simple SOAP Binding Profile version 1.0 + Ba1sic Profile version 1.0 " +
                        "Test Assertion Document (TAD), Final";
            }

            public String getLocation() {
                return "http://ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html, " +
                        "http://ws-i.org/Profiles/SimpleSoapBindingProfile-1.0-2004-08-24.html";
            }
        };

        public abstract String getFilename();

        public abstract String getDescription();

        public abstract String getLocation();
    }

}