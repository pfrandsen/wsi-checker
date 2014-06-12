package dk.pfrandsen;

import dk.pfrandsen.wsdl.*;
import dk.pfrandsen.wsdl.wsi.BasicProfileConfig;
import dk.pfrandsen.wsdl.wsi.WsiProfile;
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
// TODO: Add option for absolute/relative paths

public class GenerateConfigFile extends CommandLineTool {
    public static String OPTION_ROOT = "root";
    private static String OPTION_ROOT_SHORT = "r";
    public static String OPTION_WSDL = "wsdl";
    public static String OPTION_WSDL_SHORT = "w";
    public static String OPTION_REPORT = "report";
    public static String OPTION_REPORT_SHORT = "r";
    public static String OPTION_OUTPUT = "output";
    public static String OPTION_OUTPUT_SHORT = "o";
    public static String OPTION_BINDING = "binding";
    public static String OPTION_BINDING_SHORT = "b";
    public static String OPTION_STYLESHEET = "binding";
    public static String OPTION_STYLESHEET_SHORT = "b";
    private static String OPTION_PROFILE_NAME = "profileName";
    private static String OPTION_PROFILE_FILE = "profileFile";
    private static String OPTION_TEMPLATE = "template";
    public static String USAGE = "Usage: java -jar <jar-file> " + arg(Runner.OPTION_CONFIG_FILE) + " "
            + arg(OPTION_ROOT) + " <folder> " + arg(OPTION_WSDL)
            + " <file> " + arg(OPTION_REPORT) + " <file> " + arg(OPTION_OUTPUT) + " <file> [" + arg(OPTION_BINDING)
            + " <index>" + arg(OPTION_STYLESHEET) + " <file> " + arg(OPTION_PROFILE_NAME) + "<profile-name>"
            + arg(OPTION_PROFILE_FILE) + " <file>" + arg(OPTION_TEMPLATE) + " <file>]";

    @Override
    protected String getUsageString() {
        return USAGE;
    }

    @Override
    protected String getToolDescription() {
        return "Tool for generating WS-I analyzer configuration file.";
    }

    private String getDefaultProfileName() {
        return WsiProfile.BASIC_PROFILE_11_SOAP_10.name();
    }

    private String getBindingTemplateFilename() {
        return "wsi_binding_config_template.xml";
    }

    protected Options getCommandlineOptions() {
        Options options = new Options();
        Option help = new Option(Runner.OPTION_HELP, USAGE);
        Option root = new Option(OPTION_ROOT_SHORT, OPTION_ROOT, true,
                "WS-I tools root folder (containing schemas, stylesheets etc.)");
        root.setArgName("folder");
        Option wsdl = new Option(OPTION_WSDL_SHORT, OPTION_WSDL, true,
                "location of wsdl file");
        wsdl.setArgName("file");
        Option report = new Option(OPTION_REPORT_SHORT, OPTION_REPORT, true,
                "location of report file");
        report.setArgName("file");
        Option output = new Option(OPTION_OUTPUT_SHORT, OPTION_OUTPUT, true,
                "location of generated config file");
        report.setArgName("file");
        Option binding = new Option(OPTION_BINDING_SHORT, OPTION_BINDING, false,
                "index of binding 1..number of bindings in wsdl (default 1) to generate config for");
        report.setArgName("index");
        Option stylesheet = new Option(OPTION_STYLESHEET_SHORT, OPTION_STYLESHEET, false,
                "location of report stylesheet (default: standard location of stylesheet relative to tools root)");
        stylesheet.setArgName("file");
        Option template = new Option(OPTION_TEMPLATE, false,
                "location of generated config file");
        template.setArgName("file");
        Option profileName = new Option(OPTION_PROFILE_NAME, false,
                "name of profile file to use; default is '" + getDefaultProfileName() + "'");
        profileName.setArgName("profileName");
        Option profileFile = new Option(OPTION_PROFILE_FILE, false,
                "location of profile file to use; ignored in " + arg(OPTION_PROFILE_NAME) + " is provided");
        profileFile.setArgName("file");
        options.addOption(help);
        options.addOption(new Option(Runner.OPTION_CONFIG_FILE, "option to select this tool"));
        options.addOption(root);
        options.addOption(wsdl);
        options.addOption(report);
        options.addOption(output);
        options.addOption(binding);
        options.addOption(stylesheet);
        options.addOption(template);
        options.addOption(profileName);
        options.addOption(profileFile);
        return options;
    }

