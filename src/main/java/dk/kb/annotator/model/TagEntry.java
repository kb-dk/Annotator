package dk.kb.annotator.model;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abwe
 * Name: Andreas B. Westh
 * E-mail: abwe@kb.dk
 * Date: 15-03-11
 * Time: 13:53
 */
@XmlRootElement(name = "entry")
public class TagEntry {

    String title = "";
    String id = "";

    Calendar updated = null;
    String author = "";

    Link link = new Link("");

    List<Category> listOfTags = new ArrayList<Category>();

    Content content = new Content();


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Calendar getUpdated() {
        return updated;
    }

    public void setUpdated(Calendar updated) {
        this.updated = updated;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    @XmlElement(name = "category", type = Category.class)
    public List<Category> getListOfTags() {
        return listOfTags;
    }

    public void setListOfTags(List<Category> listOfTags) {
        this.listOfTags = listOfTags;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
