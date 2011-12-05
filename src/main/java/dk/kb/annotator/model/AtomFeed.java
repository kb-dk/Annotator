package dk.kb.annotator.model;

// JAXB

import dk.kb.annotator.api.ApiUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Wraps annotations in a feed element
 * when marshalling Annotaions.
 */

//todo abw indsæt xmlns for xlink feed.
@XmlRootElement(name = "feed")
public class AtomFeed {


    private static Logger logger = Logger.getLogger(AtomFeed.class);

    // definitions of the content 
    // of an Atom feed
    private String title = "";
    private Link link = new Link("");
    private Calendar updated = null;
    private String id = "id";

    // Comments / Tags / Xlinks
    // Are all "entry's"


    private List<Comment> comments = null;
    private List<Xlink> xlinks = null;
    private List<Tag> tags = null;

    private TagEntry singleTag = new TagEntry(); // ABW added one single tag entry


    // Default constructor needed by JAXB
    public AtomFeed() {
    }

    // The constructor to use,
    public AtomFeed(String title,
                    List<Comment> comments,
                    List<Tag> tags,
                    List<Xlink> xlinks) {

        this.comments = comments;
        this.xlinks = xlinks;
        this.tags = tags;
        this.title = title;

        if (this.isEmpty()) {
            logger.debug("ingen data i atom feed. Hverken comments tags eller links");
        }


    }

    public void initializeSingleTag() {
        if (!tags.isEmpty()) {

            // singleTag.set
            singleTag.setId(this.getId());
            singleTag.setUpdated(this.getUpdated());

            singleTag.setAuthor("");
            List<Category> tagsSomCategory = new ArrayList<Category>();

            String alleTags = "";
            for (Tag etTag : tags) {

                Category etNytTag = new Category(etTag.getTagText(), "tag");
                tagsSomCategory.add(etNytTag);
                alleTags += "'" + etNytTag.getLabel() + "' ";    // String with every tag appended. Used in the content element.
            }
            Content alleTagsCnt = new Content();
            alleTagsCnt.setType("text");
            alleTagsCnt.setValue(alleTags);
            singleTag.setListOfTags(tagsSomCategory);

            singleTag.setContent(alleTagsCnt);
            singleTag.setLink(new Link(tags.get(0).getLink())); // take the first tag in the list of tags and get the link.

        }
    }

    // Id
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    // Title
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    // Updated
    public java.util.Calendar getUpdated() {
        return this.updated;
    }

    public void setUpdated(java.util.Calendar updated) {
        this.updated = updated;
    }

    // Link
    // todo: write something better 
    @XmlElement(name = "link")
    public Link getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link.setHref(link);
    }

    public void setLink(Link linkobj) {
        this.link = linkobj;

    }

    /**
     * If all three lists are null (returned by dbReader, if
     * no rows are returned), return true else false
     */
    public boolean isEmpty() {
        return (this.tags == null && this.comments == null && this.xlinks == null);
    }

    @XmlElement(name = "entry", type = Comment.class)
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @XmlElement(name = "entry", type = Xlink.class)
    public List<Xlink> getXlinks() {
        return xlinks;
    }

    public void setXlinks(List<Xlink> xlinks) {
        this.xlinks = xlinks;
    }

    @XmlElement(name = "entry", type = TagEntry.class)
    public TagEntry getSingleTag() {
        return singleTag;
    }

    public void setSingleTag(TagEntry singleTag) {
        this.singleTag = singleTag;
    }

    @XmlTransient
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setTitleinAtomFeed(String uri, ApiUtils.annotationType type, AtomFeed atom) {
        switch (type) {
            case comment:
                atom.setTitle("Tags for resource " + uri);
                break;
            case xlink:
                atom.setTitle("Xlink for resource " + uri);
                atom.getLink().setRel("self");
                break;
            case tag:
                atom.setTitle("Tags for resource " + uri);
                break;

             case tag_aerial:
                atom.setTitle("Tags for aerial resource " + uri);
                break;
            default:
                throw new UnsupportedOperationException("type is not being handled by the implementation! " + type);
        }
    }
}
 
