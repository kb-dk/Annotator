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
 * A class that Reads from the database
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 */

public class DbReader {

    private static Logger logger = Logger.getLogger(DbWriter.class);

    private static final String SELECT_FROM = "SELECT * FROM ";

    private static final String WHERE_ID_IS = " WHERE id = ? ORDER BY TIMESTAMP ASC ";

    private static final String WHERE_URI = " WHERE XLINK_TO=? ORDER BY TIMESTAMP ASC ";

    private static final String WHERE_URI_MODIFIED_SINCE = " WHERE XLINK_TO=? AND TIMESTAMP>=? " +
            " ORDER BY TIMESTAMP DESC  ";

    private static final String WHERE_XLINK_URI = " WHERE XLINK_FROM=? ORDER BY TIMESTAMP ASC ";

    private static final String WHERE_XLINK_URI_MODIFIED_SINCE = " WHERE XLINK_FROM=? AND TIMESTAMP>=? " +
            " ORDER BY TIMESTAMP DESC  ";

    public DbReader() {

    }

    public java.util.ArrayList<Xlink> readXlinks(String uri, boolean getById) {
        return readXlinks(null, getById, uri);
    }

    public ArrayList<Xlink> readXlinks(Calendar createdBefore, boolean getById, String uri) {

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

                if (getById) {
                  sql = SELECT_FROM + "XLINK" + WHERE_ID_IS;
                  stmt = conn.prepareStatement(sql);
                  stmt.setString(1,uri);
                } else if (createdBefore != null) {
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


    public ArrayList<Tag> readTags(String uri, boolean getById) {
        return readTags(null, getById, uri);
    }

    /**
     * Read data from the TAGS table
     *
     * @param createdBefore
     * @param uri
     * @return
     */
    public ArrayList<Tag> readTags(Calendar createdBefore, boolean getById, String uri) {

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
                if (getById) {
                    sql = SELECT_FROM + "TAG" + WHERE_ID_IS;
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1,uri);
                } else if (createdBefore != null) {
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

    public ArrayList<Tag> readAerialTags(String uri, boolean getById) {
        logger.debug("reading tags uri "  + uri );
        Connection conn = null;
        ArrayList<Tag> tList = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        String sql;

        if (uri == null || uri.equals("")) {
            logger.warn("No uri provided");
            return null;
        } else {
            try {
                conn = Database.getConnection();

                if(getById){     // SELECT a Single tag
                    sql = "SELECT * FROM TAG, TAG_JOIN WHERE tag_join.tid LIKE ? AND TAG_JOIN.TID=TAG.ID"; // '/images/luftfo/2011/maj/luftfoto/object77541'
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1,"%" +uri + "%");
                    logger.debug("Getting A SPECIFIC TAG");
                }else{
                    //sql = "SELECT * FROM TAG_JOIN, TAG WHERE tag_join.oid='/images/luftfo/2011/maj/luftfoto/object77541'  AND TAG_JOIN.TID=TAG.ID"; // '/images/luftfo/2011/maj/luftfoto/object77541'
                    sql = "SELECT * FROM TAG_JOIN, TAG WHERE tag_join.oid LIKE ?  AND TAG_JOIN.TID=TAG.ID"; // '/images/luftfo/2011/maj/luftfoto/object77541'
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1,"%" +uri + "%");
                }
                resultSet = stmt.executeQuery();

                if (resultSet != null) {
                   // logger.info( resultSet.getFetchSize());


                    tList = new ArrayList<Tag>();
                    while (resultSet.next()) {
                        //logger.info( resultSet.getFetchSize() +" row id " +  resultSet.getRow());

                        // logger.info( "TID " + resultSet.getString("TID"));

                        Calendar time = Calendar.getInstance();
                        //time.setTimeInMillis(resultSet.getTimestamp("TIMESTAMP").getTime());
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
    public ArrayList<Comment> readComments(Calendar createdBefore, boolean getById, String uri) {

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
                if (getById) {
                    sql = SELECT_FROM + "COMMENTS" + WHERE_ID_IS;
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1,uri);
                } else if (createdBefore != null) {
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

    public ArrayList<Comment> readComments(String uri, boolean getById) {
        return readComments(null, getById, uri);
    }


    public String getObjectIdFromTagId(String tag_id) {
        PreparedStatement stmt = null;
        ResultSet result = null;
        Connection conn = null;
        String sql = "SELECT * FROM tag_join WHERE tid = ?";
        String oid = null;
        try {
            conn = Database.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1,tag_id);
            result = stmt.executeQuery();
            if (result.next()) {
              oid = result.getString("OID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null){
                    result.close();
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
        return oid;
    }

    public String getObjectIdFromCommentId(String comment_id) {
        PreparedStatement stmt = null;
        ResultSet result = null;
        Connection conn = null;
        String sql = "SELECT * FROM comments WHERE id = ?";
        String oid = null;
        try {
            conn = Database.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1,comment_id);
            result = stmt.executeQuery();
            if (result.next()) {
                oid = result.getString("XLINK_TO");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null){
                    result.close();
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
        return oid;
    }
}
