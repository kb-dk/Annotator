package dk.kb.annotator.model;

// JAXB

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A container for Tag data
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 */


@XmlRootElement(name = "entry")
public class Tag extends Annotation {

    private String id = "";
    private String tagText = "";
    java.util.Calendar updated = null;
    private String link = "";
    private String creator = "";

    private TagEntry entry = new TagEntry();

    public Tag() {
    }

    public Tag(String id,
               String text,
               java.util.Calendar updated,
               String link,
               String creator) {
        this.id = id;
        this.tagText = text;
        this.updated = updated;
        this.link = link;
        this.creator = creator;
    }

    // ID
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Tagtext
    @XmlElement(name = "tagText")  // ABW test denne
    public String getTagText() {
        return this.tagText;
    }

    public void setTagText(String text) {
        this.tagText = text;
    }

    // Updated
    public java.util.Calendar getUpdated() {
        return this.updated;
    }

    public void setUpdated(java.util.Calendar updated) {
        this.updated = updated;
    }


    // Link
    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    // Creator
    public String[] getCreator() {
        String[] ret = {this.creator};
        return ret;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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


}
