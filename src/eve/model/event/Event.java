package eve.model.event;

import eve.wuti.jpa.BasicEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.persistence.*;
import java.io.StringReader;
import java.util.Date;

/**
 * Created by jesus on 11/14/16.
 */
@Entity
@NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e where notificationDate > :from and notificationDate < :until order by notificationDate asc")
public class Event extends BasicEntity {
    @Basic
    private Date notificationDate;

    @OneToOne(cascade = CascadeType.ALL)
    private Source source;

    @OneToOne(cascade = CascadeType.ALL)
    private Target target;

    @OneToOne(cascade = CascadeType.ALL)
    private Payload payload;

    @OneToOne(cascade = CascadeType.ALL)
    private Related related;

    public Event() {
    }

    public Event(Source source, Target target, Payload payload, Related related) {
        this.source = source;
        this.target = target;
        this.payload = payload;
        this.related = related ;
        this.notificationDate = new Date();
    }

    public static Event getFromJSON(String jsonData) throws Exception {
        if (jsonData == null) throw new Exception("Input is empty (or could not be understood)");

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new StringReader(jsonData));

        Source source;
        try {
            source = Source.getFromJSON((JSONObject) jsonObject.get("source"));
        } catch (Throwable e) {
            source = null;
        }

        Target target;
        try {
            target = Target.getFromJSON((JSONObject) jsonObject.get("target"));
        } catch (Throwable e) {
            target = null;
        }

        Payload payload;
        try {
            payload = Payload.getFromJSON((JSONObject) jsonObject.get("payload"));
        } catch (Throwable e) {
            payload = null;
        }

        Related related ;
        try {
            related = Related.getFromJSON((JSONObject) jsonObject.get("related")) ;
        } catch (Throwable t) {
            related = null ;
        }

        return new Event(source, target, payload, related);
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public Source getSource() {
        return source;
    }

    public Target getTarget() {
        return target;
    }

    public Payload getPayload() {
        return payload;
    }

    public Related getRelated() {
        return related;
    }

    public String toJSON () {
        return "{\"id\":" + getId() + ", \"date\":" + notificationDate.getTime() + ", \"source\":" + source.toJSON() + ", \"target\":" + target.toJSON() + ", \"payload\":" + payload.toJSON() + "}";
    }
}
