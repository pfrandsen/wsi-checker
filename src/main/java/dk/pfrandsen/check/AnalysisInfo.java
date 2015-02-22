package dk.pfrandsen.check;

public class AnalysisInfo {
    private String assertion;
    private String message;
    private int severity;
    private String details;

    public AnalysisInfo() {
        this.assertion = "";
        this.message = "";
        this.severity = AnalysisInfoCollector.SEVERITY_LEVEL_UNKNOWN;
        this.details = "";
    }

    public AnalysisInfo(String assertion, String message, int severity) {
        this(assertion, message, severity, "");
    }

    public AnalysisInfo(String assertion, String message, int severity, String details) {
        this.assertion = assertion;
        this.message = message;
        this.severity = severity;
        this.details = details;
    }

    public AnalysisInfo(AnalysisInfo info) {
        this.assertion = info.assertion;
        this.message = info.message;
        this.severity = info.severity;
        this.details = info.details;
    }

    public String getAssertion() {
        return assertion;
    }

    public void setAssertion(String assertion) {
        this.assertion = assertion;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean hasDetails() {
        return details.length() > 0;
    }

    @Override
    public String toString() {
        return "AnalysisInformation{" +
                "assertion='" + assertion + '\'' +
                ", message='" + message + '\'' +
                ", severity=" + severity +
                ", details='" + details + '\'' +
                '}';
    }
}
