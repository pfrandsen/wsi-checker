package dk.pfrandsen.wsdl.wsi;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BasicProfileConfig {
    private static String DESCRIPTION = "%DESCRIPTION%";
    private static String VERBOSE = "%VERBOSE%";
    private static String REPORT_FILE = "%REPORT_FILE%";
    private static String NAMESPACE = "%NAMESPACE%";
    private static String BINDING = "%BINDING%";
    private static String WSDL_URI = "%WSDL_URI%";
    private static String ASSERTION_FILE = "%ASSERTION_FILE%";
    private static String STYLESHEET_FILE = "%STYLESHEET_FILE%";

    public static String getDefaultStylesheetFilename() {
        return "report.xsl";
    }

    public static Path appendStylesheetPath(Path toolRoot) {
        return toolRoot.resolve(getStylesheetRelativePath());
    }

    public static Path appendStylesheet(Path toolRoot, String stylesheetFilename) {
        return appendStylesheetPath(toolRoot).resolve(stylesheetFilename);
    }

    public static Path appendDefaultStylesheet(Path toolRoot) {
        return appendStylesheetPath(toolRoot).resolve(Paths.get(getDefaultStylesheetFilename()));
    }

    public static Path appendProfilePath(Path toolRoot) {
        return toolRoot.resolve(getProfileRelativePath());
    }

    public static Path appendProfile(Path toolRoot, String profileFilename) {
        return appendProfilePath(toolRoot).resolve(profileFilename);
    }

    public static Path appendSchemaPath(Path toolRoot) {
        return toolRoot.resolve(getSchemaRelativePath());
    }

    public static Path appendSchema(Path toolRoot, String schemaFilename) {
        return appendSchemaPath(toolRoot).resolve(schemaFilename);
    }

    public static Path getStylesheetRelativePath() {
        return Paths.get("common", "xsl");
    }

    public static Path getProfileRelativePath() {
        return Paths.get("common", "profiles");
    }

    public static Path getSchemaRelativePath() {
        return Paths.get("common", "schemas");
    }

    public static List<String> profiles() {
        List<String> values = new ArrayList<>();
        for (WsiProfile profile : WsiProfile.values()) {
            values.add(profile.name());
        }
        return values;
    }

    /**
     * @param profileName WsiProfile name
     * @return the filename from the WsiProfile enum or empty string if not found
     */
    public static String profileTemplateFilename(String profileName) {
        for (WsiProfile p : WsiProfile.values()) {
            if (p.name().equals(profileName)) {
                return p.getAssertionFilename();
            }
        }
        return "";
    }

    /**
     * @param profileName WsiProfile name
     * @return the description from the WsiProfile enum or empty string if not found
     */
    public static String profileDescription(String profileName) {
        for (WsiProfile p : WsiProfile.values()) {
            if (p.name().equals(profileName)) {
                return p.getDescription();
            }
        }
        return "";
    }

    /**
     * @return list of template filenames included in the jar resources
     */
    public static List<String> templateFilename() {
        List<String> names = new ArrayList<>();
        try {
            InputStream folder = BasicProfileConfig.class.getResourceAsStream("/wsi/templates");
            BufferedReader reader = new BufferedReader(new InputStreamReader(folder));
            String line;
            while ((line = reader.readLine()) != null) {
                names.add(line);
            }
        } catch (IOException e1) {
            System.out.println("Exception " + e1.getMessage());
        }
        return names;
    }

    public static String getTemplateContent(String templateFilename) throws IOException {
        String separator = System.getProperty("line.separator");
        StringBuilder template = new StringBuilder();
        InputStream stream = BasicProfileConfig.class.getResourceAsStream("/wsi/templates/" + templateFilename);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                template.append(line).append(separator);
            }
        }
        return template.toString();
    }

    public static boolean generateBindingConfigFile(String templateFilename, Path configFile, Path reportFile,
                                                    String url, Path profile, String binding,
                                                    String namespace, boolean verbose, Path stylesheet,
                                                    String description)
            throws IOException {
        if (!templateFilename().contains(templateFilename)) {
            System.out.println("Template '" + templateFilename + "' does not exist.");
            return false;
        }
        String template = getTemplateContent(templateFilename);
        return generateBindingConfigFileFromTemplate(template, configFile, reportFile, url, profile, binding,
                namespace, verbose, stylesheet, description);
    }

    public static boolean generateBindingConfigFileFromTemplate(String template, Path configFile, Path reportFile,
                                                                String url, Path profile, String binding,
                                                                String namespace, boolean verbose, Path stylesheet,
                                                                String description)
            throws IOException {
        if (configFile.getParent() != null && !configFile.getParent().toFile().exists()) {
            if (!configFile.getParent().toFile().mkdirs()) {
                System.out.println("Could not create " + configFile.getParent().toString());
            }
        }
        template = template.replaceAll(DESCRIPTION, description);
        template = template.replaceAll(VERBOSE, verbose ? "true" : "false");
        template = template.replaceAll(REPORT_FILE, reportFile.toString());
        template = template.replaceAll(NAMESPACE, namespace);
        template = template.replaceAll(BINDING, binding);
        template = template.replaceAll(WSDL_URI, url);
        template = template.replaceAll(ASSERTION_FILE, profile.toString());
        template = template.replaceAll(STYLESHEET_FILE, stylesheet.toString());
        BufferedWriter writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8);
        writer.write(template);
        writer.flush();
        writer.close();
        return true;
    }

}