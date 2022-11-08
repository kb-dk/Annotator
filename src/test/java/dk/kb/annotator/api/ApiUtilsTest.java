package dk.kb.annotator.api;

//Log imports

import dk.kb.annotator.api.ApiUtils;
import dk.kb.annotator.model.Annotation;
import dk.kb.annotator.model.Comment;
import dk.kb.annotator.model.Tag;
import dk.kb.annotator.model.Xlink;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

// JUnit imports

/**
 * Unittest of class ApiUtils
 */
public class ApiUtilsTest {

    // Tivial Logger object 
    //private static Logger logger = Logger.getLogger(ApiUtilsTest.class);

    @Before
    public void setup(){
        System.out.println("Setup - API Utils ");
    }

    /*
    * Testcases for method testParseModifiedSince()
    */

    /**
     * Testcase - OK
     */
    @Test
    public void testParseModifiedSinceOk() {
        String ifModifiedSince = "Fri, 06 May 1977 12:00:00 GMT";
        assertEquals("231768000000", ApiUtils.parseModifiedSince(ifModifiedSince).getTimeInMillis() + "");
    }

    /**
     * Testcase - Illigal date - 31. april
     */
    @Test
    public void testParseModifiedSinceNoDate() {
        String ifModifiedSince = "Fri, 31 Apr 2005 12:00:00 GMT";
        assertNull(ApiUtils.parseModifiedSince(ifModifiedSince));
    }

    /**
     * Testcase - Illigal date format
     */
    @Test
    public void testParseModifiedSinceIlligalFormat() {
        String ifModifiedSince = "Fri, 06 May 1977 12:00 GMT";
        assertNull(ApiUtils.parseModifiedSince(ifModifiedSince));
    }

    /*
    * Testcases for normalizeUri(String uri)
    * Names on testcases should explain the test.
    */

    @Test(expected = java.net.URISyntaxException.class)
    public void testNormalizedUriWrongUriSyntax() throws java.net.URISyntaxException {
        String uriString = "http://foo.bar.dk%¤&¤&%7";
        ApiUtils.normalizeUri(uriString);
    }


    @Test
    public void testNormalizedUriRemoveTrailingSlashFromDomain() throws java.net.URISyntaxException {
        String uriString = "http://foo.bar.dk/";
        assertEquals("http://foo.bar.dk", ApiUtils.normalizeUri(uriString));
    }

    @Test
    public void testNormalizedUriRemovePortNum() throws java.net.URISyntaxException {
        String uriString = "http://foo.bar.dk:80";
        assertEquals("http://foo.bar.dk", ApiUtils.normalizeUri(uriString));
    }

    @Test
    public void testNormalizedUriRemovePortNum2() throws java.net.URISyntaxException {
        String uriString = "http://foo.bar.dk:80/tags";
        assertEquals("http://foo.bar.dk/tags", ApiUtils.normalizeUri(uriString));
    }

    @Test
    public void testNormalizedUriRemoveEmptyCgi() throws java.net.URISyntaxException {
        String uriString = "http://foo.bar.dk?foo=&bar=test";
        assertEquals("http://foo.bar.dk?bar=test", ApiUtils.normalizeUri(uriString));
    }

    @Test
    public void testNormalizedUriRemoveEmptyCgi2() throws java.net.URISyntaxException {
        String uriString = "http://foo.bar.dk?foo=&bar=";
        assertEquals("http://foo.bar.dk", ApiUtils.normalizeUri(uriString));
    }

    @Test
    public void testNormalizedUriRemoveEmptyCgi3() throws java.net.URISyntaxException {
        String uriString = "http://foo.bar.dk?foo=&bib=&bar=test";
        assertEquals("http://foo.bar.dk?bar=test", ApiUtils.normalizeUri(uriString));
    }

    @Test
    public void testNormalizedUriRemoveEmptyCgi4() throws java.net.URISyntaxException {
        String uriString = "http://foo.bar.dk/?foo=";
        assertEquals("http://foo.bar.dk", ApiUtils.normalizeUri(uriString));
    }

    @Test
    public void testNormalizedUriAlphaCgi() throws java.net.URISyntaxException {
        String uriString = "http://foo.bar.dk?cal=yes&bob=yes&alf=yes&dom=yes";
        assertEquals("http://foo.bar.dk?alf=yes&bob=yes&cal=yes&dom=yes", ApiUtils.normalizeUri(uriString));
    }

    @Test
    public void testNormalizedUriFragmentOK() throws java.net.URISyntaxException {
        String uriString = "http://foo.bar.dk?foo=&bib=&bar=test#fragment";
        assertEquals("http://foo.bar.dk?bar=test#fragment", ApiUtils.normalizeUri(uriString));
    }


    @Test
    public void testDate() {

        ArrayList<Annotation> testDataAnnoList = new ArrayList<Annotation>();

        Annotation xlinkFra1980 = new Xlink();
        xlinkFra1980.setUpdated(new GregorianCalendar(1980, 1, 1, 12, 1, 1));
        Annotation tagFra1990 = new Tag();
        tagFra1990.setUpdated(new GregorianCalendar(1990, 1, 1, 12, 1, 1));

        Comment kommentarFra2010 = new Comment();
        kommentarFra2010.setUpdated(new GregorianCalendar(2010, 1, 1, 10, 1, 1));
        testDataAnnoList.add(xlinkFra1980);
        testDataAnnoList.add(tagFra1990);
        testDataAnnoList.add(kommentarFra2010);

        Date dato = ApiUtils.getLatestTimestamp(testDataAnnoList);
        assertEquals(dato, new GregorianCalendar(2010, 1, 1, 10, 1, 1).getTime());
    }


}


