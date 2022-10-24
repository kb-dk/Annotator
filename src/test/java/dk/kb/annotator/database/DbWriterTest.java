package dk.kb.annotator.database;

import dk.kb.annotator.config.ServiceConfig;
import dk.kb.annotator.model.Xlink;
import org.junit.Before;
import org.junit.Test;

public class DbWriterTest {

    @Before
    public void setupDatabase() {
        ServiceConfig.initialize("src/test/resources/annotator.properties");
    }


    @Test
    public void testGetXlinkById() {
        DbWriter dbWriter = new DbWriter();
        Xlink xLink = new Xlink();
    }
}
