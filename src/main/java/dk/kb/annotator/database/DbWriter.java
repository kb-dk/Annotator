package dk.kb.annotator.database;

import dk.kb.annotator.model.Annotation;
import dk.kb.annotator.model.Comment;
import dk.kb.annotator.model.Tag;
import dk.kb.annotator.model.Xlink;

/**
 * A class that writes to the database
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 */

//todo: slip connection tilbae til pool efter hver sql

public class DbWriter {

    private static org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(DbWriter.class);

    private java.sql.Connection conn = null;

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

    //
    // End of canned SQL
    //

    /**
     * The constructor gets a connection from the pool
     */
    public DbWriter() {
        this.conn = Database.getConnection();
    }

    public void dbClose() {
        try {
            if (this.conn != null) {
                this.conn.close();
                this.conn = null;
            }
        } catch (java.sql.SQLException sqlException) {
            logger.warn(sqlException.getMessage());
        }
    }

    public Xlink writeXlink(Xlink x) {

        java.sql.PreparedStatement stmt = null;
        try {
            if (this.exists("xlink", x)) {
                stmt = conn.prepareStatement(UPDATE_XLINK);
                stmt.setString(1, x.getLinkFrom().getHref());
                stmt.setString(2, x.getLinkTo().getHref());
                stmt.setString(3, x.getType());
                stmt.setString(4, x.getRole().getLabel());
                stmt.setString(5, x.getTitle());
                stmt.setString(6, x.getCreator()[0]);
                stmt.setTimestamp(7, new java.sql.Timestamp(x.getUpdated().getTimeInMillis()));
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
                stmt.setTimestamp(8, new java.sql.Timestamp(x.getUpdated().getTimeInMillis()));
            }
        } catch (java.sql.SQLException sqlException) {
            logger.warn(sqlException.getMessage());
            return null;
        }

        if (this.execute(stmt)) {
            logger.debug("Saved xlink " + x.toString());

            return x;
        } else {
            return null;
        }
    }

    public Tag writeAerialTag(Tag t) {
        String existingId = semanticExists(t);
        if (existingId != null && existingId.length() > 0) {    // do a insert in the join table
            logger.debug("Tag already exist! Create a relation to that tag. Tag id:" +existingId );
            java.sql.PreparedStatement stmt = null;

                try {
                    stmt = conn.prepareStatement(INSERT_TAG_JOIN);
                    stmt.setString(1, t.getLink());
                    stmt.setString(2, existingId);
                    stmt.setString(3, t.getCreator()[0]);

                } catch (java.sql.SQLException sqlException) {
                    logger.warn(sqlException.getMessage());
                    return null;
                }
                if (this.execute(stmt)) {
                    logger.debug("Saved aerial join_tag tag " + t.toString());

                    return t;
                } else {
                    return null;
                }

        } else {    // existing tag doesn't exist.
            if (writeTag(t) != null) {    // write the tag
                // create a relation between a copject to the recently inserted tag table.
                java.sql.PreparedStatement stmt = null;
                try {
                    stmt = conn.prepareStatement(INSERT_TAG_JOIN);
                    stmt.setString(1, t.getLink());
                    stmt.setString(2, t.getId());
                    stmt.setString(3, t.getCreator()[0]);

                } catch (java.sql.SQLException sqlException) {
                    logger.warn(sqlException.getMessage());
                    return null;
                }

                if (this.execute(stmt)) {
                    logger.debug("Saved aerial join_tag tag " + t.toString());

                    return t;
                } else {
                    return null;
                }
            }
        }
        return null;


    }

