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
        AddAnXLinkForTesting();
        AddATagForTesting();
    }

    public static void AddAnXLinkForTesting() {
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

    public static void AddATagForTesting() {
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
    public void testGetAerialTagByUri() {
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

    @AfterClass
    public static void RemoveAllTestRecords() {
        DbWriter dbWriter = new DbWriter();
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("xlink"), "xlink1");
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("tag"), "tag1");
    }

}
