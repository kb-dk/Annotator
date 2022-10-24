package dk.kb.annotator.listeners;

import dk.kb.annotator.config.ServiceConfig;
import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class InitializationContextListener implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(InitializationContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        InputStream in;
        try {
            InitialContext ctx = new InitialContext();
            String propFile = (String) ctx.lookup("java:comp/env/annotatorProperties");
            in = new FileInputStream(propFile);
        } catch (NamingException e) {
            logger.warn("Using default annotator properties");
            in = this.getClass().getResourceAsStream("/annotator.properties");
        } catch (FileNotFoundException e) {
            logger.fatal("Configfile not found ", e);
            throw new RuntimeException("Configfile not found ", e);
        }
        ServiceConfig.initialize(in);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
