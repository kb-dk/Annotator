package dk.kb.annotator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServiceConfig {
    public static final Logger logger = LoggerFactory.getLogger(ServiceConfig.class);

    private static Properties properties;


    public static synchronized void initialize(InputStream in ) {
        try  {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Loaded properties");
        properties.keySet().stream()
                .filter(key->!"database.password".equals(key))
                .forEach(ServiceConfig::accept);
    }

    public static Properties getProperties() {
        return properties;
    }

    public static String getDatabaseHost() {
        return properties.getProperty("database.host");
    }

    public static int getDatabasePort() {
        return Integer.valueOf(properties.getProperty("database.port"));
    }

    public static String getDatabaseUser() {
        return properties.getProperty("database.user");
    }

    public static String getDatabasePassword() {
        return properties.getProperty("database.password");
    }

    public static String getSolrizrBaseUrl() { return properties.getProperty("solrizr.baseurl",null); }

    private static void accept(Object key) {
        logger.info(key + "=" + properties.get(key));
    }
}
