package dk.kb.annotator.database;

import dk.kb.annotator.model.Comment;
import dk.kb.annotator.model.Tag;
import dk.kb.annotator.model.Xlink;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A class that writes to the database
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 */

public class DbReader {

    private static Logger logger = Logger.getLogger(DbWriter.class);

    private static final String SELECT_FROM = "SELECT * FROM ";

    private static final String WHERE_URI = " WHERE XLINK_TO=? ORDER BY TIMESTAMP DESC ";

    private static final String WHERE_URI_MODIFIED_SINCE = " WHERE XLINK_TO=? AND TIMESTAMP>=? " +
            " ORDER BY TIMESTAMP DESC  ";

    private static final String WHERE_XLINK_URI = " WHERE XLINK_FROM=? ORDER BY TIMESTAMP DESC ";

    private static final String WHERE_XLINK_URI_MODIFIED_SINCE = " WHERE XLINK_FROM=? AND TIMESTAMP>=? " +
            " ORDER BY TIMESTAMP DESC  ";

    public DbReader() {

    }

    public java.util.ArrayList<Xlink> readXlinks(String uri) {
        return readXlinks(null, uri);
    }

    public ArrayList<Xlink> readXlinks(Calendar createdBefore, String uri) {

        ArrayList<Xlink> xList = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        String sql;

        try {
            if (uri.equals("")) {
                return null;
            } else {
                conn = Database.getConnection();
                if (createdBefore != null) {
                    sql = SELECT_FROM + "XLINK" + WHERE_XLINK_URI_MODIFIED_SINCE;

                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, uri);
                    stmt.setTimestamp(2, new Timestamp(createdBefore.getTimeInMillis()));
                } else {
                    sql = SELECT_FROM + "XLINK" + WHERE_XLINK_URI;

                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, uri);
                }
                logger.info(stmt.toString());

                resultSet = stmt.executeQuery();

                if (resultSet != null) {

                    xList = new ArrayList<Xlink>();
                    while (resultSet.next()) {
                        Calendar time = Calendar.getInstance();
                        time.setTimeInMillis(resultSet.getTimestamp("TIMESTAMP").getTime());
                        Xlink xlink = new Xlink(resultSet.getString("ID"),
                                resultSet.getString("XLINK_ROLE"),
                                resultSet.getString("XLINK_TITLE"),
                                resultSet.getString("XLINK_TYPE"),
                                time,
                                resultSet.getString("XLINK_TO"),
                                resultSet.getString("XLINK_FROM"),
                                resultSet.getString("CREATOR"));
                        xList.add(xlink);
                    }
                }
            }
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                logger.warn(e);
            }

            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                logger.warn(e);
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.warn(e);
            }
        }

        return xList;
    }


    public ArrayList<Tag> readTags(String uri) {
        return readTags(null, uri);
    }

    /**
     * Read data from the TAGS table
     *
     * @param createdBefore
     * @param uri
     * @return
     */
    public ArrayList<Tag> readTags(Calendar createdBefore, String uri) {

        Connection conn = null;
        ArrayList<Tag> tList = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        String sql;

        if (uri.equals("")) {
            return null;
        } else {
            try {
                conn = Database.getConnection();
                if (createdBefore != null) {
                    sql = SELECT_FROM + "TAG" + WHERE_URI_MODIFIED_SINCE;
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, uri);
                    stmt.setTimestamp(2, new Timestamp(createdBefore.getTimeInMillis()));
                } else {
                    sql = SELECT_FROM + "TAG" + WHERE_URI;
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, uri);
                }

                resultSet = stmt.executeQuery();

                if (resultSet != null) {

                    tList = new ArrayList<Tag>();
                    while (resultSet.next()) {
                        Calendar time = Calendar.getInstance();
                        time.setTimeInMillis(resultSet.getTimestamp("TIMESTAMP").getTime());
                        Tag tag = new Tag(resultSet.getString("ID"),
                                resultSet.getString("TAG_VALUE"),
                                time,
                                resultSet.getString("XLINK_TO"),
                                resultSet.getString("CREATOR"));
                        tList.add(tag);
                    }
                }
            } catch (SQLException e) {
                logger.error(e);
            } finally {
                try {
                    if (resultSet != null){
                        resultSet.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    if (stmt != null){
                        stmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    if (conn != null){
                        conn.close();
                    }
                } catch (SQLException e) {
                    logger.warn(e);
                }
            }
        }

        return tList;
    }

    /**
     * Read comments from the COMMENTS database table
     * @param createdBefore
     * @param uri
     * @return an ArrayList of Comment objects
     */
    public ArrayList<Comment> readComments(Calendar createdBefore, String uri) {

        ArrayList<Comment> cList = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        String sql;
        try {
            if (uri.equals("")) {
                return null;
            } else {
                conn = Database.getConnection();
                if (createdBefore != null) {
                    sql = SELECT_FROM + "COMMENTS" + WHERE_URI_MODIFIED_SINCE;
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, uri);
                    stmt.setTimestamp(2, new Timestamp(createdBefore.getTimeInMillis()));
                } else {
                    sql = SELECT_FROM + "COMMENTS" + WHERE_URI;
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, uri);
                }
                logger.info(stmt.toString());

                resultSet = stmt.executeQuery();

                if (resultSet != null) {
                    cList = new ArrayList<Comment>();
                    while (resultSet.next()) {
                        Calendar time = Calendar.getInstance();
                        time.setTimeInMillis(resultSet.getTimestamp("TIMESTAMP").getTime());
                        Comment comment = new Comment(resultSet.getString("ID"),
                                resultSet.getString("COMMENT_TEXT"),
                                time,
                                resultSet.getString("XLINK_TO"),
                                resultSet.getString("CREATOR"),
                                resultSet.getString("HOST_URI"));
                        cList.add(comment);
                    }
                }
            }
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
        } finally {
            try {
                if (resultSet != null){
                    resultSet.close();
                }
            } catch (SQLException e) {
                logger.warn(e);
            }

            try {
                if (stmt != null){
                    stmt.close();
                }
            } catch (SQLException e) {
                logger.warn(e);
            }

            try {
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e) {
                logger.warn(e);
            }
        }

        return cList;
    }

    public ArrayList<Comment> readComments(String uri) {
        return readComments(null, uri);
    }

}
