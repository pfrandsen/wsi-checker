package dk.pfrandsen.wsdl;

import com.ibm.wsdl.Constants;
import com.ibm.wsdl.factory.WSDLFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Util {

    public static Definition getWsdlDefinition(String uri) throws WSDLException {
        WSDLReader reader = WSDLFactoryImpl.newInstance().newWSDLReader();
        reader.setFeature(Constants.FEATURE_VERBOSE, false);
        reader.setFeature("javax.wsdl.importDocuments", true);
        return reader.readWSDL(uri);
    }

    public static List<Binding> getBindings(Definition definition) {
        List<Binding> result = new ArrayList<Binding>();
        Map bindings = definition.getBindings();
        Iterator iterator = bindings.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getValue() instanceof Binding) {
                result.add((Binding) entry.getValue());
            }
        }
        return result;
    }

    public static List<Service> getServices(Definition definition) {
        List<Service> result = new ArrayList<Service>();
        Map services = definition.getServices();
        Iterator iterator = services.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                template.append(line).append(separator);
            }
        } finally {
            reader.close();
        }
        return template.toString();
    }

    public static String getReportLocation(Path configFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);  // Important!!!
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(configFile.toFile());
        NodeList reportFileNodes = document.getElementsByTagNameNS("http://www.ws-i.org/testing/2004/07/analyzerConfig/", "reportFile");
        if (reportFileNodes != null && reportFileNodes.getLength() > 0) {
            Node node = reportFileNodes.item(0);
            if (node instanceof Element) {
                System.out.println(((Element) node).getAttribute("location"));
                return ((Element) node).getAttribute("location");
            }
        }
        return "";
    }

}
