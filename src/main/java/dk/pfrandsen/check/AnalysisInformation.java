package dk.pfrandsen.check;

public class AnalysisInformation {
    private String assertion;
    private String message;
    private int severity;
    private String details;

    public AnalysisInformation(String assertion, String message, int severity) {
        this(assertion, message, severity, "");
    }

    public AnalysisInformation(String assertion, String message, int severity, String details) {
        this.assertion = assertion;
        this.message = message;
        this.severity = severity;
        this.details = details;
    }

    public AnalysisInformation(AnalysisInformation info) {
        this.assertion = info.assertion;
        this.message = info.message;
        this.severity = info.severity;
        this.details = info.details;
    }

    public String getAssertion() {
        return assertion;
    }

    public String getMessage() {
        return message;
    }

    public int getSeverity() {
        return severity;
    }

    public String getDetails() {
        return details;
    }

    public boolean hasDetails() {
        return details.length() > 0;
    }

    public String toJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"assertion\":\"" + getAssertion() + "\"");

        builder.append(", \"message\":\"" + getMessage() + "\"");  // TODO: encode
        builder.append(", \"severity\":" + getSeverity());
        if (hasDetails()) {
            builder.append(", \"details\":\"" + getDetails() + "\"");
        }
        builder.append("}");
        return builder.toString();
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
