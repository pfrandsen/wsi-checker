package dk.pfrandsen.wsdl;

import com.ibm.wsdl.Constants;
import com.ibm.wsdl.factory.WSDLFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Util {

    public static Definition getWsdlDefinition(Path path) throws WSDLException {
        return getWsdlDefinition(path.toString());
    }

    public static Definition getWsdlDefinition(String uri) throws WSDLException {
        WSDLReader reader = WSDLFactoryImpl.newInstance().newWSDLReader();
        reader.setFeature(Constants.FEATURE_VERBOSE, false);
        reader.setFeature("javax.wsdl.importDocuments", true);
        return reader.readWSDL(uri);
    }

    public static List<Binding> getBindings(Definition definition) {
        List<Binding> result = new ArrayList<>();
        Map bindings = definition.getBindings();
        for (Object item : bindings.entrySet()) {
            Map.Entry entry = (Map.Entry) item;
            if (entry.getValue() instanceof Binding) {
                result.add((Binding) entry.getValue());
            }
        }
        return result;
    }

    public static List<Service> getServices(Definition definition) {
        List<Service> result = new ArrayList<>();
        Map services = definition.getServices();
        for (Object item : services.entrySet()) {
            Map.Entry entry = (Map.Entry) item;
            if (entry.getValue() instanceof Service) {
                result.add((Service) entry.getValue());
            }
        }
        return result;
    }

    public static String getWsiTemplate(String templateName) throws IOException {
        String separator = System.getProperty("line.separator");
        StringBuilder template = new StringBuilder();
        InputStream stream = Util.class.getResourceAsStream("/wsi/" + templateName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                template.append(line).append(separator);
            }
        }
        return template.toString();
    }

    public static String getReportLocationFromConfigFile(Path configFile) throws Exception {
        Element element = item(getConfigFileElementsByTagName(configFile, "reportFile"), 0);
        if (element != null) {
            return ("" + element.getAttribute("location")).trim();
        }
        return "";
    }

    public static String getDescriptionFromConfigFile(Path configFile) throws Exception {
        Element element = item(getConfigFileElementsByTagName(configFile, "description"), 0);
        if (element != null) {
            return ("" + element.getTextContent()).trim();
        }
        return "";
    }

    private static NodeList getConfigFileElementsByTagName(Path configFile, String tagName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);  // Important!!!
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(configFile.toFile());
        return document.getElementsByTagNameNS("http://www.ws-i.org/testing/2004/07/analyzerConfig/", tagName);
    }

    private static Element item(NodeList nodeList, int index) {
        if (index >= 0) {
            if (nodeList != null && nodeList.getLength() > index) {
                Node node = nodeList.item(index);
                if (node instanceof Element) {
                    return (Element) node;
                }
            }
        }
        return null;
    }

    public static String concatenate(List<String> values, String separator) {
        String sep = "";
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            builder.append(value).append(sep);
            sep = separator;
        }
        return builder.toString();
    }

    public static void mkDirs(Path folder) {
        if (!folder.toFile().exists()) {
            if (!folder.toFile().mkdirs()) {
                System.out.println("Could not create: " + folder.toString());
            }
        }
    }
}
