package dk.kb.annotator.database;

//Log imports

import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class Database {

    private static Logger logger = Logger.getLogger(Database.class);

    public Database() {}

    public static java.sql.Connection getConnection() {

//	String DATASOURCE_CONTEXT = "java:comp/env/jdbc/udvTest";
        String DATASOURCE_CONTEXT = "java:comp/env/jdbc/KBsannotationDB";

    
	Connection conn = null;

	try {
	
	    Context initialContext = new InitialContext();
     
	    if ( initialContext == null){
		logger.debug("JNDI problem. Cannot get InitialContext.");
	    }
      
	    DataSource datasource = (DataSource)initialContext.lookup(DATASOURCE_CONTEXT);
	    if (datasource != null) {
		conn = datasource.getConnection();
            logger.info(" Database Connection established");
            System.out.println(" Database Connection established");
	    }
	    else {
		logger.debug("Failed to lookup datasource.");
	    }
      
	}
	catch ( NamingException ex ) {
	    logger.debug("Naming ex - Cannot get connection: " + ex);
	}
	catch(SQLException ex){
	    logger.debug("SQL ex - Cannot get connection: " + ex);
	}
	return conn;

    }
}
