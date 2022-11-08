package dk.kb.annotator.model;

// JAXB


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Calendar;

/**
 * A container for Tag data
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 */


@XmlRootElement(name = "entry")
public class Tag extends Annotation {

    private String id = "";
    private String tagText = "";
    private Content content = null;
    private Calendar updated = null;
    private String link = "";
    private String creator = "";

    private TagEntry entry = new TagEntry();

    public Tag() {
    }

    public Tag(String id,
               String content,
               java.util.Calendar updated,
               String link,
               String creator) {
        this.id = id;
        this.content = new Content(content);
        this.updated = updated;
        this.link =link;
        this.creator = creator;
    }

    // ID
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }


    // Updated
    public java.util.Calendar getUpdated() {
        return this.updated;
    }

    public void setUpdated(java.util.Calendar updated) {
        this.updated = updated;
    }


    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
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


    @Override
    public String toString() {
        return "Tag{" +
                "id='" + id + '\'' +
                ", tagText='" + tagText + '\'' +
                ", updated=" + updated +
                ", link='" + link + '\'' +
                ", creator='" + creator + '\'' +
                '}';
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
