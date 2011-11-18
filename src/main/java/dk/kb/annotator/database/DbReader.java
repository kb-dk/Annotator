package dk.kb.annotator.database;

import dk.kb.annotator.model.Comment;
import dk.kb.annotator.model.Tag;
import dk.kb.annotator.model.Xlink;

/**
 * A class that writes to the database
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 */

public class DbReader {

    private static org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(DbWriter.class);

    private java.sql.Connection conn = null;
    private Database db = null;

    private static final String SELECT_FROM = "SELECT * FROM ";

    private static final String WHERE_URI = " WHERE XLINK_TO=? ORDER BY TIMESTAMP DESC ";

    private static final String WHERE_URI_MODIFIED_SINCE = " WHERE XLINK_TO=? AND TIMESTAMP>=? " +
            " ORDER BY TIMESTAMP DESC  ";

    private static final String WHERE_XLINK_URI = " WHERE XLINK_FROM=? ORDER BY TIMESTAMP DESC ";

    private static final String WHERE_XLINK_URI_MODIFIED_SINCE = " WHERE XLINK_FROM=? AND TIMESTAMP>=? " +
            " ORDER BY TIMESTAMP DESC  ";


    public DbReader() {
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

    public java.util.ArrayList<Xlink> readXlinks(String uri) {
        return readXlinks(null, uri);
    }

    public java.util.ArrayList<Xlink> readXlinks(java.util.Calendar createdBefore, String uri) {
        java.util.ArrayList<Xlink> xList = null;

        java.sql.ResultSet result = this.doQuery("XLINK", uri, createdBefore);
        if (result != null) {

            xList = new java.util.ArrayList<Xlink>();
            try {
                while (result.next()) {
                    java.util.Calendar time = java.util.Calendar.getInstance();
                    time.setTimeInMillis(result.getTimestamp("TIMESTAMP").getTime());
                    Xlink xlink = new Xlink(result.getString("ID"),
                            result.getString("XLINK_ROLE"),
                            result.getString("XLINK_TITLE"),
                            result.getString("XLINK_TYPE"),
                            time,
                            result.getString("XLINK_TO"),
                            result.getString("XLINK_FROM"),
                            result.getString("CREATOR"));
                    xList.add(xlink);
                }
                result.close();
            } catch (java.sql.SQLException sqlException) {
                logger.warn(sqlException.getMessage());
            }
        }
        return xList;
    }


    public java.util.ArrayList<Tag> readTags(String uri) {
        java.util.ArrayList<Tag> tList = null;
        return readTags(null, uri);
    }

    public java.util.ArrayList<Tag> readTags(java.util.Calendar createdBefore,
                                             String uri) {
        java.util.ArrayList<Tag> tList = null;

        java.sql.ResultSet result = this.doQuery("TAG", uri, createdBefore);
        if (result != null) {

            tList = new java.util.ArrayList<Tag>();
            try {
                while (result.next()) {
                    java.util.Calendar time = java.util.Calendar.getInstance();
                    time.setTimeInMillis(result.getTimestamp("TIMESTAMP").getTime());
                    Tag tag = new Tag(result.getString("ID"),
                            result.getString("TAG_VALUE"),
                            time,
                            result.getString("XLINK_TO"),
                            result.getString("CREATOR"));
                    tList.add(tag);
                }
                result.close();
            } catch (java.sql.SQLException sqlException) {
                logger.warn(sqlException.getCause().getMessage() + " msg: " + sqlException.getMessage());
            }
        }
        return tList;
    }

    public java.util.ArrayList<Comment> readComments(java.util.Calendar createdBefore,
                                                     String uri) {
        java.util.ArrayList<Comment> cList = null;
        java.sql.ResultSet result = this.doQuery("COMMENTS", uri, createdBefore);

        if (result != null) {

            cList = new java.util.ArrayList<Comment>();
            try {
                while (result.next()) {
                    java.util.Calendar time = java.util.Calendar.getInstance();
                    time.setTimeInMillis(result.getTimestamp("TIMESTAMP").getTime());
                    Comment comment = new Comment(result.getString("ID"),
                            result.getString("COMMENT_TEXT"),
                            time,
                            result.getString("XLINK_TO"),
                            result.getString("CREATOR"),
                            result.getString("HOST_URI"));
                    cList.add(comment);
                }
                result.close();
            } catch (java.sql.SQLException sqlException) {
                logger.warn(sqlException.getMessage());
            }
        }
        return cList;
    }

    public java.util.ArrayList<Comment> readComments(String uri) {
        return readComments(null, uri);
    }

    private java.sql.ResultSet doQuery(String table,
                                       String uri,
                                       java.util.Calendar createdBefore) {

        java.sql.PreparedStatement stmt = null;
        java.sql.ResultSet result = null;
        String sql = "";
        try {
            if (uri.equals("")) {
                return null;
            } else {
                if (createdBefore != null) {
                    if (table.equals("XLINK")) {
                        sql = SELECT_FROM + table + WHERE_XLINK_URI_MODIFIED_SINCE;
                    } else {
                        sql = SELECT_FROM + table + WHERE_URI_MODIFIED_SINCE;
                    }
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, uri);
                    stmt.setTimestamp(2, new java.sql.Timestamp(createdBefore.getTimeInMillis()));
                } else {
                    if (table.equals("XLINK")) {
                        sql = SELECT_FROM + table + WHERE_XLINK_URI;
                    } else {
                        sql = SELECT_FROM + table + WHERE_URI;
                    }
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, uri);
                }
                //logger.debug(stmt.toString());

                return stmt.executeQuery();
            }
        } catch (java.sql.SQLException sqlException) {
            logger.warn(sqlException.getMessage());
        }
        return null;
    }

}
