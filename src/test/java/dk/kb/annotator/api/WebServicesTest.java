package dk.kb.annotator.api;

//Log imports

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

// Core imports


class WebServicesTest {

    public WebServicesTest() {
    }

    // Static logger object
    private static Logger logger = LoggerFactory.getLogger(WebServicesTest.class);
    
    // Path to test xml files
    private final static File ANNOTATION_FILE = new File("testdata/annotations.xml");
    private final static File COMMENT_FILE = new File("testdata/comment.xml");
    private final static File TAG_FILE = new File("testdata/tag.xml");
    private final static File XLINK_FILE = new File("testdata/xlink.xml");
    
    // Utilities for parsing xml files
    private static DocumentBuilder builder;
    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    /**
    * Choose the right file from disc.
    */
    public static Document getAnnotationsAsXml(){
        return parseFromString(ANNOTATION_FILE);
    }
    
    /**
    * Choose the right file from disc, given the annotation type.
    */
    public static Document getAnnotationsAsXml(ApiUtils.annotationType type){
        switch (type){
            case  xlink: return parseFromString(XLINK_FILE);
            case  comment: return parseFromString(COMMENT_FILE);
            default : return parseFromString(TAG_FILE); 
       }
    }
    
    /**
    * Parse the file, and return a document.
    */
    private static Document parseFromString(File file){
        try{
            builder = factory.newDocumentBuilder(); 
            return builder.parse(file);
        } catch (javax.xml.parsers.ParserConfigurationException parse) {
            logger.warn("Error initialising parser. Message is " + parse.getMessage());
            return null;
        } catch (org.xml.sax.SAXException sax) {
            logger.warn("Error parsing xml-file. Message is " + sax.getMessage());
            return null;
        } catch (java.io.IOException io) {
            logger.warn("Error reading xml-file. Message is " + io.getMessage());
            return null;
        }   
    }
}



