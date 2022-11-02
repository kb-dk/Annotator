package dk.kb.annotator.database;

import dk.kb.annotator.api.ApiUtils;
import dk.kb.annotator.config.ServiceConfig;
import dk.kb.annotator.model.*;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.List;


public class DbReaderTest {

    @BeforeClass
    public static void setupDatabase() throws FileNotFoundException {
        ServiceConfig.initialize(new FileInputStream("src/test/resources/annotator.properties"));
        addAnXLinkForTesting();
        addATagForTesting();
    }

    public static void addAnXLinkForTesting() {
        DbWriter dbWriter = new DbWriter();
        Xlink xLink = new Xlink();
        xLink.setId("xlink1");
        Category category = new Category();
        category.setAuthor("test");
        category.setTerm("isPartOf");
        category.setLabel("");
        xLink.setRole(category);
        xLink.setTitle("title1");
        Link linkTo = new Link();
        linkTo.setHref("/test/test/test");
        xLink.setLinkTo(linkTo);
        Link linkFrom = new Link();
        linkFrom.setHref("/test/test/test");
        xLink.setLinkFrom(linkFrom);
        xLink.setType("simple");
        Calendar calendar = Calendar.getInstance();
        xLink.setUpdated(calendar);
        xLink.setCreator("test");

        dbWriter.writeXlink(xLink);
    }

    public static void addATagForTesting() {
        DbWriter dbWriter = new DbWriter();
        Tag tag = new Tag();
        tag.setId("tag1");
        Content content = new Content();
        content.setType("text");
        content.setValue("Value1");
        tag.setContent(content);
        tag.setLink("/test/test/test");
        tag.setCreator("Test");
        Calendar calendar = Calendar.getInstance();
        tag.setUpdated(calendar);

        dbWriter.writeAerialTag(tag);
    }

    @Test
    public void testGetXlinkById() {
        DbReader dbReader = new DbReader();
        List<Xlink> result = dbReader.readXlinks("xlink1",true);
        System.out.println("From testGetXlinkById:" + result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetXlinkByUri() {
        DbReader dbReader = new DbReader();
        List<Xlink> result = dbReader.readXlinks("/test/test/test",false);
        System.out.println("From testGetXlinkByUri:");
        result.forEach(System.out::println);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetTagByUri() {
        DbReader dbReader = new DbReader();
        List<Tag> result = dbReader.readTags("tag1",true);
        System.out.println("From testGetTagByUri:" + result);
        assertEquals(1, result.size());
    }
    @Test
    public void testGetAerialTagByUri() { // TODO add creator and timestamp to tag_join table and test again
        DbReader dbReader = new DbReader();
        List<Tag> result = dbReader.readAerialTags("/test/test/test",false);
        System.out.println("From testGetTagByUri:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetTagByCreatedBefore() {
        DbReader dbReader = new DbReader();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        List<Tag> result = dbReader.readTags(calendar, false, "/test/test/test");
        System.out.println("From testGetTagByCreatedBefore" + calendar + ":" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetCommentsByCreatedBefore() { // TODO add timestamp column to comment database and test again
        DbReader dbReader = new DbReader();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        List<Comment> result = dbReader.readComments(calendar, false, "/test/test/test");
        System.out.println("From testGetCommentsByCreatedBefore" + calendar + ":" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetCommentsById() { // TODO add timestamp column to comment database and test again
        DbReader dbReader = new DbReader();
        List<Comment> result = dbReader.readComments("comment1", true);
        System.out.println("From testGetCommentsById:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetCommentsByUri() { // TODO add timestamp column to comment database and test again
        DbReader dbReader = new DbReader();
        List<Comment> result = dbReader.readComments("/test/test/test", false);
        System.out.println("From testGetCommentsByUri:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetObjectIdFromTagId() { // TODO add creator and timestamp to tag_join table and test again
        DbReader dbReader = new DbReader();
        String result = dbReader.getObjectIdFromTagId("3ae98fc4-965b-4256-8368-4fe7d74dec73");
        System.out.println("From testGetObjectIdFromTagId:" + result);
//        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetObjectIdFromCommentId() { // TODO add creator and timestamp to tag_join table and test again
        DbReader dbReader = new DbReader();
        String result = dbReader.getObjectIdFromCommentId("46c5a11a-c0a3-4680-ada8-f3d6b84bef5a");
        System.out.println("From testGetObjectIdFromCommentId:" + result);
//        assertTrue(result.size() >= 1);
    }

    @AfterClass
    public static void removeAllTestRecords() {
        DbWriter dbWriter = new DbWriter();
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("xlink"), "xlink1");
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("tag"), "tag1");
    }

}
