package dk.kb.annotator.database;

import dk.kb.annotator.model.*;
import org.apache.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.List;

public class DbReaderTest {

    private static final Logger logger = Logger.getLogger(DbReaderTest.class);
    private final DbReader dbReader = new DbReader();

    private Calendar getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return calendar;
    }

    @BeforeClass
    public static void setupDatabase() throws FileNotFoundException {
        DbWriterTest.setupDatabase();
        DbWriterTest.addATestCommentToDB();
        DbWriterTest.addATestXlinkToDB();
        DbWriterTest.addATestAerialTagToDB();
    }

    @Test
    public void testGetXlinkById() {
        List<Xlink> result = dbReader.readXlinks("xlink1",true);
        System.out.println("From testGetXlinkById:" + result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetXlinkByUri() {
        List<Xlink> result = dbReader.readXlinks("/test/test/test",false);
        System.out.println("From testGetXlinkByUri:");
        result.forEach(System.out::println);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetTagByUri() {
        List<Tag> result = dbReader.readTags("tag1",true);
        System.out.println("From testGetTagByUri:" + result);
        assertEquals(1, result.size());
    }
    @Test
    public void testGetAerialTagByUri() {
        List<Tag> result = dbReader.readAerialTags("/test/test/test/objectxxxxx",false);
        System.out.println("From testGetTagByUri:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetTagByCreatedBefore() {
        List<Tag> result = dbReader.readTags(getYesterday(), false, "/test/test/test/objectxxxxx");
        System.out.println("From testGetTagByCreatedBefore:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetCommentsByCreatedBefore() {
        List<Comment> result = dbReader.readComments(getYesterday(), false, "/test/test/test/objectxxxxx");
        System.out.println("From testGetCommentsByCreatedBefore:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetCommentsById() {
        List<Comment> result = dbReader.readComments("comment1", true);
        System.out.println("From testGetCommentsById:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetCommentsByUri() {
        List<Comment> result = dbReader.readComments("/test/test/test/objectxxxxx", false);
        System.out.println("From testGetCommentsByUri:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetObjectIdFromTagId() {
        String result = dbReader.getObjectIdFromTagId("tag1");
        System.out.println("From testGetObjectIdFromTagId:" + result);
        assertEquals("/test/test/test/objectxxxxx", result);
    }

    @Test
    public void testGetObjectIdFromCommentId() {
        String result = dbReader.getObjectIdFromCommentId("comment1");
        System.out.println("From testGetObjectIdFromCommentId:" + result);
        assertEquals("/test/test/test/objectxxxxx", result);
    }

    @AfterClass
    public static void removeAllTestRecords() {
        logger.info("Cleaning up ...");
        DbWriterTest.deleteTheXlink("xlink1");
        DbWriterTest.deleteTheAerialTag("tag1");
        DbWriterTest.deleteTheComment("comment1");
        DbWriterTest.removeTestRecords();
    }
}
