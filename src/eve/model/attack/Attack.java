package eve.model.attack;

import com.sun.istack.internal.NotNull;
import eve.model.event.*;
import eve.model.infra.Host;
import eve.var.Utils;
import eve.wuti.jpa.BasicEntity;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by jesus on 12/2/16.
 */
@Entity
@NamedQuery(name = "Event.findBySourceAndTarget", query = "SELECT e FROM Event e where source = :source and target=:target")
public class Attack extends BasicEntity {
    private static final int LONG_STRINGS_MAX_LENGTH = 1000;
    private static final int SHORT_STRINGS_MAX_LENGTH = 150;

    @Basic
    private Date  firstNotificationDate;

    @Basic
    private Date lastNotificationDate;

    @Basic
    @Column(length = SHORT_STRINGS_MAX_LENGTH)
    private String name;

    @Basic
    @Column(length = SHORT_STRINGS_MAX_LENGTH)
    private String probes;

    @Basic
    @Column(length = LONG_STRINGS_MAX_LENGTH)
    private String evidences;

    @Basic
    @Column(length = LONG_STRINGS_MAX_LENGTH)
    private String linksToURLs;

    @Basic
    private int numberOfEvents = 0;

    @OneToOne(cascade = CascadeType.ALL)
    private Source source;

    @OneToOne(cascade = CascadeType.ALL)
    private Target target;

    @OneToOne(cascade = CascadeType.ALL)
    private Related related;

    public Attack() {
    }

    public Attack(@NotNull Event firstEvent) {
        Source source =   firstEvent.getSource();
        Target target = firstEvent.getTarget();
        Payload payload = firstEvent.getPayload();
        Related related = firstEvent.getRelated();

        this.firstNotificationDate = new Date();
        this.lastNotificationDate = new Date();
        this.numberOfEvents = 1;

        if (payload != null) this.name = Utils.adaptToLength(payload.getName(), SHORT_STRINGS_MAX_LENGTH);
        if (payload != null && payload.getProbe() != null) this.probes = Utils.adaptToLength(payload.getProbe().toString(), SHORT_STRINGS_MAX_LENGTH);
        if (payload != null) this.linksToURLs = toLink(payload.getURL());
        if (payload != null) this.evidences = Utils.adaptToLength(payload.getEvidence(), LONG_STRINGS_MAX_LENGTH) ;

        if (source != null) this.source = new Source(source);
        if (target != null) this.target = new Target(target);
        if (related != null) this.related = new Related(related);
    }

    public void addEvent(Event event) {
        if (event == null) return;

        if (event.getSource() != null) {
            if (source == null) source = new Source(event.getSource());
            source.autoComplete(event.getSource());
        }

        if (event.getTarget() != null) {
            if (target == null) target = new Target(event.getTarget());
            target.autoComplete(event.getTarget());
        }

        autoCompleteName(event.getPayload());
        autoCompleteProbes(event.getPayload());
        autoCompleteEvidences(event.getPayload());
        autoCompleteURLs(event.getPayload());

        if (event.getRelated() != null) {
            if (related == null) related = event.getRelated();
        }

        lastNotificationDate = new Date();
        numberOfEvents++;
    }

    public Date getFirstNotificationDate() {
        return firstNotificationDate;
    }

    public Source getSource() {
        return source;
    }

    public Target getTarget() {
        return target;
    }

    public Date getLastNotificationDate() {
        return lastNotificationDate;
    }

    private void autoCompleteName(Payload payload) {
        if (payload == null || payload.getName() == null) return;
        if (name == null) name = payload.getName();
        if (!name.contains(payload.getName()))
            name += ". " + payload.getName().trim();
        Utils.adaptToLength(name, SHORT_STRINGS_MAX_LENGTH) ;
    }

    private void autoCompleteProbes(Payload payload) {
        if (payload == null || payload.getProbe() == null) return;
        if (probes == null) probes = payload.getProbe().toString();
        if (!probes.contains(payload.getProbe().toString()))
            probes += ". " + payload.getProbe().toString().trim();
        Utils.adaptToLength(probes, SHORT_STRINGS_MAX_LENGTH) ;
    }

    private void autoCompleteEvidences(Payload payload) {
        if (payload == null || payload.getEvidence() == null) return;
        if (evidences == null) evidences = payload.getEvidence();
        if (!evidences.contains(payload.getEvidence()))
            evidences += ". " + payload.getEvidence().trim();
        Utils.adaptToLength(evidences, LONG_STRINGS_MAX_LENGTH) ;
    }

    private void autoCompleteURLs(Payload payload) {
        if (payload == null || payload.getURL() == null) return;
        if (linksToURLs == null) linksToURLs = payload.getURL();
        if (!linksToURLs.contains(payload.getURL()) && !((linksToURLs.length() + payload.getURL().length() + 50) > LONG_STRINGS_MAX_LENGTH))
            linksToURLs += ", " + toLink(payload.getURL());
    }

    private String toLink(String url) {
        if (url == null) return "";
        url = url.trim();
        String show = url.length() > 60 ? url.substring(0, 59) + "..." : url;
        return "<a href='" + url + "' target='_blank' style='color:black'>" + show + "</a>";
    }

    public String toJSON() {
        return "{"
                + "\"id\":" + getId()
                + ", \"firstNotificationDate\":" + getFirstNotificationDate().getTime()
                + (source != null ? ", \"source\":" + source.toJSON() : "")
                + (target != null ? ", \"target\":" + target.toJSON() : "")
                + (related != null ? ", \"related\":" + related.toJSON() : "")
                + ", \"name\": \"" + (name != null ? JSONObject.escape(name) : "") + "\""
                + ", \"probes\": \"" + (probes != null ? JSONObject.escape(probes) : "") + "\""
                + ", \"linksToURLs\": \"" + (linksToURLs != null ? JSONObject.escape(linksToURLs) : "") + "\""
                + ", \"evidences\": \"" + (evidences != null ? JSONObject.escape(evidences) : "") + "\""
                + ", \"totalNumberOfEvents\":" + numberOfEvents +
                "}";
    }

    public String toRedTeamJSON(int idForUnknownHost) {
        return "{"
                + "\"id\":" + getId() + ", \"firstNotificationDate\":" + getFirstNotificationDate().getTime()
                + ", \"sourceId\":" + (source != null && source.getHost() != null ? source.getHost().getId() : idForUnknownHost)
                + ", \"targetId\":" + (target != null && target.getHost() != null ? target.getHost().getId() : idForUnknownHost)
                + ", \"probes\": \"" + (probes != null ? JSONObject.escape(probes) : "") + "\""
                + ", \"name\": \"" + (name != null ? JSONObject.escape(name) : "") + "\""
                + ", \"totalNumberOfEvents\":" + numberOfEvents
                + ", \"firstEvidence\": \"" + (evidences != null ? JSONObject.escape(evidences) : "") + "\"" +
                "}";
    }
}
