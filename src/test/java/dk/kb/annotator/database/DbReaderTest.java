package dk.kb.annotator.database;

import dk.kb.annotator.api.ApiUtils;
import dk.kb.annotator.config.ServiceConfig;
import dk.kb.annotator.model.*;

import org.apache.log4j.Logger;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;


public class DbReaderTest {

    private static final Logger logger = Logger.getLogger(DbReaderTest.class);

    @BeforeClass
    public static void setupDatabase() throws FileNotFoundException {
        ServiceConfig.initialize(new FileInputStream("src/test/resources/annotator.properties"));

        createEditionForTesting();
        createTypeForTesting();
        createObjectForTesting();

        addAnXLinkForTesting();
        addATagForTesting();
        addACommentForTesting();
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

    public static void addACommentForTesting() {
        DbWriter dbWriter = new DbWriter();
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
    }

    public static void addATagForTesting() {
        DbWriter dbWriter = new DbWriter();
        Tag tag = new Tag();
        tag.setId("tag1");
        Content content = new Content();
        content.setType("text");
        content.setValue("Value1");
        tag.setContent(content);
        tag.setLink("/test/test/test/objectxxxxx");
        tag.setCreator("Test");
        Calendar calendar = Calendar.getInstance();
        tag.setUpdated(calendar);

        dbWriter.writeAerialTag(tag);
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
        linkTo.setHref("/test/test/test/objectxxxxx");
        xLink.setLinkTo(linkTo);
        Link linkFrom = new Link();
        linkFrom.setHref("/test/test/test/objectxxxxx");
        xLink.setLinkFrom(linkFrom);
        xLink.setType("simple");
        Calendar calendar = Calendar.getInstance();
        xLink.setUpdated(calendar);
        xLink.setCreator("test");

        dbWriter.writeXlink(xLink);
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
        List<Xlink> result = dbReader.readXlinks("/test/test/test/objectxxxxx",false);
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
        List<Tag> result = dbReader.readTags(calendar, false, "/test/test/test/objectxxxxx");
        System.out.println("From testGetTagByCreatedBefore" + calendar + ":" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetCommentsByCreatedBefore() {
        DbReader dbReader = new DbReader();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        List<Comment> result = dbReader.readComments(calendar, false, "/test/test/test/objectxxxxx");
        System.out.println("From testGetCommentsByCreatedBefore:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetCommentsById() {
        DbReader dbReader = new DbReader();
        List<Comment> result = dbReader.readComments("comment1", true);
        System.out.println("From testGetCommentsById:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetCommentsByUri() {
        DbReader dbReader = new DbReader();
        List<Comment> result = dbReader.readComments("/test/test/test/objectxxxxx", false);
        System.out.println("From testGetCommentsByUri:" + result);
        assertTrue(result.size() >= 1);
    }

    @Test
    public void testGetObjectIdFromTagId() { // TODO add creator and timestamp to tag_join table and test again
        DbReader dbReader = new DbReader();
        String result = dbReader.getObjectIdFromTagId("tag1");
        System.out.println("From testGetObjectIdFromTagId:" + result);
        assertEquals("/test/test/test/objectxxxxx", result);
    }

    @Test
    public void testGetObjectIdFromCommentId() {
        DbReader dbReader = new DbReader();
        String result = dbReader.getObjectIdFromCommentId("comment1");
        System.out.println("From testGetObjectIdFromCommentId:" + result);
        assertEquals("/test/test/test/objectxxxxx", result);
    }

    @AfterClass
    public static void removeAllTestRecords() {
        DbWriter dbWriter = new DbWriter();
        System.out.println("");
        logger.info("Cleaning up ...");

        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("xlink"), "xlink1");
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("tag"), "tag1");
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("comment"), "comment1");

        removeTestRecords();
    }

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
