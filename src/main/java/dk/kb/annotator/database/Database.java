package dk.kb.annotator.database;

import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class Database {

    private static final Logger logger = Logger.getLogger(Database.class);

    public Database() {}

    public static Connection getConnection() {

    String DATASOURCE_CONTEXT = "java:comp/env/jdbc/KBsannotationDB";

    
	Connection conn = null;

	try {
	
	    Context initialContext = new InitialContext();
     
	    DataSource datasource = (DataSource)initialContext.lookup(DATASOURCE_CONTEXT);
	    if (datasource != null) {
		    conn = datasource.getConnection();
            logger.info(" Database Connection established");

	    } else {
		    logger.debug("Failed to lookup datasource.");
	    }
      
	}
	catch ( NamingException ex ) {
	    logger.error("Naming ex - Cannot get connection: " + ex);
	}
	catch(SQLException ex){
	    logger.error("SQL ex - Cannot get connection: " + ex);
        ex.printStackTrace();
	}
	return conn;

    }
}
