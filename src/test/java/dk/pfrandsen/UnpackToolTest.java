package dk.pfrandsen;

import dk.pfrandsen.wsdl.wsi.Helper;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UnpackToolTest {
    private Path outputPath;

    @Before
    public void setUp() {
        outputPath = Paths.get("target", "unpack");
    }

    @After
    public void cleanUp() {
        if (outputPath.toString().startsWith("target")) {
            Helper.deleteFolder(outputPath);
        }
    }

    private void checkExtract(Path path) throws IOException {
        Path profile = Paths.get(path.toString(), "common", "profiles", "SSBP10_BP11_TAD.xml");
        Path schema = Paths.get(path.toString(), "common", "schemas", "wsdl11.xsd");
        Path styleSheet = Paths.get(path.toString(), "common", "xsl", "report.xsl");
        assertTrue("File " + profile + " does not exist", Files.exists(profile));
        assertTrue("File " + schema + " does not exist", Files.exists(schema));
        assertTrue("File " + styleSheet + " does not exist", Files.exists(styleSheet));
        assertEquals("7f8eb4aee9199b8694f04e61e0cffbf25e8f1c23", sha1(profile));
        assertEquals("0ab4d31ae6b05b34bbea6c8ccbe2913486fa9bf6", sha1(schema));
        assertEquals("f446a57778cc82d080ef61fbdd1d9c7d3408a066", sha1(styleSheet));
    }

    @Test
    public void testUnpackRun() throws Exception {
        String[] args = {"-unpackTool", "-root", outputPath.toString()};
        UnpackTool tool = new UnpackTool();
        assertTrue("Running the unpack tool .run() returned false", tool.run(tool.parseCommandLine(args)));
        checkExtract(outputPath);
    }

    @Test
    public void testUnpackMain() throws Exception {
        String[] args = {"-unpackTool", "-root", outputPath.toString()};
        UnpackTool.main(args);
        checkExtract(outputPath);
    }

    @Test
    public void testExtractTool() throws Exception {
        UnpackTool tool = new UnpackTool();
        assertTrue("Running the unpack tool extractTool returned false", tool.extractTool(outputPath));
        checkExtract(outputPath);
    }

    private String sha1(Path file) throws IOException {
        return DigestUtils.sha1Hex(new FileInputStream(file.toFile()));
    }

}
