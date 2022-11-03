package dk.kb.annotator.database;

import dk.kb.annotator.api.ApiUtils;
import dk.kb.annotator.config.ServiceConfig;
import dk.kb.annotator.model.*;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class DbWriterTest {

    private static final Logger logger = Logger.getLogger(DbWriterTest.class);

    @BeforeClass
    public static void setupDatabase() throws FileNotFoundException {
        ServiceConfig.initialize(new FileInputStream("src/test/resources/annotator.properties"));
        createEditionForTesting();
        createTypeForTesting();
        createObjectForTesting();
    }

    private static void createEditionForTesting(){
        final String INSERT_EDITION =
                "insert into edition " +
                        "(ID,NAME,NAME_EN,URL_NAME,URL_MATRIAL_TYPE,URL_PUB_YEAR,URL_PUB_MONTH,URL_COLLECTION,CUMULUS_CATALOG,CUMULUS_TOP_CATAGORY,NORMALISATIONRULE,STATUS,UI_LANGUAGE,UI_SORT,UI_SHOW,OPML,DESCRIPTION,DESCRIPTION_EN,COLLECTION_DA,COLLECTION_EN,DEPARTMENT_DA,DEPARTMENT_EN,CONTACT_EMAIL,LAST_MODIFIED,VISIBLE_TO_PUBLIC,LOG) " +
                        "values ('/test/test/test','NAME','NAME_EN','URL_NAME','URL_MATERIAL_TYPE',2022,'URL_PUB_MONTH','URL_COLLECTION','CUMULUS_CATALOG','CUMULUS_TOP_CATEGORY','NORMALISATIONRULE','STATUS','UI_LANGUAGE','UI_SORT','UI_SHOW','OPML','DESCRIPTION','DESCRIPTION_EN','COLLECTION_DA','COLLECTION_EN','DEPARTMENT_DA','DEPARTMENT_EN','CONTACT_EMAIL','1923-05-25',1,'LOG')";
        logger.info("Writing an object ...");
        Connection conn = Database.getConnection();
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(INSERT_EDITION);
            int wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);
            conn.close();
            logger.info("Test edition is added");

        }catch (SQLException sqlException) {
            logger.error(sqlException.getMessage(), sqlException);
        }

    }

    private static void createTypeForTesting(){
        final String INSERT_TYPE =
                "insert into type " +
                        "(ID,TYPE_TEXT) " +
                        "values (100,'test')";
        logger.info("Writing a type ...");
        Connection conn = Database.getConnection();
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(INSERT_TYPE);
            int wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);
            conn.close();
            logger.info("Test type is added");

        }catch (SQLException sqlException) {
            logger.error(sqlException.getMessage(), sqlException);
        }

    }

    private static void createObjectForTesting(){
        final String INSERT_OBJECT =
                "insert into object " +
                        "(ID,TYPE_ID,EID,MODS,LAST_MODIFIED,DELETED,LAST_MODIFIED_BY,OBJ_VERSION,POINT,TITLE,CREATOR,BOOKMARK,LIKES,CORRECTNESS,RANDOM_NUMBER,INTERESTINGESS,PERSON,BUILDING,LOCATION,NOT_BEFORE,NOT_AFTER) " +
                        "values ('/test/test/test/objectxxxxx',100,'/test/test/test','MODS',1411596124439,'n','test',1,POINT(0, 0)::geometry,'TITLE','CREATOR',0,0,0,0,1,'PERSON','BUILDING','LOCATION','1923-05-25','1923-05-25')";
        logger.info("Writing an object ...");
        Connection conn = Database.getConnection();
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(INSERT_OBJECT);
            int wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);
            conn.close();
            logger.info("Test object is added");

        }catch (SQLException sqlException) {
            logger.error(sqlException.getMessage(), sqlException);
        }

    }

    @Test
    public void testWriteXlink() {
        DbWriter dbWriter = new DbWriter();
        DbReader dbReader = new DbReader();

        // Adding a new xlink to xlink database
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

        // Check if the xlink is in the table
        ArrayList<Xlink> xLinks = dbReader.readXlinks(xLink.getId(), true);
        System.out.println(xLinks);
        assertEquals(1, xLinks.size());

        // Cleaning up afterwards
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("xlink"), xLink.getId());
    }

    @Test
    public void testWriteAerialTag(){ // TODO add creator and timestamp to tag_join table and test again
        DbWriter dbWriter = new DbWriter();
        DbReader dbReader = new DbReader();

        // Adding a new aerial tag to tag database
        Tag tag = new Tag();
        tag.setId("tag1");
        Content content = new Content();
        content.setType("test");
        content.setValue("Value1");
        tag.setContent(content);
        tag.setLink("/test/test/test");
        tag.setCreator("Test");
        Calendar calendar2 = Calendar.getInstance();
        tag.setUpdated(calendar2);
        dbWriter.writeAerialTag(tag);

        // Check if the aerial tag is in the table
        ArrayList<Tag> tags = dbReader.readAerialTags(tag.getId(), true);
        System.out.println(tags);
        assertEquals(1, tags.size());

        // Cleaning up afterwards
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("tag_aerial"), tag.getId());
    }

    @Test
    public void testWriteTag(){
        DbWriter dbWriter = new DbWriter();
        DbReader dbReader = new DbReader();

        // Adding a new tag to tag database
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
        dbWriter.writeTag(tag);

        // Check if the tag is in the table
        ArrayList<Tag> tags = dbReader.readTags(tag.getId(), true);
        System.out.println(tags);
        assertEquals(1, tags.size());

        // Cleaning up afterwards
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("tag"), tag.getId());
    }

    @Test
    public void testWriteComment(){
        DbWriter dbWriter = new DbWriter();
        DbReader dbReader = new DbReader();

        // Adding a new comment to comment database
        Comment comment = new Comment();
        comment.setId("comment1");
        Content content = new Content();
        content.setType("text");
        content.setValue("Value1");
        comment.setContent(content);
        comment.setUpdated(Calendar.getInstance());
        comment.setLink("/test/test/test/objectxxxxx");
        comment.setCreator("test");
        comment.setHostUri("http://cop-02.kb.dk:8080//annotation/comment");
        dbWriter.writeComment(comment);

        // Check if the comment is in the table
        ArrayList<Comment> comments = dbReader.readComments(comment.getId(), true);
        System.out.println(comments);
        assertEquals(1, comments.size());

        // Cleaning up afterwards
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("comment"), comment.getId());
    }

    @AfterClass
    public static void removeTestRecords(){
        final String DELETE_EDITION =
                "delete from edition where ID='/test/test/test'";
        final String DELETE_TYPE =
                "delete from type where ID=100";
        final String DELETE_OBJECT =
                "delete from object where ID='/test/test/test/objectxxxxx'";

        Connection conn = Database.getConnection();
        PreparedStatement stmt;
        try {
            logger.info("Deleting test object ...");
            stmt = conn.prepareStatement(DELETE_OBJECT);
            int wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);
            logger.info("Test object is deleted");

            logger.info("Deleting test type ...");
            stmt = conn.prepareStatement(DELETE_TYPE);
            wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);
            logger.info("Test type is deleted");

            logger.info("Deleting test edition ...");
            stmt = conn.prepareStatement(DELETE_EDITION);
            wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);
            logger.info("Test edition is deleted");

            conn.close();
        }catch (SQLException sqlException) {
            logger.error(sqlException.getMessage(), sqlException);
        }

    }

}
