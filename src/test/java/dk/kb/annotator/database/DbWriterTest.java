package dk.kb.annotator.database;

import dk.kb.annotator.api.ApiUtils;
import dk.kb.annotator.config.ServiceConfig;
import dk.kb.annotator.model.*;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class DbWriterTest {

    @Before
    public void setupDatabase() throws FileNotFoundException {
        ServiceConfig.initialize(new FileInputStream("src/test/resources/annotator.properties"));
    }


    @Test
    public void testWriteXlink() {
        DbWriter dbWriter = new DbWriter();
        DbReader dbReader = new DbReader();

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
        xLink.setCreator("Test");

        dbWriter.writeXlink(xLink);

        ArrayList<Xlink> xLinks = dbReader.readXlinks(xLink.getId(), true);
        System.out.println(xLinks);

        assertEquals(1, xLinks.size());

        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("xlink"), xLink.getId());
    }

    @Test
    public void testWriteAerialTag(){
        DbWriter dbWriter = new DbWriter();
        DbReader dbReader = new DbReader();

        Tag tag = new Tag();
        tag.setId("tag1");
        Content content = new Content();
        content.setType("text");
        content.setValue("Value1");
        tag.setContent(content);
        tag.setLink("/test/test/test");
        tag.setCreator("Test");
        Calendar calendar2 = Calendar.getInstance();
        tag.setUpdated(calendar2);

        dbWriter.writeAerialTag(tag);

        ArrayList<Tag> tags = dbReader.readTags(tag.getId(), true);
        System.out.println(tags);

        assertEquals(1, tags.size());

        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("tag"), tag.getId());
    }


}
