package dk.kb.annotator.model;

/**
 * An interface for annotation data
 * @author Sigfrid Lundberg (slu@kb.dk)
 */


public abstract class Annotation {

    // get methods
    public abstract String getId();
    public abstract java.util.Calendar getUpdated();
    public abstract String[] getCreator();

    // set methods
    public abstract void setId(String id);
    public abstract void setUpdated(java.util.Calendar updated);
    public abstract void setCreator(String creator);

}
