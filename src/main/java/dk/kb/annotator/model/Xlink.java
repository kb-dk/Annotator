package dk.kb.annotator.model;

import javax.xml.bind.annotation.*;

/**
 * A container for Tag data
 *
 * @author Sigfrid Lundberg (slu@kb.dk)
 */

@XmlRootElement(name = "entry")
public class Xlink extends Annotation {

    String id = "";
    dk.kb.annotator.model.Category role = new dk.kb.annotator.model.Category();
    String title = "";
    String xlinkType = "simple";
    java.util.Calendar updated = null;
    Link linkTo = null;
    Link linkFrom = null;
    String creator = "";

    public Xlink() {
    }

    public Xlink(String id,
                 String role,
                 String title,
                 String type,
                 java.util.Calendar updated,
                 String linkTo,
                 String linkFrom,
                 String creator) {

        this.id = id;
        this.role = new dk.kb.annotator.model.Category(role);
        this.title = title;
        this.xlinkType = type;
        this.updated = updated;

        this.linkTo = new Link(linkTo);
        this.linkTo.setRel("related");  // setting link from must be related not alternate

        this.linkFrom = new Link(linkFrom);
        this.creator = creator;


    }


    // ID
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Type
    @XmlTransient
    public String getType() {
        return this.xlinkType;
    }

    public void setType(String type) {
        // not supported
    }

    // Role

    @XmlElement(name = "category")
    public dk.kb.annotator.model.Category getRole() {
        return role;
    }

    public void setRole(dk.kb.annotator.model.Category role) {
        this.role = role;
    }

    // Title
    public String getTitle() {
        return this.title;
    }

/*    public void setTitle(String title) {
        this.role = title;
    }*/

    public void setTitle(String title) {
        this.title = title;
    }

    // Updated
    public java.util.Calendar getUpdated() {
        return this.updated;
    }

    public void setUpdated(java.util.Calendar updated) {
        this.updated = updated;
    }

    @XmlElement(name = "link")
    public Link getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(Link linkTo) {
        this.linkTo = linkTo;
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

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getXlinkType() {
        return xlinkType;
    }

    public void setXlinkType(String xlinkType) {
        this.xlinkType = xlinkType;
    }

    @XmlElement(name = "link")
    public Link getLinkFrom() {
        return linkFrom;
    }

    public void setLinkFrom(Link linkFrom) {
        this.linkFrom = linkFrom;
    }

    @Override
    public String toString() {
        return "Xlink{" +
                "id='" + id + '\'' +
                ", role='" + role + '\'' +
                ", title='" + title + '\'' +
                ", xlinkType='" + xlinkType + '\'' +
                ", updated=" + updated +
                ", linkTo='" + linkTo + '\'' +
                ", linkFrom='" + linkFrom + '\'' +
                ", creator='" + creator + '\'' +
                '}';
    }
}