    @Override
    protected boolean run(CommandLine cmd) {
        Path root = Paths.get(cmd.getOptionValue(OPTION_ROOT));
        String wsdl = cmd.getOptionValue(OPTION_WSDL);
        Path output = Paths.get(cmd.getOptionValue(OPTION_OUTPUT));
        Path report = Paths.get(cmd.getOptionValue(OPTION_REPORT));
        int bindingIndex = 0;
        if (cmd.hasOption(OPTION_BINDING)) {
            try {
                bindingIndex = Integer.parseInt(cmd.getOptionValue(OPTION_BINDING)) - 1;
                if (bindingIndex < 0) {
                    setErrorMessage("Binding index must be 1 or greater - was " + cmd.getOptionValue(OPTION_BINDING));
                    return false;
                }
            } catch (NumberFormatException e) {
                setErrorMessage("Binding index must be integer - was " + cmd.getOptionValue(OPTION_BINDING));
                return false;
            }
        }

        // default values
        String templateName = getBindingTemplateFilename();
        Path stylesheet = BasicProfileConfig.appendDefaultStylesheet(root);
        String profileFilename = BasicProfileConfig.profileFilename(getDefaultProfileName());
        String description = BasicProfileConfig.profileDescription(getDefaultProfileName());
        if (cmd.hasOption(OPTION_STYLESHEET)) {
            stylesheet = Paths.get(cmd.getOptionValue(OPTION_STYLESHEET));
        }
        if (cmd.hasOption(OPTION_PROFILE_NAME)) {
            List<String> profiles = BasicProfileConfig.profiles();
            String argName = cmd.getOptionValue(OPTION_PROFILE_NAME);
            if (!profiles.contains(argName)) {
                setErrorMessage("Profile name '" + argName + "' not in [" + Util.concatenate(profiles, ";") + "]");
                return false;
            }
            profileFilename = BasicProfileConfig.profileFilename(argName);
            templateName = BasicProfileConfig.profileFilename(argName);
            description = BasicProfileConfig.profileDescription(argName);
        } else if (cmd.hasOption(OPTION_PROFILE_FILE)) {
            profileFilename = cmd.getOptionValue(OPTION_PROFILE_FILE);
            // TODO: template file path
            description = "No description (custom file " + profileFilename + ")";
        }
        return generateBindingConfigFile(templateName, root, wsdl, bindingIndex, output, report, stylesheet, profileFilename,
                description);

        // TODO: template given as filename

    }

    /**
     *
     * @param toolsRoot path to WS-I tools root
     * @param wsdl url/path to wsdl
     * @param bindingIndex 1 based index to binding in wsdl to generate config for
     * @param output path to config file created
     * @param report path to where report file is stored
     * @return true if config file is created
     */
    public boolean generateDefaultBindingConfigFile(Path toolsRoot, String wsdl, int bindingIndex, Path output,
                                                    Path report) {
        boolean retVal = true;
        BindingInfo bindingInfo = new BindingInfo();
        if (! bindingInfo.parseWsdl(wsdl, bindingIndex)) {
            System.err.println("WSDL error: " + bindingInfo.errorMessage);
            return false;
        }
        String templateName = getDefaultProfileName();
        Path stylesheet = BasicProfileConfig.appendDefaultStylesheet(toolsRoot);
        Path profile = BasicProfileConfig.appendProfile(toolsRoot,
                BasicProfileConfig.profileFilename(getDefaultProfileName()));
        String description = BasicProfileConfig.profileDescription(getDefaultProfileName());

        try {
            BasicProfileConfig.generateBindingConfigFile(templateName, output, report, wsdl, toolsRoot,
                    profile, bindingInfo.binding, bindingInfo.namespace, true, stylesheet, description);
        } catch (IOException e) {
            retVal = false;
            setErrorMessage("IO exception" + e.getMessage());
        }
        return retVal;
    }

    /**
     *
     * @param templateFilename
     * @param toolsRoot path to WS-I tools root
     * @param wsdl url/path to wsdl
     * @param bindingIndex 1 based index to binding in wsdl to generate config for
     * @param output path to config file created
     * @param report path to where report file is stored
     * @param stylesheet
     * @param profileFilename
     * @return
     */
    public boolean generateBindingConfigFile(String templateFilename, Path toolsRoot, String wsdl, int bindingIndex,
                                             Path output, Path report, Path stylesheet, String profileFilename,
                                             String description) {
        boolean retVal = true;
        BindingInfo bindingInfo = new BindingInfo();
        if (! bindingInfo.parseWsdl(wsdl, bindingIndex)) {
            setErrorMessage("WSDL error: " + bindingInfo.errorMessage);
            return false;
        }
        Path profile = BasicProfileConfig.appendProfile(toolsRoot, profileFilename);
        try {
            BasicProfileConfig.generateBindingConfigFile(templateFilename, output, report, wsdl, toolsRoot,
                    profile, bindingInfo.binding, bindingInfo.namespace, true, stylesheet, description);
        } catch (IOException e) {
            retVal = false;
            setErrorMessage("IO exception" + e.getMessage());
        }
        return retVal;
    }

    public static void main(String[] args) {
        GenerateConfigFile tool = new GenerateConfigFile();
        CommandLine cmd;
        try {
            cmd = tool.parseCommandLine(args);
        } catch (ParseException e) {
            tool.printHelp();
            return;
        }
        boolean success = tool.run(cmd);
        System.out.println("Generate config file completed with status: " + (success ? "SUCCESS" : "FAILURE"));
    }

    private class BindingInfo {
        public String errorMessage = "";
        public String binding = "";
        public String namespace = "";

        public boolean parseWsdl(String wsdl, int bindingIndex) {
            Definition definition;
            try {
                definition = Util.getWsdlDefinition(wsdl);
                List<Binding> bindings = Util.getBindings(definition);
                if (bindings.size() == 0) {
                    errorMessage = "No bindings found in " + wsdl;
                    return false;
                }
                if (bindingIndex >= bindings.size()) {
                    errorMessage = "Binding index too big, max index is " + bindings.size();
                    return false;
                }
                binding = bindings.get(bindingIndex).getQName().getLocalPart();
                namespace = bindings.get(bindingIndex).getQName().getNamespaceURI();
                return true;
            } catch (WSDLException e) {
                errorMessage = "Exception while parsing wsdl - " + e.getMessage();
            }
            return false;
        }
    }
}
