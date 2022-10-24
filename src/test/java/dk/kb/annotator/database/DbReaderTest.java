package dk.kb.annotator.database;

import dk.kb.annotator.config.ServiceConfig;
import dk.kb.annotator.model.Xlink;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DbReaderTest {

    @Before
    public void setupDatabase() {
        ServiceConfig.initialize("src/test/resources/annotator.properties");
    }

    @Test
    public void testGetXlinkById() {
        DbReader dbReader = new DbReader();
        List<Xlink> result = dbReader.readXlinks("e1226f48-43ef-4cb8-95bd-f01da24f05a2",true);
    }

    @Test
    public void testGetXlinkByUri() {
        DbReader dbReader = new DbReader();
        List<Xlink> result = dbReader.readXlinks("/images/luftfo/2011/maj/luftfoto/object63946",false);
        result.forEach(System.out::println);
    }

}
