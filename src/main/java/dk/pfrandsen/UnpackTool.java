package dk.pfrandsen;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnpackTool extends CommandLineTool {
    static private String OPTION_ROOT = "root";
    static private String OPTION_ROOT_SHORT = "r";
    static private String USAGE = "Usage: java -jar <jar-file> " + arg(Runner.OPTION_UNPACK) + " " + arg(OPTION_ROOT)
            + " <folder> ";

    @Override
    protected String getUsageString() {
        return USAGE;
    }

    @Override
    protected String getToolDescription() {
        return "Tool for extraction WS-I tool files (schemas etc. needed during WSDL analysis).";
    }

    protected Options getCommandlineOptions() {
        Options options = new Options();
        Option help = new Option(Runner.OPTION_HELP, USAGE);

        Option root = new Option(OPTION_ROOT_SHORT, OPTION_ROOT, true,
                "root folder to place WS-I tool files in (will be created if it does not exist)");
        root.setArgName("folder");
        options.addOption(help);
        options.addOption(new Option(Runner.OPTION_UNPACK, "option to select this tool"));
        options.addOption(root);
        return options;
    }

    @Override
    protected boolean run(CommandLine cmd) {
        Path rootFolder = Paths.get(cmd.getOptionValue(OPTION_ROOT));
        return extractTool(rootFolder);
    }

    public boolean extractTool(Path rootFolder) {
        byte[] buffer = new byte[2 * 1024];
        boolean retVal = true;
        InputStream stream = UnpackTool.class.getResourceAsStream("/wsi/tool/common.zip");
        ZipInputStream zipStream = new ZipInputStream(stream);
        try {
            ZipEntry entry = zipStream.getNextEntry();
            while (entry != null) {
                String fileName = entry.getName();
                if (entry.isDirectory()) {
                    Path folder = rootFolder.resolve(fileName);
                    folder.toFile().mkdirs();
                } else {
                    File f = rootFolder.resolve(fileName).toFile();
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

    public static void main(String[] args) {
        UnpackTool tool = new UnpackTool();
        CommandLine cmd;
        try {
            cmd = tool.parseCommandLine(args);
        } catch (ParseException e) {
            tool.printHelp();
            return;
        }
        boolean success = tool.run(cmd);
        System.out.println("Tool unpack completed with status: " + (success ? "SUCCESS" : "FAILURE"));
    }

}