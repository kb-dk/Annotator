package dk.kb.annotator.database;

import dk.kb.annotator.api.ApiUtils;
import dk.kb.annotator.config.ServiceConfig;
import dk.kb.annotator.model.*;
import org.apache.log4j.Logger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbWriterTest {

    private static final Logger logger = Logger.getLogger(DbWriterTest.class);
    private static final DbWriter dbWriter = new DbWriter();
    private static final DbReader dbReader = new DbReader();

    @BeforeAll
    public static void setupDatabase() throws FileNotFoundException {
        ServiceConfig.initialize(new FileInputStream("src/test/resources/annotator.properties"));
        createEditionForTesting();
        createTypeForTesting();
        createObjectForTesting();
    }

    public static void createEditionForTesting(){
        final String INSERT_EDITION =
                "insert into edition " +
                        "(ID,NAME,NAME_EN,URL_NAME,URL_MATRIAL_TYPE,URL_PUB_YEAR,URL_PUB_MONTH,URL_COLLECTION,CUMULUS_CATALOG,CUMULUS_TOP_CATAGORY,NORMALISATIONRULE,STATUS,UI_LANGUAGE,UI_SORT,UI_SHOW,OPML,DESCRIPTION,DESCRIPTION_EN,COLLECTION_DA,COLLECTION_EN,DEPARTMENT_DA,DEPARTMENT_EN,CONTACT_EMAIL,LAST_MODIFIED,VISIBLE_TO_PUBLIC,LOG) " +
                        "values ('/test/test/test','NAME','NAME_EN','URL_NAME','URL_MATERIAL_TYPE',2022,'URL_PUB_MONTH','URL_COLLECTION','CUMULUS_CATALOG','CUMULUS_TOP_CATEGORY','NORMALISATIONRULE','STATUS','UI_LANGUAGE','UI_SORT','UI_SHOW','OPML','DESCRIPTION','DESCRIPTION_EN','COLLECTION_DA','COLLECTION_EN','DEPARTMENT_DA','DEPARTMENT_EN','CONTACT_EMAIL','1923-05-25',1,'LOG')";
        logger.info("Writing an edition ...");
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

    public static void createTypeForTesting(){
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

    public static void createObjectForTesting(){
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
    public void testWriteAndDeleteXlink() {
        Xlink xLink = addATestXlinkToDB();
        checkIfXlinkExists(xLink.getId());
        deleteTheXlink(xLink.getId());
        checkIfXlinkIsDeleted(xLink.getId());
    }

    public static Xlink addATestXlinkToDB() {
        Xlink xLink = new Xlink("xlink1", "isPartOf", "title1", "simple", Calendar.getInstance(), "/test/test/test", "/test/test/test", "Test" );
        dbWriter.writeXlink(xLink);
        return xLink;
    }

    private void checkIfXlinkExists(String id) {
        ArrayList<Xlink> xLinks = dbReader.readXlinks(id, true);
        System.out.println(xLinks);
        assertEquals(1, xLinks.size());
    }

    public static void deleteTheXlink(String id) {
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("xlink"), id);
    }

    private void checkIfXlinkIsDeleted(String id) {
        ArrayList<Xlink> xLinks = dbReader.readXlinks(id, true);
        System.out.println(xLinks);
        assertEquals(0, xLinks.size());
    }

    @Test
    public void testWriteAndDeleteAerialTag(){
        Tag tag = addATestAerialTagToDB();
        checkIfAerialTagExists(tag.getId());
        deleteTheAerialTag(tag.getId());
        checkIfAerialTagIsDeleted(tag.getId());
    }

    public static Tag addATestAerialTagToDB() {
        Tag tag = new Tag("tag1", "contentXXXXXXX", Calendar.getInstance(), "/test/test/test/objectxxxxx", "Test");
        dbWriter.writeAerialTag(tag);
        return tag;
    }

    private void checkIfAerialTagExists(String id) {
        ArrayList<Tag> tags = dbReader.readAerialTags(id, true);
        System.out.println(tags);
        assertEquals(1, tags.size());
    }

    public static void deleteTheAerialTag(String id) {
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("tag_aerial"), id);
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("tag"), id);
    }

    private void checkIfAerialTagIsDeleted(String id) {
        ArrayList<Tag> tags = dbReader.readAerialTags(id, true);
        System.out.println(tags);
        assertEquals(0, tags.size());
    }

    @Test
    public void testWriteAndDeleteTag(){
        Tag tag = addATestTagToDB();
        checkIfTagExists(tag.getId());
        deleteTheTag(tag.getId());
        checkIfTagIsDeleted(tag.getId());
    }

    public static Tag addATestTagToDB() {
        Tag tag = new Tag("tag1", "contentXXXXXXX", Calendar.getInstance(), "/test/test/test/objectxxxxx", "Test");
        dbWriter.writeTag(tag);
        return tag;
    }

    private void checkIfTagExists(String id) {
        ArrayList<Tag> tags = dbReader.readTags(id, true);
        System.out.println(tags);
        assertEquals(1, tags.size());
    }

    public static void deleteTheTag(String id) {
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("tag"), id);
    }

    private void checkIfTagIsDeleted(String id) {
        ArrayList<Tag> tags = dbReader.readTags(id, true);
        System.out.println(tags);
        assertEquals(0, tags.size());
    }

    @Test
    public void testWriteAndDeleteComment(){
        Comment comment = addATestCommentToDB();
        checkIfCommentExists(comment.getId());
        deleteTheComment(comment.getId());
        checkIfCommentIsDeleted(comment.getId());
    }

    public static Comment addATestCommentToDB() {
        Comment comment = new Comment("comment1", "contentXXXXXXX", Calendar.getInstance(), "/test/test/test/objectxxxxx", "test", "http://cop-02.kb.dk:8080//annotation/comment");
        dbWriter.writeComment(comment);
        return comment;
    }

    private void checkIfCommentExists(String id) {
        ArrayList<Comment> comments = dbReader.readComments(id, true);
        System.out.println(comments);
        assertEquals(1, comments.size());
    }

    public static void deleteTheComment(String id) {
        dbWriter.deleteAnnotation(ApiUtils.annotationType.valueOf("comment"), id);
    }

    private void checkIfCommentIsDeleted(String id) {
        ArrayList<Comment> comments = dbReader.readComments(id, true);
        System.out.println(comments);
        assertEquals(0, comments.size());
    }

    @AfterAll
    public static void removeTestRecords(){
        final String DELETE_EDITION =
                "delete from edition where ID='/test/test/test'";
        final String DELETE_TYPE =
                "delete from type where ID=100";
        final String DELETE_OBJECT =
                "delete from object where ID='/test/test/test/objectxxxxx'";
        final String DELETE_OBJECT_TAG =
                "delete from tag_join where oid='/test/test/test/objectxxxxx'";

        Connection conn = Database.getConnection();
        PreparedStatement stmt;
        try {
            logger.info("Deleting test all the tag_join entries with object id ...");
            stmt = conn.prepareStatement(DELETE_OBJECT_TAG);
            int wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);
            logger.info("Test tag-object relations are deleted");

            logger.info("Deleting test object ...");
            stmt = conn.prepareStatement(DELETE_OBJECT);
            wasExecuted = stmt.executeUpdate();
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
