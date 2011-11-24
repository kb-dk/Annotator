package dk.kb.annotator.database;

import dk.kb.annotator.api.ApiUtils;
import dk.kb.annotator.model.Annotation;
import dk.kb.annotator.model.Comment;
import dk.kb.annotator.model.Tag;
import dk.kb.annotator.model.Xlink;
import org.apache.log4j.Logger;
import org.apache.taglibs.standard.tag.common.core.CatchTag;

import javax.servlet.jsp.tagext.TryCatchFinally;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A class that writes to the database
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 */

//todo: slip connection tilbae til pool efter hver sql

public class DbWriter {

    private static Logger logger = Logger.getLogger(DbWriter.class);
    //
    // Canned SQL. No fun!
    //

    private static final String INSERT_XLINK =
            "insert into xlink " +
                    "(ID,XLINK_FROM,XLINK_TO,XLINK_TYPE,XLINK_ROLE,XLINK_TITLE,CREATOR,TIMESTAMP) " +
                    "values (?,?,?,?,?,?,?,?)";

    private static final String UPDATE_XLINK =
            "update XLINK set " +
                    " XLINK_FROM=? ,XLINK_TO=? ,XLINK_TYPE=? ,XLINK_ROLE=? ,XLINK_TITLE=?,CREATOR=? ,TIMESTAMP=?  " +
                    "where ID= ? ";

    private static final String INSERT_COMMENT =
            "insert into comments " +
                    "(ID,XLINK_TO,CREATOR,TIMESTAMP,COMMENT_TEXT,HOST_URI) " +
                    "values (?,?,?,?,?,?)";

    private static final String UPDATE_COMMENT =
            "update comments set " +
                    " XLINK_TO=?,CREATOR=?,TIMESTAMP=?,COMMENT_TEXT=?,HOST_URI=? " +
                    "where ID=?";

    private static final String INSERT_TAG =
            "insert into tag " +
                    "(ID,XLINK_TO,CREATOR,TIMESTAMP,TAG_VALUE) " +
                    "values (?,?,?,?,?)";

      private static final String INSERT_TAG_JOIN =
            "insert into tag_join " +
                    "(OID, TID, id) " +
                    "values (?,?,?)";

    private static final String UPDATE_TAG =
            "update tag set " +
                    " XLINK_TO=?,CREATOR=?,TIMESTAMP=?,TAG_VALUE=? " +
                    "where ID=?";

    // For deletion
    private static final String DELETE_COMMENT =
            "delete from comments where ID=?";
    private static final String DELETE_TAG =
            "delete from tag where ID=?";
    private static final String DELETE_XLINK =
            "delete from xlink where ID=?";


    // End of canned SQL

    /**
     * The constructor gets a connection from the pool
     */
    public DbWriter() {
    }

