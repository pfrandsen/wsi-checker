package dk.pfrandsen;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnpackTool {
    static private String OPTION_ROOT = "root";
    static private String USAGE = "Usage: java -jar <jar-file> -" + Runner.OPTION_UNPACK + " -" + OPTION_ROOT
            + " <folder> ";

    public static void printHelp() {
        System.out.println();
        System.out.println("Tool for extraction WS-I tool files (schemas etc. needed during WSDL analysis).");
        System.out.println();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(USAGE, getCommandlineOptions());
    }

    private static Options getCommandlineOptions() {
        Options options = new Options();
        Option help = new Option(Runner.OPTION_HELP, USAGE);

        Option root = OptionBuilder.withArgName("folder")
                .hasArg()
                .isRequired()
                .withDescription("root folder to place WS-I tool files in (will be created if it does not exist)")
                .create(OPTION_ROOT);
        options.addOption(help);
        options.addOption(new Option(Runner.OPTION_UNPACK, "option to select this tool"));
        options.addOption(root);
        return options;
    }

    public static void main(String[] args) {
        CommandLineParser parser = new GnuParser(); // replace with BasicParser when Apache commons-cli is released
        CommandLine cmd;
        try {
            Options options = getCommandlineOptions();
            // parse the command line arguments
            cmd = parser.parse(options, args);
        } catch (ParseException exp) {
            // oops, something went wrong
            printHelp();
            return;
        }
        if (cmd.hasOption(Runner.OPTION_HELP)) {
            printHelp();
            return;
        }

        Path rootFolder = Paths.get(cmd.getOptionValue(OPTION_ROOT)).toAbsolutePath();
        boolean success = extractTool(rootFolder);
        System.out.println("Tool unpack completed with status: " + (success ? "SUCCESS" : "FAILURE"));
    }

    private static boolean extractTool(Path rootFolder) {
        byte[] buffer = new byte[2*1024];
        boolean retVal = true;
        InputStream stream = UnpackTool.class.getResourceAsStream("/wsi/tool/common.zip");
        ZipInputStream zipStream = new ZipInputStream(stream);
        try {
            ZipEntry entry = zipStream.getNextEntry();
            while (entry != null) {
                String fileName = entry.getName();
                if (entry.isDirectory()) {
                    System.out.println("Directory " + entry.getName());
                    (new File(rootFolder.toString() + File.separator + entry.getName())).mkdirs();
                } else {
                    System.out.println("File " + fileName);
                    File f = new File(rootFolder.toString() + File.separator + fileName);
                    new File(f.getParent()).mkdirs(); // should already be created (directory entry should come
                    // before file entries)
                    FileOutputStream outputStream = new FileOutputStream(f);
                    int length;
                    while ((length = zipStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.close();
                }
                entry = zipStream.getNextEntry();
            }
            zipStream.close();
        } catch (IOException e) {
            retVal = false;
            System.out.println("Exception " + e.getMessage());
        }
        return retVal;
    }

}