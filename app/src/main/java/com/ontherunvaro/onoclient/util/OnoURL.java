package com.ontherunvaro.onoclient.util;

/**
 * Created by ontherunvaro on 5/11/16.
 */

public class OnoURL {

    private static final String BASE_URL = "ono.es";
    private static final String SEPARATOR = "/";

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static OnoURLBuilder builder() {
        return new OnoURLBuilder();
    }

    public enum OnoPage {

        CLIENT_AREA("clientes"), LOGIN("area-cliente/login");

        final String value;

        OnoPage(String val) {
            this.value = val;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class OnoURLBuilder {

        private final StringBuilder current;

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

        @Override
        public String toString() {
            return current.toString();
        }
    }
}