    public Xlink writeXlink(Xlink x) {

        logger.info("Writing an XLink...");
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = Database.getConnection();
            if (this.exists("xlink", x)) {
                stmt = conn.prepareStatement(UPDATE_XLINK);
                stmt.setString(1, x.getLinkFrom().getHref());
                stmt.setString(2, x.getLinkTo().getHref());
                stmt.setString(3, x.getType());
                stmt.setString(4, x.getRole().getLabel());
                stmt.setString(5, x.getTitle());
                stmt.setString(6, x.getCreator()[0]);
                stmt.setTimestamp(7, new Timestamp(x.getUpdated().getTimeInMillis()));
                stmt.setString(8, x.getId());
            } else {
                stmt = conn.prepareStatement(INSERT_XLINK);
                stmt.setString(1, x.getId());
                stmt.setString(2, x.getLinkFrom().getHref());
                stmt.setString(3, x.getLinkTo().getHref());
                stmt.setString(4, x.getType());
                stmt.setString(5, x.getRole().getLabel());
                stmt.setString(6, x.getTitle());
                stmt.setString(7, x.getCreator()[0]);
                stmt.setTimestamp(8, new Timestamp(x.getUpdated().getTimeInMillis()));
            }

            int wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);
            if (wasExecuted > 0) {
                logger.debug("Saved xlink " + x.toString());
            } else {
                x = null;
            }

        } catch (SQLException sqlException) {
            logger.warn(sqlException.getMessage());
            x = null;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.warn(e);
            }
        }
        logger.info("Finished writing an XLink...");
        return x;
    }

    public Tag writeAerialTag(Tag t) {

        logger.info("Writing an aerial tag...");
        String existingId = semanticExists(t);
        if (existingId != null && existingId.length() > 0) {
            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = Database.getConnection();
                stmt = conn.prepareStatement(INSERT_TAG_JOIN);
                stmt.setString(1, t.getLink());
                stmt.setString(2, existingId);
                stmt.setString(3, t.getCreator()[0]);

                int wasExecuted = stmt.executeUpdate();
                logger.info("wasExecuted = " + wasExecuted);

                if (wasExecuted > 0) {
                    logger.debug("Saved aerial join_tag tag " + t.toString());
                }

            } catch (SQLException sqlException) {
                logger.warn(sqlException.getMessage());
                t = null;
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }

                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    logger.warn(e);
                }
            }
        } else if (writeTag(t) != null){ // existing tag doesn't exist.
            // create a relation between a copject to the recently inserted tag table.
            PreparedStatement stmt = null;
            Connection conn = null;

            try {
                conn = Database.getConnection();
                stmt = conn.prepareStatement(INSERT_TAG_JOIN);
                stmt.setString(1, t.getLink());
                stmt.setString(2, t.getId());
                stmt.setString(3, t.getCreator()[0]);
                int wasExecuted = stmt.executeUpdate();

                if (wasExecuted > 0) {
                    logger.debug("Saved aerial join_tag tag " + t.toString());
                } else {
                    t = null;
                }
            } catch (SQLException sqlException) {
                logger.warn(sqlException.getMessage());
                return null;
            } finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        logger.warn(e);
                    }
                }

                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        logger.warn(e);
                    }
                }
            }
        }

        logger.info("Finished writing an aerial tag...");
        return t;
    }

    public Tag writeTag(Tag t) {

        logger.info("Writing a tag...");
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Database.getConnection();
            if (this.exists("tag", t)) {
                stmt = conn.prepareStatement(UPDATE_TAG);
                stmt.setString(1, t.getLink());
                stmt.setString(2, t.getCreator()[0]);
                stmt.setTimestamp(3, new Timestamp(t.getUpdated().getTimeInMillis()));
                stmt.setString(4, t.getTagText());
                stmt.setString(5, t.getId());
            } else {
                stmt = conn.prepareStatement(INSERT_TAG);
                stmt.setString(1, t.getId());
                stmt.setString(2, t.getLink());
                stmt.setString(3, t.getCreator()[0]);
                stmt.setTimestamp(4, new Timestamp(t.getUpdated().getTimeInMillis()));
                stmt.setString(5, t.getTagText());
            }
            int wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);

            if (wasExecuted > 0) {
                logger.debug("Saved tag " + t.toString());
            } else {
                t = null;
            }
        } catch (SQLException sqlException) {
            logger.warn(sqlException);
            t = null;
        }
        finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.warn(e);
            }
        }

        logger.info("Finished writing a tag...");
        return t;
    }

    public Comment writeComment(Comment c) {
        // todo slu write host uri a new field.

        logger.info("Writing a comment...");
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Database.getConnection();
            if (this.exists("comments", c)) {
                logger.info("Updating existing comment...");
                stmt = conn.prepareStatement(UPDATE_COMMENT);
                stmt.setString(1, c.getLink().getHref());
                stmt.setString(2, c.getCreator()[0]);
                stmt.setTimestamp(3, new Timestamp(c.getUpdated().getTimeInMillis()));
                stmt.setString(4, c.getContent().getValue());
                stmt.setString(5, c.getHostUri());
                stmt.setString(6, c.getId());
            } else {
                logger.info("Adding new comment...");
                stmt = conn.prepareStatement(INSERT_COMMENT);
                stmt.setString(1, c.getId());
                stmt.setString(2, c.getLink().getHref());
                stmt.setString(3, c.getCreator()[0]);
                stmt.setTimestamp(4, new Timestamp(c.getUpdated().getTimeInMillis()));
                stmt.setString(5, c.getContent().getValue());
                stmt.setString(6, c.getHostUri());
            }

            int wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);
            if (wasExecuted > 0) {
                logger.debug("Saved comment " + c.toString());
            } else {
                c = null;
            }
        } catch (SQLException sqlException) {
            logger.warn(sqlException);
            c = null;
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                logger.warn(e);
            }
        }

        logger.info("Finished writing a comment...");
        return c;
    }

    public boolean deleteAnnotation(ApiUtils.annotationType type, String id) {

        logger.debug("Deleting "+type+" id="+id);
        PreparedStatement stmt = null;
        Connection conn = null;

        try {
            conn = Database.getConnection();
            switch (type) {
                case comment :
                    stmt = conn.prepareStatement(DELETE_COMMENT);
                    stmt.setString(1,id);
                    break;
                case tag :
                    stmt = conn.prepareStatement(DELETE_TAG);
                    stmt.setString(1,id);
                    break;
                case xlink :
                    stmt = conn.prepareStatement(DELETE_XLINK);
                    stmt.setString(1,id);
                    break;
                default :
                    logger.warn("delete annotation invalid type "+type);
                    return false;
            }
            int wasExecuted = stmt.executeUpdate();
            logger.info("wasExecuted = " + wasExecuted);
            if (wasExecuted > 0) {
                logger.debug("deleted "+type+" id="+id);
                return true;
            } else {
                logger.debug(type+" id="+id+" not deleted");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting "+type,e);
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException sql) {
                logger.warn(sql);
                return false;
            }
        }
    }

    private boolean exists(String table, Annotation a) {

        logger.info("Checking an annotation exists in table:" + table);
        if (a.getId() == null || a.getId().equals("")) {
            a.setId(java.util.UUID.randomUUID().toString());
            return false;
        }

        boolean found = false;
        String check_sql = "select count(*) as hits from " + table + " where id=?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Database.getConnection();
            stmt = conn.prepareStatement(check_sql);
            stmt.setString(1, a.getId());

            rs = stmt.executeQuery();
            found = rs.next();

        } catch (SQLException sqlException) {
            logger.warn(sqlException);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.warn(e);
            }
        }

        logger.info("found = " + found);
        return found;

    }

    /**
     * Method to find out if a tag already exist. i.e. bondegård already exist. If it does we should use the id.
     *
     * @param tag to be saved
     * @return String id
     */
    private String semanticExists(Tag tag) {

        boolean found = false;
        String check_sql = "select * from TAG where TAG.XLINK_TO LIKE ? AND TAG.tag_value LIKE ?";
        try {
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(check_sql);
            //stmt.setString(1, tag.getLink());
            stmt.setString(1, "%/images/luftfo/2011/maj/luftfoto/%"); //'%/images/billed/2011/aug/billeder/%'
            stmt.setString(2, "%" + tag.getTagText() + "%");
            //preparedStatement.setString(2, "%Module=jvmRuntimeModule:freeMemory%");
            //found = stmt.executeQuery().next();
            //stmt.close();

            ArrayList<Tag> tList = null;

            ResultSet result = stmt.executeQuery();
            if (result != null) {

                tList = new java.util.ArrayList<Tag>();
                try {
                    while (result.next()) {
                        Calendar time = Calendar.getInstance();
                        time.setTimeInMillis(result.getTimestamp("TIMESTAMP").getTime());
                        Tag existingTag = new Tag(result.getString("ID"),
                                result.getString("TAG_VALUE"),
                                time,
                                result.getString("XLINK_TO"),
                                result.getString("CREATOR"));
                        tList.add(existingTag);
                    }
                    result.close();
                    stmt.close();
                } catch (SQLException sqlException) {
                    logger.warn(sqlException.getCause().getMessage() + " msg: " + sqlException.getMessage());
                }
            }
            if (tList.size() > 1) {
                logger.warn("We received several instance of the same tag_value: " + tag.getTagText() + " for " + tag.getLink());
                return null;
            }else if (tList.size() == 1){
                logger.debug("We received one instance of the same tag_value: " + tag.getTagText() );

                return tList.get(0).getId();
            } else{
                logger.debug("Tag semantics doesn't exist!");
                return null;
            }


        } catch (SQLException sqlException) {
            logger.warn(sqlException.getMessage());
        }

        return null;

    }

}
