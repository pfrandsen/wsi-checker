package dk.pfrandsen;

import org.apache.commons.cli.*;

public class Runner {
    public static String OPTION_ANALYZE = "analyze";
    public static String OPTION_CONFIG_FILE = "generateConfig";
    public static String OPTION_UNPACK = "unpackTool";
    static private String OPTION_HELP = "help";
    static private String USAGE = "to get help for tool use: java -jar <jar-file> -" + OPTION_HELP + " -<tool>";

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        options.addOption(new Option(OPTION_HELP, "Print this message"));
        options.addOption(new Option(OPTION_ANALYZE, "Get help on WSDL analyzer tool"));
        options.addOption(new Option(OPTION_CONFIG_FILE, "Get help on configuration file generator tool"));
        options.addOption(new Option(OPTION_UNPACK, "Get help on file unpack tool"));
        formatter.printHelp(USAGE, options);
    }

    private static Options getCommandlineOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.create(OPTION_HELP));
        options.addOption(OptionBuilder.create(OPTION_ANALYZE));
        options.addOption(OptionBuilder.create(OPTION_CONFIG_FILE));
        options.addOption(OptionBuilder.create(OPTION_UNPACK));
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

        if (cmd.hasOption(OPTION_ANALYZE)) {
            if (cmd.hasOption(OPTION_HELP)) {
                AnalyzeWsdl.printHelp();
            } else {
                System.out.println("Running analyzer");
            }
        } else if (cmd.hasOption(OPTION_CONFIG_FILE)) {
            if (cmd.hasOption(OPTION_HELP)) {
                GenerateConfigFile.printHelp();
            } else {
                System.out.println("Creating configuration file");
            }
        } else if (cmd.hasOption(OPTION_UNPACK)) {
            if (cmd.hasOption(OPTION_HELP)) {
                System.out.println("Help for tool file extractor");
            } else {
                System.out.println("Extracting tool files");
            }
        } else {
            System.out.println("Required option -" + OPTION_ANALYZE + " or -" + OPTION_CONFIG_FILE + " or -"
                    + OPTION_UNPACK + " not provided");
            printHelp();
        }
    }

}