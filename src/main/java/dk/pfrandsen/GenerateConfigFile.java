package dk.pfrandsen;

import dk.pfrandsen.wsdl.*;
import dk.pfrandsen.wsdl.wsi.WsiBasicProfileChecker;
import org.apache.commons.cli.*;
import org.apache.commons.cli.CommandLine;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// TODO: Add options for specifying WS-I tools root and location of report stylesheet file

public class GenerateConfigFile {
    static private String OPTION_HELP = "help";
    static private String OPTION_WSDL = "wsdl";
    static private String OPTION_REPORT = "report";
    static private String OPTION_OUTPUT = "output";
    static private String OPTION_BINDING = "binding";
    static private String USAGE = "Usage: java -jar <jar-file> -" + Runner.OPTION_CONFIG_FILE + " -" + OPTION_WSDL
            + " <file> -" + OPTION_REPORT + " <file> -" + OPTION_OUTPUT + " <file> [-" + OPTION_BINDING + " <index>]";

    public static void printHelp() {
        System.out.println();
        System.out.println("Tool for generating WS-I analyzer configuration file.");
        System.out.println();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(USAGE, getCommandlineOptions());
    }

    private static Options getCommandlineOptions() {
        Options options = new Options();
        Option help = new Option(OPTION_HELP, USAGE);

        Option wsdl = OptionBuilder.withArgName("file")
                .hasArg()
                .isRequired()
                .withDescription("location of wsdl file")
                .create(OPTION_WSDL);
        Option report = OptionBuilder.withArgName("file")
                .hasArg()
                .isRequired()
                .withDescription("location of report file")
                .create(OPTION_REPORT);
        Option output = OptionBuilder.withArgName("file")
                .hasArg()
                .isRequired()
                .withDescription("generated config file")
                .create(OPTION_OUTPUT);
        Option binding = OptionBuilder.withArgName("index")
                .hasArg()
                .withDescription("index of binding 1..number of bindings in wsdl (default 1) to generate config for")
                .create(OPTION_BINDING);

        options.addOption(help);
        options.addOption(new Option(Runner.OPTION_CONFIG_FILE, "option to select this tool"));
        options.addOption(wsdl);
        options.addOption(report);
        options.addOption(output);
        options.addOption(binding);
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
        if (cmd.hasOption(OPTION_HELP)) {
            printHelp();
            return;
        }

        String wsdl = Paths.get(cmd.getOptionValue(OPTION_WSDL)).toAbsolutePath().toString();
        Path output = Paths.get(cmd.getOptionValue(OPTION_OUTPUT)).toAbsolutePath();
        Path report = Paths.get(cmd.getOptionValue(OPTION_REPORT)).toAbsolutePath();
        int bindingIndex = 0;
        if (cmd.hasOption(OPTION_BINDING)) {
            try {
                bindingIndex = Integer.parseInt(cmd.getOptionValue(OPTION_BINDING)) - 1;
                if (bindingIndex < 0) {
                    System.err.println("Binding index must be 1 or greater - was " + cmd.getOptionValue(OPTION_BINDING));
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Binding index must be integer - was " + cmd.getOptionValue(OPTION_BINDING));
            }
        }

        try {
            Definition definition = Util.getWsdlDefinition(wsdl);
            List<Binding> bindings = Util.getBindings(definition);
            if (bindings.size() == 0) {
                System.err.println("No bindings found in " + wsdl);
                return;
            }
            if (bindingIndex >= bindings.size()) {
                System.err.println("Binding index too big, max index is " + bindings.size());
                return;
            }
            String binding = bindings.get(bindingIndex).getQName().getLocalPart();
            String namespace = bindings.get(bindingIndex).getQName().getNamespaceURI();
            String template = Util.getWsiTemplate("wsi_binding_config_template.xml");
            Path toolsRoot = WsiBasicProfileChecker.getDefaultWsiTestToolsRoot();
            WsiBasicProfileChecker.WsiProfile profile = WsiBasicProfileChecker.getDefaultWsiProfile();
            WsiBasicProfileChecker.generateBindingConfigFile(template, output, report, wsdl, toolsRoot, profile,
                    binding, namespace, true);
        } catch (WSDLException e) {
            System.err.println("Exception while parsing wsdl - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO exception" + e.getMessage());
        }
    }
}
