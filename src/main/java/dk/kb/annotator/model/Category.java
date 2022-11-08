package dk.kb.annotator.model;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: abwe
 * Name: Andreas B. Westh
 * E-mail: abwe@kb.dk
 * Date: 09-03-11
 * Time: 16:36
 */
@XmlRootElement(name = "category")
public class Category {


    private String term = "role";


    private String label = "";

    private String author = "";

    public Category() {
    }

    public Category(String label) {
        this.label = label;
    }

    public Category(String label, String term) {
        this.label = label;
        this.term = term;
    }

    public Category( String label,String term, String author) {
        this.term = term;
        this.label = label;
        this.author = author;
    }

    @XmlAttribute
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @XmlAttribute
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @XmlAttribute
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
