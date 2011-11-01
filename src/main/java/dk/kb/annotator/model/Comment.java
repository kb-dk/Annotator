package dk.kb.annotator.model;

// JAXB

import javax.xml.bind.annotation.*;
import java.util.Calendar;

/**
 * A container for Comment data
 * Uses Jaxb for marshalling as XML and JSON
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 */


@XmlRootElement(name = "entry")
public class Comment extends Annotation {

    private String id = "";
    private Content content = null;
    private Calendar updated = null;
    private Link link = null;
    private String creator = "";
    private String hosturi = ""; // @todo slu tilføjer host uri fil. @fixed 20110308/slu

    public Comment() {
    }

    /**
     * Constructor initializer.
     *
     * @param id
     * @param content Object
     * @param updated
     * @param link
     * @param creator
     * @param hosturi
     */
    public Comment(String id,
                   Content content,
                   Calendar updated,
                   String link,
                   String creator,
                   String hosturi) {
        this.id = id;
        this.content = content;
        this.updated = updated;
        this.link = new Link(link);
        this.creator = creator;
        this.hosturi = hosturi;
    }

    /**
     * Constructor of convience.
     *
     * @param id
     * @param content String
     * @param updated
     * @param link
     * @param creator
     * @param hosturi
     */
    public Comment(String id,
                   String content,
                   Calendar updated,
                   String link,
                   String creator,
                   String hosturi) {
        this.id = id;
        this.content = new Content(content);
        this.updated = updated;
        this.link = new Link(link);
        this.creator = creator;
        this.hosturi = hosturi;
    }


    // ID
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Content getContent() {
        return this.content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    // Updated
    public java.util.Calendar getUpdated() {
        return this.updated;
    }

    public void setUpdated(java.util.Calendar updated) {
        this.updated = updated;
    }

    // Link
    @XmlElement(name = "link")
    public Link getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link.setHref(link);
    }

    // Creator
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Special get'er, used by JAXB marshalling
     * XmlElementWrapper require it to return an Array
     */
    @XmlElement(name = "name")
    @XmlElementWrapper(name = "author")
    public String[] getCreator() {
        String[] ret = {this.creator};
        return ret;
    }


    // hostUri
    @XmlTransient
    public String getHostUri() {
        return hosturi;
    }

    public void setHostUri(String hosturi) {
        this.hosturi = hosturi;
    }


    /**
     * Special get'er. Title is mandatory in Atom
     */
    // todo: consider this default value
    @XmlElement(name = "title")
    public String getTitle() {
        return "[none]";
    }


    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", updated=" + updated +
                ", link=" + link +
                ", creator='" + creator + '\'' +
                ", hosturi='" + hosturi + '\'' +
                '}';
    }
}
