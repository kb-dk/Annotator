package dk.kb.annotator.api;

import dk.kb.annotator.database.DbReader;
import dk.kb.annotator.database.DbWriter;
import dk.kb.annotator.model.Annotation;
import dk.kb.annotator.model.AtomFeed;
import dk.kb.annotator.model.Comment;
import dk.kb.annotator.model.Tag;
import dk.kb.annotator.model.Xlink;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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

    // This is the database read handle
    private DbReader dbReader = null; //new DbReader();

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello() {
        logger.info("Annotation Engine greets you");

        return "Annotation Engine greets you";
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
                                   @QueryParam("id") String id,
                                   @Context HttpHeaders headers) {
        logger.debug("GET (atom+xml) URI: " + uri);

        // Feth "Modified-Since" Header
        Calendar modifiedSince = ApiUtils.getModifiedSince(headers);

        // Instantiate read & write handles
        dbReader = new DbReader();

        if (isTest) { // Just for testing purpose
            logger.debug(" Test. Returning OK");
            return  Response.ok().build();
        } else { // "real" case

            boolean getById = (id != null && !"".equals(id));
            if (getById) uri=id;

            //todo: move all logic to utility class
            ArrayList<dk.kb.annotator.model.Xlink> xlinks = null;
            ArrayList<dk.kb.annotator.model.Tag> tags = null;
            ArrayList<dk.kb.annotator.model.Comment> comments = null;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z");

            if (modifiedSince != null) {
                logger.debug(" all results, modifiedSince " + sdf.format(modifiedSince.getTime()));
                // Read all annotaions since 'modified since'
                xlinks = dbReader.readXlinks(modifiedSince,getById, uri);
                tags = dbReader.readTags(modifiedSince,getById, uri);
                comments = dbReader.readComments(modifiedSince,getById, uri);
            } else {
                // Read all annotaions
                logger.debug(" Read all annotations");
                try {
                    xlinks = dbReader.readXlinks(uri,getById);
                    tags = dbReader.readTags(uri,getById);
                    comments = dbReader.readComments(uri,getById);
                    logResultFromDB(xlinks, tags, comments);

                } catch (Exception e) {
                    logger.warn("Error reading data from DB. Returning server error." + e);
                    return Response.serverError().build();
                }
            }
            // Create the atom feed

            logResultFromDB(xlinks, tags, comments);
            AtomFeed atom = new AtomFeed("Annotations since "
                    + modifiedSince, comments, tags, xlinks); // todo: rethink this title

            setIDandLink(uri, null, headers, atom);
           // atom.initializeSingleTag();
            /* Return the the connections to the pool */

            if (!atom.isEmpty()) { // OK 
                logger.debug("  return ok ");
                return Response.ok(atom).build();
            } else if( atom.isEmpty()){
                logger.debug("No Content ");
                return Response.noContent().build();
            }
              else
             { //Not Modified
                logger.debug("  return not modified");
                return Response.notModified("Not Modified since " + modifiedSince).build();
            }

        }
    }

    private void logResultFromDB(ArrayList<Xlink> xlinks,
                                 ArrayList<Tag> tags,
                                 ArrayList<Comment> comments) {
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
                                   @QueryParam("id") String id,
                                   @PathParam("type") ApiUtils.annotationType type,
                                   @Context HttpHeaders headers) {
        try {
            if (uri != null) uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(" URI: " + uri + " wrong encoded");
            e.printStackTrace();
        }


        boolean getById = (id != null && !"".equals(id));
        if (getById){
            uri=id;
            logger.debug("GETTING Specific Annotation from id "+id +  " type " + type);
        }
        else{
              logger.debug("GET  URI: " + uri + " type " + type);
        }

        Calendar modifiedSince = ApiUtils.getModifiedSince(headers);

        dbReader = new DbReader();

        if (isTest) { // Just for testing purpose
            return Response.ok().build();
        } else { // "real" case
            ArrayList<Xlink> xlinks = new ArrayList<dk.kb.annotator.model.Xlink>();
            ArrayList<Tag> tags = new ArrayList<dk.kb.annotator.model.Tag>();
            ArrayList<Comment> comments = new ArrayList<Comment>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z");
            if (modifiedSince != null) {
                logger.debug(" all results, modifiedSince " + sdf.format(modifiedSince.getTime()));
                // Read all annotations
                switch (type) {
                    case xlink:
                        xlinks = dbReader.readXlinks(modifiedSince, getById, uri);
                        break;
                    case tag:
                        tags = dbReader.readTags(modifiedSince, getById, uri);
                        break;
                    case comment:
                        comments = dbReader.readComments(modifiedSince, getById, uri);
                        break;
                    default:
                        logger.warn("type is not being handled by the implementation! Type: " + type);
                        return Response.status(Response.Status.BAD_REQUEST).build();
                }
            } else {
                switch (type) {
                    case xlink:
                        logger.debug(" XLINK readXlinks  from DB" + uri);
                        xlinks = dbReader.readXlinks(uri,getById);
                        break;
                    case tag:
                        logger.debug(" TAG readTags  from DB" + uri);
                        tags = dbReader.readTags(uri,getById);
                        break;
                    case comment:
                        logger.debug(" Comment readComments from DB" + uri);
                        comments = dbReader.readComments(uri,getById);
                        break;
                    case tag_aerial:
                        logger.debug(" TAG_AERIAL read tags from DB.");
                        tags = dbReader.readAerialTags(uri, getById);
                        break;
                    default:
                        return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
            // XML-ify the beans
            // todo: consider putting all this in utility class
            logResultFromDB(xlinks, tags, comments);

            AtomFeed atom = new AtomFeed("Annotations since " + modifiedSince, comments, tags, xlinks);

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
           // atom.initializeSingleTag();

            if (atom != null) {
                logger.debug(" return ok. hits: " + tmpList.size());
                return Response.ok(atom).build();
            } else {
                logger.debug(" Not Modified since");
                return Response.notModified("Not Modified since " + modifiedSince).build();
            }
        }
    }
    @DELETE
    @Path("/{type}")
        @Produces({"application/atom+xml", "application/xml", "application/json"})
    public Response deleteAnnotation(@PathParam("type") ApiUtils.annotationType type,
                                     @QueryParam(value = "id") String id) {

        DbWriter dbWriter = new DbWriter();
        if(id != null && !id.equals("") ) {
            logger.debug("deleting annotation with creator and object id. "+type+" id="+id );
            if (dbWriter.deleteAnnotation(type,id))
                return Response.ok().build();
            else
                return Response.status(500).build();

        }else if (id != null && !id.equals("") ){
            logger.debug("deleting "+type+" id="+id);

            if (dbWriter.deleteAnnotation(type,id))
                return Response.ok().build();
            else
                return Response.status(500).build();


        }else{
            logger.warn("Couldn't delete annotation.  "+type+" id="+id);

            return Response.status(500).build();
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
        DbWriter dbWriter = new DbWriter();

        Annotation annotation = null;
        // Current time
        Calendar rightNow = Calendar.getInstance();

        // we always have a resource that we annotate (from) and a creator.
        if (creator.equals("") || from.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        switch (type) {
            case tag:
                logger.debug("Got a tag");
            case tag_aerial:
                logger.debug("Got an aerial tag");
                if (value.equals("")) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                annotation = new Tag("", value, rightNow, from, creator);
                Annotation aerialTag = dbWriter.writeAerialTag((Tag) annotation);
                if (aerialTag != null) { // Tag succesfully written to db. todo flyttes til util klasse.
                    try {
                        URI permaUri = new URI(aerialTag.getId());
                        return Response.created(permaUri).build();
                    } catch (java.net.URISyntaxException uriErr) {
                        logger.warn("could nor parse URI returned by dbwriter. Error is: " + uriErr.getMessage());
                        return Response.created(null).build();
                    }
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            case comment:
                logger.debug("Got a comment");
                if (value.equals("")) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                annotation = new Comment("", value, rightNow, from, creator, "http://" + host.replace("[", "").replace("]", "") + "/" + uriInfo.getAbsolutePath().getRawPath());
                Annotation newComment = dbWriter.writeComment((Comment) annotation);
                if (newComment != null) { // Tag succesfully written to db
                    try {
                        URI permaUri = new URI("?id="+newComment.getId());
                        return Response.created(permaUri).build();
                    } catch (java.net.URISyntaxException uriErr) {
                        logger.warn("could nor parse URI returned by dbwriter. Error is: " + uriErr.getMessage());
                        return Response.created(null).build();
                    }
                } else {
                    return Response.serverError().build();
                }
            case xlink:
                logger.debug("Got an xlink");
                if (role.equals("") || title.equals("") || to.equals("")) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                // todo: make role one of ApiUtils.AnnotationRole
                annotation = new Xlink("", role + "", title, "simple", rightNow, to, from, creator);
                Annotation newXlink = dbWriter.writeXlink((Xlink) annotation);

                if (newXlink != null) { // Tag successfully written to db
                    try {
                        URI permaUri = new URI(newXlink.getId());
                        return Response.created(permaUri).build();
                    } catch (java.net.URISyntaxException uriErr) {
                        logger.warn("could nor parse URI returned by dbwriter. Error is: " + uriErr.getMessage());
                        return Response.created(null).build();
                    }
                } else {
                    return Response.serverError().build();
                }
            default:
                logger.debug("Hit the default case");
                logger.warn("type is not being handled by the implementation! Type: " + type);
                return Response.status(Response.Status.BAD_REQUEST).build();

        }
        //return Response.status(Response.Status.BAD_REQUEST).build();
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
}
