package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/** Reads settings from config.properties, overridable by system properties and environment variables. */
public final class ConfigReader {

    private static final Properties PROPS = load();

    private ConfigReader() {
    }

    /** Lookup order: -Dkey system property, then KEY env var (camelCase -> UPPER_SNAKE), then config.properties. */
    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null) value = System.getenv(toEnvName(key));
        if (value == null) value = PROPS.getProperty(key);
        if (value == null) throw new IllegalStateException("Missing config value: " + key);
        return value.trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    private static String toEnvName(String key) {
        return key.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }

    private static Properties load() {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) throw new IllegalStateException("config.properties not found on the classpath");
            Properties props = new Properties();
            props.load(in);
            return props;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
