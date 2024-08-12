package ru.ibs.pmp.zzztestapplication.some.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author NAnishhenko
 */
public class SomeTestClass2 {

    public static void test() {
        String changeString = "30496201  44288-M1 \"185681366\"\n"
                + "22639035  44293-S5 \"770000 1086295104\"";

        Pattern pattern = Pattern.compile("^(.+?)\\s\\s(.+?)-.+?\\s\"(.+?)\"", Pattern.MULTILINE);

        class PolicyToChange {

            private final String patientId;
            private final String smoId;
            private final String number;

            public PolicyToChange(String patientId, String smoId, String number) {
                this.patientId = patientId;
                this.smoId = smoId;
                this.number = number;
            }

            public String getPatientId() {
                return patientId;
            }

            public String getSmoId() {
                return smoId;
            }

            public String getNumber() {
                return number;
            }
        }

        class TT {

            public Map<String, PolicyToChange> get(String changeString) {
                Map<String, PolicyToChange> retMap = new HashMap<>();
                Matcher matcher = pattern.matcher(changeString);
                while (matcher.find()) {
                    String patientId = matcher.group(1);
                    String smoId = matcher.group(2);
                    String policyNumber = matcher.group(3);
                    PolicyToChange policyToChange = new PolicyToChange(patientId, smoId, policyNumber);
                    retMap.put(patientId, policyToChange);
                }
                return retMap;
            }
        }

        Map<String, PolicyToChange> get = new TT().get(changeString);
        System.out.println("Hello!");
    }
}
