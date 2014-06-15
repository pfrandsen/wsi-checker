package dk.pfrandsen;

import dk.pfrandsen.wsdl.Util;
import dk.pfrandsen.wsdl.wsi.BasicProfileConfig;
import dk.pfrandsen.wsdl.wsi.WsiProfile;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GenerateConfigFile extends CommandLineTool {
    public static String OPTION_WSDL = "wsdl";
    public static String OPTION_REPORT = "report";
    public static String OPTION_OUTPUT = "output";
    public static String OPTION_BINDING = "binding";
    public static String OPTION_STYLESHEET = "stylesheet";
    public static String OPTION_PROFILE_NAME = "profileName";
    public static String OPTION_PROFILE_FILE = "profileFile";
    public static String OPTION_TEMPLATE = "template";
    public static String USAGE = "Usage: java -jar <jar-file> " + arg(Runner.OPTION_CONFIG_FILE) + " "
            + arg(Runner.OPTION_ROOT) + " <folder> " + arg(OPTION_WSDL)
            + " <file> " + arg(OPTION_REPORT) + " <file> " + arg(OPTION_OUTPUT) + " <file> [" + arg(OPTION_BINDING)
            + " <index>" + arg(OPTION_STYLESHEET) + " <file> " + arg(OPTION_PROFILE_NAME) + "<profile-name>"
            + arg(OPTION_PROFILE_FILE) + " <file>" + arg(OPTION_TEMPLATE) + " <file>]";

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
        System.out.println(tool.getStatusMessage(success));
    }

    @Override
    public String getUsageString() {
        return USAGE;
    }

    @Override
    public String getToolDescription() {
        return "Tool for generating WS-I analyzer configuration file.";
    }

    private String getDefaultProfileName() {
        return WsiProfile.BASIC_PROFILE_11_SOAP_10.name();
    }

    private String getBindingTemplateFilename() {
        return "wsi_binding_config_template.xml";
    }

    public Options getCommandlineOptions() {
        Options options = new Options();
        Option help = new Option(Runner.OPTION_HELP, USAGE);
        Option root = new Option(Runner.OPTION_ROOT, true,
                "WS-I tools root folder (containing schemas, stylesheets etc.)");
        root.setRequired(true);
        root.setArgName("folder");
        Option wsdl = new Option(OPTION_WSDL, true,
                "location of wsdl file");
        wsdl.setRequired(true);
        wsdl.setArgName("file");
        Option report = new Option(OPTION_REPORT, true,
                "location of report file");
        report.setRequired(true);
        report.setArgName("file");
        Option output = new Option(OPTION_OUTPUT, true,
                "location of generated config file");
        output.setRequired(true);
        report.setArgName("file");
        Option binding = new Option(OPTION_BINDING, true,
                "index of binding 1..number of bindings in wsdl (default 1) to generate config for");
        report.setArgName("index");
        Option stylesheet = new Option(OPTION_STYLESHEET, true,
                "location of report stylesheet (default: standard location of stylesheet relative to tools root)");
        stylesheet.setArgName("file");
        Option template = new Option(OPTION_TEMPLATE, true,
                "location of generated config file");
        template.setArgName("file");
        Option profileName = new Option(OPTION_PROFILE_NAME, true,
                "name of profile file to use; default is '" + getDefaultProfileName() + "'");
        profileName.setArgName("name");
        Option profileFile = new Option(OPTION_PROFILE_FILE, true,
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

    private String getTemplateContent(CommandLine cmd) {
        String templateContent = "";
        if (cmd.hasOption(OPTION_TEMPLATE)) {
            String templateArg = cmd.getOptionValue(OPTION_TEMPLATE);
            try {
                templateContent = FileUtils.readFileToString(new File(templateArg));
            } catch (IOException e) {
                setErrorMessage("Exception while loading template '" + templateArg + ", " + e.getMessage());
            }
        } else {
            String name = getBindingTemplateFilename();
            try {
                templateContent = BasicProfileConfig.getTemplateContent(name);
            } catch (IOException e) {
                setErrorMessage("Exception while loading template '" + name + ", " + e.getMessage());
            }
        }
        return templateContent;
    }

    @Override
    public boolean run(CommandLine cmd) {
        Path root = Paths.get(cmd.getOptionValue(Runner.OPTION_ROOT));
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

        String templateContent = getTemplateContent(cmd);
        if (templateContent.length() == 0) {
            return false;
        }
        // default values
        Path stylesheet = BasicProfileConfig.appendDefaultStylesheet(root);
        String profileFilename = BasicProfileConfig.profileTemplateFilename(getDefaultProfileName());
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
            profileFilename = BasicProfileConfig.profileTemplateFilename(argName);
            description = BasicProfileConfig.profileDescription(argName);
        } else if (cmd.hasOption(OPTION_PROFILE_FILE)) {
            profileFilename = cmd.getOptionValue(OPTION_PROFILE_FILE);
            description = "No description (custom file " + profileFilename + ")";
        }

        Path assertionsFile = BasicProfileConfig.appendProfile(root, profileFilename);
        return generateBindingConfigFileFromTemplate(templateContent, root, wsdl, bindingIndex, output, report,
                stylesheet, assertionsFile, description);
    }

    @Override
    public String getStatusMessage(boolean runStatus) {
        return "Generate config file completed with status: " + (runStatus ? "SUCCESS" : "FAILURE");
    }

    /**
     * @param template       template contents
     * @param toolsRoot      path to WS-I tools root
     * @param wsdl           url/path to wsdl
     * @param bindingIndex   1 based index to binding in wsdl to generate config for
     * @param output         path to config file created
     * @param report         path to where report file is stored
     * @param stylesheet     path to report stylesheet
     * @param testAssertions path to test assertions file
     * @param description    text to place in description element
     * @return true if config file created
     */
    public boolean generateBindingConfigFileFromTemplate(String template, Path toolsRoot, String wsdl, int bindingIndex,
                                                         Path output, Path report, Path stylesheet, Path testAssertions,
                                                         String description) {
        boolean retVal = true;
        BindingInfo bindingInfo = new BindingInfo();
        if (!bindingInfo.parseWsdl(wsdl, bindingIndex)) {
            setErrorMessage("WSDL error: " + bindingInfo.errorMessage);
            return false;
        }
        try {
            BasicProfileConfig.generateBindingConfigFileFromTemplate(template, output, report, wsdl, testAssertions,
                    bindingInfo.binding, bindingInfo.namespace, true, stylesheet, description);
        } catch (IOException e) {
            retVal = false;
            setErrorMessage("IO exception" + e.getMessage());
        }
        return retVal;
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
