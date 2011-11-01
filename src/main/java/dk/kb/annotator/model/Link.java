package dk.kb.annotator.model;

// JAXB

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class for marshalling for Links
 * as &lt;link href='' rel=''>
 *
 * @author
 */


@XmlRootElement(name = "link")
public class Link {

    // So far links are always "alternate"

    private String rel = "alternate";

    // href is the actual URI
    private String href = "";

    public Link() {
    }

    public Link(String href) {
        this.href = href;
    }

    @XmlAttribute
    public String getHref() {
        return this.href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @XmlAttribute
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    @Override
    public String toString() {
        return "Link{" +
                "rel='" + rel + '\'' +
                '}';
    }
}



