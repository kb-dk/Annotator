package dk.kb.annotator.api;

//Log imports

import dk.kb.annotator.database.DbReader;
import dk.kb.annotator.database.DbWriter;
import dk.kb.annotator.model.Annotation;
import dk.kb.annotator.model.AtomFeed;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

// Jersey imports
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
// Java core imports
// Local imports


/**
 * Class that implements the API described in the README file of the project
 * Uses Jersey REST framework. (http://jersey.java.net/)
 */
// "annotation" is the root of all services
@Path("/")
public class WebServices {

    // Just for unittests etc.
    // If set to true - don't access database, only use stubs.
    public boolean isTest = false;

    // Static logger object
    private static Logger logger = Logger.getLogger(WebServices.class);

    // Database write handle 
    private DbWriter dbWriter = null; //new DbWriter();

    // This is the database read handle
    private DbReader dbReader = null; //new DbReader();




        @GET
        @Path("/hello")
        @Produces(MediaType.TEXT_PLAIN)
        public String sayHello() {
            logger.info("sayHello");

            return "Hello Jersey";
        }

    //----------- Read (GET) methods of our REST Api---------------//

    /**
     * Get alle annotations as Atom XML (comments, tags and xlinks), related to a source URI.
     * If "If-Modified-Since" is set, only return annotations if there are new annotations.
     * else return 304 "Not Modified".
     *
     * @return Atom XML
     */
    @GET
    @Path("/")      // {uri}
    @Produces({"application/atom+xml", "application/xml", "application/json"})
    public Response getAnnotations(@QueryParam("uri") String uri,
                                   @Context HttpHeaders headers) {
        logger.debug("GET (atom+xml) URI: " + uri);

        // Feth "Modified-Since" Header
        Calendar modifiedSince = ApiUtils.getModifiedSince(headers);

        // Instantiate read & write handles
        dbReader = new DbReader();

        if (isTest) { // Just for testing purpose
            logger.debug(" Test. Returning OK");
            //return Response.ok(WebServicesTest.getAnnotationsAsXml()).build();
            return  Response.ok().build();
        } else { // "real" case

            //todo: move all logic to utility class
            ArrayList<dk.kb.annotator.model.Xlink> xlinks = null;
            ArrayList<dk.kb.annotator.model.Tag> tags = null;
            ArrayList<dk.kb.annotator.model.Comment> comments = null;


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z");

            if (modifiedSince != null) {
                logger.debug(" all results, modifiedSince " + sdf.format(modifiedSince.getTime()));
                // Read all annotaions since 'modified since'
                xlinks = dbReader.readXlinks(modifiedSince, uri);
                tags = dbReader.readTags(modifiedSince, uri);
                comments = dbReader.readComments(modifiedSince, uri);
            } else {
                // Read all annotaions
                logger.debug(" Read all annotaions");
                try {
                    xlinks = dbReader.readXlinks(uri);
                    tags = dbReader.readTags(uri);
                    comments = dbReader.readComments(uri);
                    logResultFromDB(xlinks, tags, comments);

                } catch (Exception e) {
                    logger.warn("Error reading data from DB. Returning server error." + e);
                    return Response.serverError().build();
                }
            }
            // Create the atom feed

            //Comment tmpComment = new Comment("0", "test", new GregorianCalendar(), "etlink", "Andreas", "hosturi");
            //comments.add(tmpComment);
            logResultFromDB(xlinks, tags, comments);
            dk.kb.annotator.model.AtomFeed atom = new dk.kb.annotator.model.AtomFeed("Annotations since " + modifiedSince, comments, tags, xlinks); // todo: rethink this title

            setIDandLink(uri, null, headers, atom);
            atom.initializeSingleTag();
            /* Return the the connections to the pool */
            dbReader.dbClose();

            if (!atom.isEmpty()) { // OK 
                logger.debug("  return ok ");
                return Response.ok(atom).build();
            } else { //Not Modified
                logger.debug("  return not modified");
                return Response.notModified("Not Modified since " + modifiedSince).build();
            }
        }
    }

    private void logResultFromDB(ArrayList<dk.kb.annotator.model.Xlink> xlinks, ArrayList<dk.kb.annotator.model.Tag> tags, ArrayList<dk.kb.annotator.model.Comment> comments) {
        logger.trace("comments = " + comments);
        logger.trace("tags = " + tags);
        logger.trace("xlinks = " + xlinks);
    }

    /**
     * <p/>
     * Get all annotations as Atom XML, related to a source URI.
     * If "If-Modified-Since" is set, only return annotations if there are new annotations.
     * else return 304 "Not Modified".
     *
     * @param type one of "tag" | "comment" | "xlink"
     * @return Atom XML
     */

