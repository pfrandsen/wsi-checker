package dk.pfrandsen.wsdl.wsi;


public enum WsiProfile {
    BASIC_PROFILE_11 {
        public String getAssertionFilename() {
            return "BasicProfile_1.1_TAD.xml";
        }

        public String getDescription() {
            return "BP11 Basic Profile version 1.1 Test Assertion Document (TAD), WG Draft";
        }

        public String getLocation() {
            return "http://ws-i.org/Profiles/BasicProfile-1.1-2004-06-11.html";
        }
    },

    BASIC_PROFILE_10 {
        public String getAssertionFilename() {
            return "BasicProfileTestAssertions.xml";
        }

        public String getDescription() {
            return "BP1 Basic Profile version 1.0 Test Assertion Document (TAD), Final";
        }

        public String getLocation() {
            return "http://www.ws-i.org/Profiles/BasicProfile-1.0-2004-04-16.html";
        }
    },

    BASIC_PROFILE_11_SOAP_10 {
        public String getAssertionFilename() {
            return "SSBP10_BP11_TAD.xml";
        }

        public String getDescription() {
            return "SSBP1+BP11 Simple SOAP Binding Profile version 1.0 + Ba1sic Profile version 1.1 " +
                    "Test Assertion Document (TAD), Final";
        }

        public String getLocation() {
            return "http://ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html, " +
                    "http://ws-i.org/Profiles/SimpleSoapBindingProfile-1.0-2004-08-24.html";
        }
    };

    public abstract String getAssertionFilename();

    public abstract String getDescription();

    public abstract String getLocation();

}
