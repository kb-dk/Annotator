package dk.kb.annotator.config;

import dk.kb.annotator.database.Database;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServiceConfig {
    public static final Logger logger = Logger.getLogger(ServiceConfig.class);

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
                .forEach(key -> {logger.info(key+"="+properties.get(key));});
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
}
