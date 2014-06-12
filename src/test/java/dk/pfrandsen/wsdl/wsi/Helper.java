package dk.pfrandsen.wsdl.wsi;

import java.io.File;
import java.nio.file.Path;

public class Helper {

    public static void deleteFolder(Path folder) {
        if (folder.toFile().exists() && folder.toFile().isDirectory()) {
            System.out.println("deleting " + folder.toAbsolutePath());
            deleteRecursive(folder.toFile());
        }
    }

    static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteRecursive(f);
            }
            file.delete();
        } else {
            file.delete();
        }
    }

}