    @GET
    @Path("/{type}")
    @Produces({"application/atom+xml", "application/xml", "application/json"})
    public Response getAnnotations(@QueryParam("uri") String uri,
                                   @PathParam("type") ApiUtils.annotationType type,
                                   @Context HttpHeaders headers) {
        logger.debug("GET (atom+xml) URI: " + uri + " type " + type);
        //URLEncoder urlEncoder = new URLEncoder();
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

         logger.debug("GET (atom+xml) URI: " + uri + " type " + type);
        Calendar modifiedSince = ApiUtils.getModifiedSince(headers);

        // Instantiate read & write handles
        dbReader = new DbReader();
        dbWriter = new DbWriter();

        if (isTest) { // Just for testing purpose
            //return Response.ok(WebServicesTest.getAnnotationsAsXml(type)).build();
            return Response.ok().build();
        } else { // "real" case
            ArrayList<dk.kb.annotator.model.Xlink> xlinks = new ArrayList<dk.kb.annotator.model.Xlink>();
            ArrayList<dk.kb.annotator.model.Tag> tags = new ArrayList<dk.kb.annotator.model.Tag>();
            ArrayList<dk.kb.annotator.model.Comment> comments = new ArrayList<dk.kb.annotator.model.Comment>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z");
            if (modifiedSince != null) {
                logger.debug(" all results, modifiedSince " + sdf.format(modifiedSince.getTime()));   //
                // Read all annotaions
                switch (type) {
                    case xlink:
                        xlinks = dbReader.readXlinks(modifiedSince, uri);
                        break;
                    case tag:

                        tags = dbReader.readTags(modifiedSince, uri);
                        break;
                    case comment:
                        comments = dbReader.readComments(modifiedSince, uri);
                        break;
                    default:
                        logger.warn("type is not being handled by the implementation! Type: " + type);
                        return Response.status(Response.Status.BAD_REQUEST).build();
                }
            } else {
                switch (type) {
                    case xlink:
                        logger.debug(" XLINK readXlinks  from DB" + uri);
                        xlinks = dbReader.readXlinks(uri);
                        break;
                    case tag:
                        logger.debug(" TAG readTags  from DB" + uri);
                        tags = dbReader.readTags(uri);
                        break;
                    case comment:
                        logger.debug(" Comment readComments from DB" + uri);
                        comments = dbReader.readComments(uri);
                        break;
                    default:
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    //throw new UnsupportedOperationException("type is not being handled by the implementation! " + type);
                }
            }
            // XML-ify the beans
            // todo: consider putting all this in utility class
            //Document atomXML = null;//AnnotationUtils.createAtomXML(xlinks, tags, comments);
            logResultFromDB(xlinks, tags, comments);

            dk.kb.annotator.model.AtomFeed atom = new dk.kb.annotator.model.AtomFeed("Annotations since " + modifiedSince, comments, tags, xlinks);

            setIDandLink(uri, type, headers, atom);


            // last updated
            ArrayList<Annotation> tmpList = new ArrayList<Annotation>();
            tmpList.addAll(xlinks);
            tmpList.addAll(tags);
            tmpList.addAll(comments);
            Calendar lastUpdated = new GregorianCalendar();
            lastUpdated.setTimeInMillis(ApiUtils.getLatestTimestamp(tmpList).getTime());
            atom.setUpdated(lastUpdated);


            atom.setTitleinAtomFeed(uri, type, atom);

            atom.initializeSingleTag();
            //printListsInAtomFeed(atom);

            /* Return the the connections to the pool */

            dbReader.dbClose();

            if (atom != null) {
                logger.debug(" return ok. hits: " + tmpList.size());
                return Response.ok(atom).build();
            } else {
                logger.debug(" Not Modified since");
                return Response.notModified("Not Modified since " + modifiedSince).build();
            }
        }
    }



    /**
     * Sets the id and link on a AtomFeed.
     *
     * @param uri     Uniform Ressource Identifier.
     * @param type    Type i.e. tag, comment or
     * @param headers
     * @param atom
     */

    private void setIDandLink(String uri, ApiUtils.annotationType type, HttpHeaders headers, AtomFeed atom) {
        String host = headers.getRequestHeader(HttpHeaders.HOST).toString().replace("[", "").replace("]", "");
        String typeStr = "";
        if (type != null) {
            typeStr = type.toString();
        }
        String urlToSelf = host + "/annotation/" + uri + "/" + typeStr;
        atom.setLink(urlToSelf);
        atom.getLink().setRel("self");
        atom.setId(urlToSelf);
    }


    //------------ Write (POST) mehods of the REST API --------------------//

    /**
     * This is the 'INSERT' method of the REST api
     * <p/>
     * Write an xlink, a comment or a tag. The resource
     * to annotate is given in the cgi parameter 'uri'
     * <p/>
     * <p/>
     * todo: Use new Comment / Tag / Xlink constructors
     */

