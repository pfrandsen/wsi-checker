package dk.pfrandsen.wsdl.wsi;

import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ReportParserTest {
    private ReportParser parser;

    private static String getLocation(String reportFile) throws URISyntaxException {
        return ReportParserTest.class.getResource(reportFile).toURI().getPath();
    }

    @Before
    public void setUp() {
        parser = new ReportParser();
    }

    @Test
    public void testReportOne() throws Exception {
        String reportFile = getLocation("/report/report_1.xml");
        parser.parse(Paths.get(reportFile));
        assertTrue(parser.isPassed());
    }

    @Test
    public void testReportTwo() throws Exception {
        String reportFile = getLocation("/report/report_2.xml");
        parser.parse(Paths.get(reportFile));
        assertFalse(parser.isPassed());
        assertEquals(1, parser.getErrors().size());
        assertEquals(0, parser.getWarnings().size());
    }

    @Test
    public void testWsiSampleReport() throws Exception {
        String reportFile = getLocation("/report/wsi_sample_report.xml");
        parser.parse(Paths.get(reportFile));
        assertTrue(parser.isPassed());
        assertEquals(0, parser.getErrors().size());
        assertEquals(5, parser.getWarnings().size());
    }

}
