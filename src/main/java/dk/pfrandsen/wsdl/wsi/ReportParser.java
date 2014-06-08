package dk.pfrandsen.wsdl.wsi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

public class ReportParser {

    private boolean passed = false;
    private List<String> errors = new ArrayList<String>();
    private List<String> warnings = new ArrayList<String>();

    public boolean isPassed() {
        return passed;
    }

    private void setPassed(String summaryAttributeValue) {
        this.passed = "passed".compareToIgnoreCase(summaryAttributeValue) == 0;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    private void registerError(String assertionId, String artifactType, String entryType, String entryRef) {
        Formatter formatter = new Formatter();
        formatter.format("Assertion %s (artifact: '%s', entry: '%s', ref: '%s')",
                assertionId, artifactType, entryType, entryRef);
        errors.add(formatter.toString());
    }

    private void registerWarning(String assertionId, String artifactType, String entryType, String entryRef) {
        Formatter formatter = new Formatter();
        formatter.format("Assertion %s (artifact: '%s', entry: '%s', ref: '%s')",
                assertionId, artifactType, entryType, entryRef);
        warnings.add(formatter.toString());
    }

    // Extract result attribute of first element in the list and use it to set the passed status
    private void summary(NodeList summary) {
        if (summary != null && summary.getLength() > 0) {
            Node node = summary.item(0);
            if (node instanceof Element) {
                setPassed("" + ((Element) node).getAttribute("result"));
            }
        }
    }

    public void parse(Path reportFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(reportFile.toFile());
        summary(document.getElementsByTagName("summary"));
        NodeList artifactList = document.getElementsByTagName("artifact");
        for (int idx = 0; idx < artifactList.getLength(); idx++) {
            // each node in the artifact list has the following structure
            // <artifact type="...">
            //   <entry type="..." referenceID="...">
            //     <assertionResult id="..." result="...">
            //     ...
            //   <entry type="..." referenceID="...">
            //     <assertionResult id="..." result="...">
            //     ...
            //   ...
            Node node = artifactList.item(idx);
            if (node instanceof Element) {
                Element element = (Element) node;
                String artifactType = "" + element.getAttribute("type");
                NodeList entries = element.getElementsByTagName("entry");
                for (int entryIdx = 0; entryIdx < entries.getLength(); entryIdx++) {
                    if (entries.item(entryIdx) instanceof Element) {
                        Element entry = (Element) entries.item(entryIdx);
                        String entryType = "" + entry.getAttribute("type");
                        String entryRef = "" + entry.getAttribute("referenceID");
                        NodeList assertionResults = entry.getElementsByTagName("assertionResult");
                        for (int assertionIdx = 0; assertionIdx < assertionResults.getLength(); assertionIdx++) {
                            if (assertionResults.item(assertionIdx) instanceof Element) {
                                Element assertionResult = (Element) assertionResults.item(assertionIdx);
                                String resultAttribute = "" + assertionResult.getAttribute("result");
                                String assertionId = "" + assertionResult.getAttribute("id");
                                if (resultAttribute.equals("failed")) {
                                    registerError(assertionId, artifactType, entryType, entryRef);
                                } else if (resultAttribute.equals("warning")) {
                                    registerWarning(assertionId, artifactType, entryType, entryRef);
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
