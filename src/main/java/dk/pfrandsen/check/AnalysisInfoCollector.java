package dk.pfrandsen.check;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnalysisInfoCollector {
    public static int SEVERITY_LEVEL_UNKNOWN = -1;
    public static int SEVERITY_LEVEL_CRITICAL = 1;
    public static int SEVERITY_LEVEL_MAJOR = 2;
    public static int SEVERITY_LEVEL_MINOR = 3;
    public static int SEVERITY_LEVEL_COSMETIC = 4;

    private List<AnalysisInfo> errors;
    private List<AnalysisInfo> warnings;
    private List<AnalysisInfo> info;

    public AnalysisInfoCollector() {
        errors = new ArrayList<AnalysisInfo>();
        warnings = new ArrayList<AnalysisInfo>();
        info = new ArrayList<AnalysisInfo>();
    }

    public AnalysisInfoCollector(AnalysisInfoCollector collector) {
        errors = new ArrayList<AnalysisInfo>();
        warnings = new ArrayList<AnalysisInfo>();
        info = new ArrayList<AnalysisInfo>();
        for (AnalysisInfo error : collector.errors) {
            errors.add(new AnalysisInfo(error));
        }
        for (AnalysisInfo warning : collector.warnings) {
            warnings.add(new AnalysisInfo(warning));
        }
        for (AnalysisInfo i : collector.info) {
            info.add(new AnalysisInfo(i));
        }
    }

    public void add(AnalysisInfoCollector collector) {
        for (AnalysisInfo error : collector.errors) {
            errors.add(new AnalysisInfo(error));
        }
        for (AnalysisInfo warning : collector.warnings) {
            warnings.add(new AnalysisInfo(warning));
        }
        for (AnalysisInfo i : collector.info) {
            info.add(new AnalysisInfo(i));
        }
    }

    public List<AnalysisInfo> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    // used by json de-serializer
    public void setErrors(List<AnalysisInfo> errors) {
        this.errors = errors;
    }

    public List<AnalysisInfo> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    // used by json de-serializer
    public void setWarnings(List<AnalysisInfo> warnings) {
        this.warnings = warnings;
    }

    public List<AnalysisInfo> getInfo() {
        return Collections.unmodifiableList(info);
    }

    // used by json de-serializer
    public void setInfo(List<AnalysisInfo> info) {
        this.info = info;
    }

    public int errorCount() {
        return errors.size();
    }

    public void addError(String assertion, String message, int severity) {
        addError(assertion, message, severity, "");
    }

    public void addError(String assertion, String message, int severity, String details) {
        add(errors, assertion, message, severity, details);
    }

    public int warningCount() {
        return warnings.size();
    }

    public void addWarning(String assertion, String message, int severity) {
        addWarning(assertion, message, severity, "");
    }

    public void addWarning(String assertion, String message, int severity, String details) {
        add(warnings, assertion, message, severity, details);
    }

    public int infoCount() {
        return info.size();
    }

    public void addInfo(String assertion, String message, int severity) {
        addInfo(assertion, message, severity, "");
    }

    public void addInfo(String assertion, String message, int severity, String details) {
        add(info, assertion, message, severity, details);
    }

    private void add(List<AnalysisInfo> collection, String assertion, String message, int severity, String details) {
        collection.add(new AnalysisInfo(assertion, message, severity, details));
    }
}
