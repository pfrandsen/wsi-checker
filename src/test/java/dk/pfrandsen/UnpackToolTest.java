package dk.pfrandsen;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UnpackToolTest {

    @Test
    public void testUnpack() throws Exception {
        Path path = Paths.get("target", "unpack");
        String[] args = {"-unpackTool", "-root", path.toString()};

        deleteFolder(path);
        UnpackTool.main(args);
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

    private void deleteFolder(Path folder) {
        if (folder.toFile().exists() && folder.toFile().isDirectory()) {
            System.out.println("deleting " + folder.toAbsolutePath());
            deleteRecursive(folder.toFile());
        }
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteRecursive(f);
            }
            // System.out.println("deleting " + file.getAbsolutePath());
            file.delete();
        } else {
            // System.out.println("deleting " + file.getAbsolutePath());
            file.delete();
        }
    }

    private String sha1(Path file) throws IOException {
        return DigestUtils.sha1Hex(new FileInputStream(file.toFile()));
    }

}