    @POST
    @Path("/{type}/")
    public Response writeAnnotation(@Context UriInfo uriInfo,
                                    @PathParam("type") ApiUtils.annotationType type,
                                    @FormParam("value") String value, // why seperate value from text
                                    @FormParam("creator") String creator,
                                    @FormParam("from") String from,
                                    @FormParam("to") String to,
                                    @FormParam("role") ApiUtils.annotationRole role,
                                    @FormParam("title") String title,
                                    @Context HttpHeaders headers
    ) {
        logger.debug("POST type:" + type + " value " + value + " creator " + creator + " from " + from + " to " + to + " role " + role + "title" + title + " uriInfo:path" + uriInfo.getPath());

        String host = headers.getRequestHeader(HttpHeaders.HOST).toString();

        // Instantiate read & write handles
        dbWriter = new DbWriter();
        //dbReader = new DbReader();

        dk.kb.annotator.model.Annotation annotation = null;
        // Current time
        Calendar rightNow = Calendar.getInstance();

        // we always have a resource that we annotate (from) and a creator.
        if (creator.equals("") || from.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        switch (type) {
            case tag:
                if (value.equals("")) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                annotation = new dk.kb.annotator.model.Tag("", value, rightNow, from, creator);
                dk.kb.annotator.model.Annotation newTag = dbWriter.writeTag((dk.kb.annotator.model.Tag) annotation);
                if (newTag != null) { // Tag succesfully written to db. todo flyttes til util klasse.
                    try {
                        URI permaUri = new URI(newTag.getId());
                        dbWriter.dbClose();
                        return Response.created(permaUri).build();
                    } catch (java.net.URISyntaxException uriErr) {
                        logger.warn("could nor parse URI returned by dbwriter. Error is: " + uriErr.getMessage());
                        dbWriter.dbClose();
                        return Response.created(null).build();
                    }
                }
            case tag_aerial:
                if (value.equals("")) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                annotation = new dk.kb.annotator.model.Tag("", value, rightNow, from, creator);
                dk.kb.annotator.model.Annotation aerialTag = dbWriter.writeAerialTag((dk.kb.annotator.model.Tag) annotation);
                if (aerialTag != null) { // Tag succesfully written to db. todo flyttes til util klasse.
                    try {
                        URI permaUri = new URI(aerialTag.getId());
                        dbWriter.dbClose();
                        return Response.created(permaUri).build();
                    } catch (java.net.URISyntaxException uriErr) {
                        logger.warn("could nor parse URI returned by dbwriter. Error is: " + uriErr.getMessage());
                        dbWriter.dbClose();
                        return Response.created(null).build();
                    }
                }
            case comment:
                if (value.equals("")) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                annotation = new dk.kb.annotator.model.Comment("", value, rightNow, from, creator, "http://" + host.replace("[", "").replace("]", "") + "/" + uriInfo.getAbsolutePath().getRawPath());
                dk.kb.annotator.model.Annotation newComment = dbWriter.writeComment((dk.kb.annotator.model.Comment) annotation);
                if (newComment != null) { // Tag succesfully written to db
                    try {
                        URI permaUri = new URI(newComment.getId());
                        dbWriter.dbClose();
                        return Response.created(permaUri).build();
                    } catch (java.net.URISyntaxException uriErr) {
                        logger.warn("could nor parse URI returned by dbwriter. Error is: " + uriErr.getMessage());
                        dbWriter.dbClose();
                        return Response.created(null).build();
                    }
                } else {
                    return Response.serverError().build();
                }
            case xlink:
                if (role.equals("") || title.equals("") || to.equals("")) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                // todo: make role one of ApiUtils.AnnotationRole
                annotation = new dk.kb.annotator.model.Xlink("", role + "", title, "simple", rightNow, to, from, creator);
                dk.kb.annotator.model.Annotation newXlink = dbWriter.writeXlink((dk.kb.annotator.model.Xlink) annotation);


                if (newXlink != null) { // Tag succesfully written to db
                    try {
                        URI permaUri = new URI(newXlink.getId());
                        dbWriter.dbClose();
                        return Response.created(permaUri).build();
                    } catch (java.net.URISyntaxException uriErr) {
                        logger.warn("could nor parse URI returned by dbwriter. Error is: " + uriErr.getMessage());
                        dbWriter.dbClose();
                        return Response.created(null).build();
                    }
                } else {
                    dbWriter.dbClose();
                    return Response.serverError().build();
                }
            default:
                logger.warn("type is not being handled by the implementation! Type: " + type);
                dbWriter.dbClose();
                return Response.status(Response.Status.BAD_REQUEST).build();

        }


    }


    /**
     * This is the 'UPDATE' method of the REST api
     * <p/>
     * Edit the annotation defined by the permanent 'uri'
     * <p/>
     * todo: implement this, ABWE!
     */
    /*
    @POST
    @Path("/{uri}")
    public Response writeAnnotation(    @PathParam("uri") ApiUtils.annotationType uri,
    @QueryParam("value") String value,
    @QueryParam("text") String text,
    @QueryParam("creator") String creator,
    @QueryParam("xlinkto") String to,
    @QueryParam("role") String role,
    @QueryParam("title") String title
    )
    throws java.net.URISyntaxException{
    // Should return Not Modified 304
    return Response.ok().build();
    }
    */

    /**
     * logs the content of an atom feed. How many tags, xlinks and comments.
     *
     * @param atom
     */
    private void printListsInAtomFeed(dk.kb.annotator.model.AtomFeed atom) {
        logger.trace("atom.getTags() = " + atom.getTags());
        logger.trace("atom.getXlinks() = " + atom.getXlinks());
        logger.trace("atom.getComments() = " + atom.getComments());
    }


}
