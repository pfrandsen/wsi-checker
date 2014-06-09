package dk.pfrandsen.wsdl;

import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Test
    public void testGetReportLocation() throws Exception {
        String configFile = getLocation("/config/config.xml");
        String location = Util.getReportLocation(Paths.get(configFile));
        assertEquals("target/report.xml", location);
    }

    private static String getLocation(String configFile) throws URISyntaxException {
        return UtilTest.class.getResource(configFile).toURI().getPath();
    }

}
