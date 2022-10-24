package dk.kb.annotator.database;

import dk.kb.annotator.config.ServiceConfig;
import dk.kb.annotator.model.Xlink;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class DbReaderTest {

    @Before
    public void setupDatabase() throws FileNotFoundException {
        ServiceConfig.initialize(new FileInputStream("src/test/resources/annotator.properties"));
    }
    @Test
    public void testGetXlinkById() {
        DbReader dbReader = new DbReader();
        List<Xlink> result = dbReader.readXlinks("e1226f48-43ef-4cb8-95bd-f01da24f05a2",true);
        System.out.println(result);
    }

    @Test
    public void testGetXlinkByUri() {
        DbReader dbReader = new DbReader();
        List<Xlink> result = dbReader.readXlinks("/images/luftfo/2011/maj/luftfoto/object63946",false);
        result.forEach(System.out::println);
    }

}
