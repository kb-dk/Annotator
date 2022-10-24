package dk.kb.annotator.config;

import dk.kb.annotator.database.Database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServiceConfig {

    private static Properties properties;


    public static synchronized void initialize(String configfile) {
        try  {
            InputStream input = new FileInputStream(configfile);
            properties = new Properties();
            properties.load(input);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
