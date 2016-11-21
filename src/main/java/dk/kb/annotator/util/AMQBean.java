package dk.kb.annotator.util;

/**
 * Created by dgj on 21-11-2016.
 */
public class AMQBean {

    private String host;
    private String updateQueue;

    public AMQBean() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUpdateQueue() {
        return updateQueue;
    }

    public void setUpdateQueue(String updateQueue) {
        this.updateQueue = updateQueue;
    }
}
