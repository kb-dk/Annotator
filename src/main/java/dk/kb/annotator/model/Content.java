package dk.kb.annotator.model;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by IntelliJ IDEA.
 * User: abwe
 * Name: Andreas B. Westh
 * E-mail: abwe@kb.dk
 * Date: 15-03-11
 * Time: 13:58
 */

public class Content {
    String type = "text";
    String value = "";


    public Content() {
    }

    public Content(String value) {
        this.value = value;
    }

    @XmlAttribute(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
