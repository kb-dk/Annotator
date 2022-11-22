package dk.kb.annotator.api;

//Log imports

import dk.kb.annotator.model.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


// Jersey Imports
// java core imports

public class ApiUtils {


    private static final Logger logger = LoggerFactory.getLogger(ApiUtils.class);

    /**
     * Definition of annotation semantics
     */
    public enum annotationType {
        xlink, comment, tag, tag_aerial
    }

    /**
     * Definetions of annotation roles
     */
    public enum annotationRole {
        similarity, hasPart, isPartOf
    }

    /**
     * Normalize a Uri according to some simple rules:
     * First advice: Use cool URI's. They don't need to bee
     * normalized.
     * <p/>
     * Calls to the REST api are suppose to URLescape everything, so that
     * the servlet can intrerpret the URI. By the time it is converted
     * to a String by java, it is human readable - no '&amp;' or '%21'
     * <p/>
     * java.net.URI naming
     * [scheme:][//authority][path][?query][#fragment]
     * authority: [user-info@]host[:port]
     *
     * @return a String representation of the URI.
     */
    public static String normalizeUri(String uri)
            throws java.net.URISyntaxException {
        URI parsedUri = new URI(uri).normalize();

        String path = parsedUri.getPath();
        String query = parsedUri.getQuery();

        // Remove empty 
        // Case no query
        if (query != null) {
            // Case multiple parameters
            if (query.contains("&")) {
                String newQuery = "";
                String[] params = query.split("&");
                List<String> paramList = Arrays.asList(params);
                Collections.sort(paramList);
                boolean first = true;
                for (String param : paramList) {
                    if (!param.endsWith("=")) {
                        if (!first) {
                            newQuery += "&";
                        }
                        first = false;
                        newQuery += param;
                    }
                }
                query = newQuery.equals("") ? null : newQuery;

            }
            // Case One parameter
            else {
                if (query.endsWith("=")) {
                    query = null;
                }
            }
        }

        int port = parsedUri.getPort();
        // Removes defalut port definitions
        if (port == 80) {
            port = -1;
        }

        // Remove trailing slash from domain if no path
        if (path.equals("/")) {
            path = "";
        }


        return new URI(parsedUri.getScheme(), parsedUri.getUserInfo(), parsedUri.getHost(), port, path, query, parsedUri.getFragment()).toString();
    }


    /**
     * Extract "If-Modifie-Since" from HttpHeaders object
     * If it is not set, return null;
     *
     * @return Calendar Object parsed by parseModifiedSince()
     */
    public static Calendar getModifiedSince(HttpHeaders headers) {
        if (headers.getRequestHeader("If-Modified-Since") != null) {
            // Header fields can be repeated.
            final java.util.List modifiedList = headers.getRequestHeader("If-Modified-Since");
            if (!modifiedList.isEmpty()) {
                //We always use the first instance.
                return parseModifiedSince(modifiedList.get(0).toString());
            }
        }
        return null;
    }


    /**
     * Parse the String that is given by any http client as
     * value of the If-Modified-Since http header
     * Usage of this method should check for null in return
     *
     * @return Calender object if text is parsable, else null.
     */
    public static Calendar parseModifiedSince(String input) {
        try {
            //                                      The format of a If-modified-Since field
            DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US); // forcing locale us in order to read timezone (z) when system locale is i.e. DA, DK.
            dateFormat.setLenient(false);
            Date parsedDate = dateFormat.parse(input);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsedDate);
            return cal;
        } catch (java.text.ParseException parse) {
            logger.error("could not parse the If-Modified-Since header field: '" +
                    input +
                    "' error is: " +
                    parse.getMessage());
            return null;
        }
    }

    /**
     * Return the latest date from a list of annotations.
     * @param input a List of Annotations.
     * @return a date.
     */
    public static Date getLatestTimestamp(ArrayList<Annotation> input) {
        Date output = new GregorianCalendar(1970, 1, 1, 12, 1, 1).getTime();
        for (Annotation a : input) {
            if (output.before(a.getUpdated().getTime())) {
                output = a.getUpdated().getTime();
            }
        }
        return output;
    }
}