    public Tag writeTag(Tag t) {

        java.sql.PreparedStatement stmt = null;
        try {
            if (this.exists("tag", t)) {
                stmt = conn.prepareStatement(UPDATE_TAG);
                stmt.setString(1, t.getLink());
                stmt.setString(2, t.getCreator()[0]);
                stmt.setTimestamp(3, new java.sql.Timestamp(t.getUpdated().getTimeInMillis()));
                stmt.setString(4, t.getTagText());
                stmt.setString(5, t.getId());
            } else {
                stmt = conn.prepareStatement(INSERT_TAG);
                stmt.setString(1, t.getId());
                stmt.setString(2, t.getLink());
                stmt.setString(3, t.getCreator()[0]);
                stmt.setTimestamp(4, new java.sql.Timestamp(t.getUpdated().getTimeInMillis()));
                stmt.setString(5, t.getTagText());
            }
        } catch (java.sql.SQLException sqlException) {
            logger.warn(sqlException.getMessage());
            return null;
        }

        if (this.execute(stmt)) {
            logger.debug("Saved tag " + t.toString());

            return t;
        } else {
            return null;
        }
    }

    public Comment writeComment(Comment c) {
        // todo slu write host uri a new field.

        java.sql.PreparedStatement stmt = null;
        try {

            if (this.exists("comments", c)) {
                stmt = conn.prepareStatement(UPDATE_COMMENT);
                stmt.setString(1, c.getLink().getHref());
                stmt.setString(2, c.getCreator()[0]);
                stmt.setTimestamp(3, new java.sql.Timestamp(c.getUpdated().getTimeInMillis()));
                stmt.setString(4, c.getContent().getValue());
                stmt.setString(5, c.getHostUri());
                stmt.setString(6, c.getId());
            } else {
                stmt = conn.prepareStatement(INSERT_COMMENT);
                stmt.setString(1, c.getId());
                stmt.setString(2, c.getLink().getHref());
                stmt.setString(3, c.getCreator()[0]);
                stmt.setTimestamp(4, new java.sql.Timestamp(c.getUpdated().getTimeInMillis()));
                stmt.setString(5, c.getContent().getValue());
                stmt.setString(6, c.getHostUri());
            }
        } catch (java.sql.SQLException sqlException) {
            logger.warn(sqlException.getMessage());
            return null;
        }

        if (this.execute(stmt)) {
            logger.debug("Saved comment " + c.toString());
            return c;
        } else {
            return null;
        }
    }

    private boolean execute(java.sql.PreparedStatement stmt) {

        try {
            stmt.execute();
            stmt.close();
        } catch (java.sql.SQLException sqlException) {
            logger.warn(sqlException.getMessage());
            return false;
        }

        try {
            this.conn.commit();
        } catch (java.sql.SQLException sqlException) {
            logger.warn(sqlException.getMessage());
            return false;
        }
        return true;
    }

    private boolean exists(String table, Annotation a) {


        if (a.getId() == null || a.getId() == "") {
            a.setId(java.util.UUID.randomUUID().toString());
            return false;
        }

        boolean found = false;
        String check_sql = "select count(*) as hits from " + table + " where id=?";
        try {
            java.sql.PreparedStatement stmt = conn.prepareStatement(check_sql);
            stmt.setString(1, a.getId());
            found = stmt.executeQuery().next();
            stmt.close();
        } catch (java.sql.SQLException sqlException) {
            logger.warn(sqlException.getMessage());
        }

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
            java.sql.PreparedStatement stmt = conn.prepareStatement(check_sql);
            //stmt.setString(1, tag.getLink());
            stmt.setString(1, "%/images/billed/2011/aug/billeder/%"); //'%/images/billed/2011/aug/billeder/%'
            stmt.setString(2, "%" + tag.getTagText() + "%");
            //preparedStatement.setString(2, "%Module=jvmRuntimeModule:freeMemory%");
            //found = stmt.executeQuery().next();
            //stmt.close();


            java.util.ArrayList<Tag> tList = null;

            java.sql.ResultSet result = stmt.executeQuery();
            if (result != null) {

                tList = new java.util.ArrayList<Tag>();
                try {
                    while (result.next()) {
                        java.util.Calendar time = java.util.Calendar.getInstance();
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
                } catch (java.sql.SQLException sqlException) {
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
                logger.debug("Tag semantics doesnt exist!");
                return null;
            }


        } catch (java.sql.SQLException sqlException) {
            logger.warn(sqlException.getMessage());
        }

        return null;

    }

}
