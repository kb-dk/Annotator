package dk.kb.annotator.listeners;

import dk.kb.annotator.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class InitializationContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(InitializationContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        InputStream in;
        String version = getClass().getPackage().getImplementationVersion();
        logger.info("Starting Annotatator ...");
        try {
            InitialContext ctx = new InitialContext();
            String propFile = (String) ctx.lookup("java:comp/env/annotatorProperties");
            in = new FileInputStream(propFile);
        } catch (NamingException e) {
            logger.warn("Using default annotator properties");
            in = this.getClass().getResourceAsStream("/annotator.properties");
        } catch (FileNotFoundException e) {
            logger.error("Configfile not found ", e);
            throw new RuntimeException("Configfile not found ", e);
        }
        ServiceConfig.initialize(in);
        logger.info("Annotator version "+version+" started succesfully");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
