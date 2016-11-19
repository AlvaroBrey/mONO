package com.ontherunvaro.onoclient.util;

/**
 * Created by ontherunvaro on 5/11/16.
 */

public class OnoURL {

    private static final String BASE_URL = "ono.es";
    private static final String SEPARATOR = "/";

    public enum OnoPage {

        CLIENT_AREA("clientes"), LOGIN("area-cliente/login");

        protected String value;

        OnoPage(String val) {
            this.value = val;
        }

        @Override
        public String toString() {
            return value;
        }
    }


    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static OnoURLBuilder builder() {
        return new OnoURLBuilder();
    }


    public static class OnoURLBuilder {

        private StringBuilder current;

        private OnoURLBuilder() {
            current = new StringBuilder("https://" + BASE_URL);
        }

        public OnoURLBuilder withPage(OnoPage page) {
            current.append(SEPARATOR);
            current.append(page.value);
            return this;
        }

        public OnoURLBuilder withString(String value) {
            current.append(SEPARATOR);
            current.append(value);
            return this;
        }

        public String build() {
            return current.toString();
        }

        @Override
        public String toString() {
            return current.toString();
        }
    }
}
