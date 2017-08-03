package eve.model.event;

import eve.var.Utils;
import eve.wuti.jpa.BasicEntity;
import org.json.simple.JSONObject;

import javax.persistence.*;

/**
 * Created by jesus on 11/14/16.
 */
@Entity
public class Payload extends BasicEntity {
    public static enum Probe {syslogs, snoopy, apache, honeystuff, ids, pcap, netflow, influx, other}

    @Basic
    @Column(length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    private Probe probe;

    @Basic
    @Column(length = 300)
    private String evidence;

    @Basic
    private String url;

    public Payload() {
    }

    public Payload(String name, Probe probe, String evidence, String url) {
        this.name = Utils.adaptToLength(name, 100);
        this.probe = probe;
        this.evidence = Utils.adaptToLength(evidence, 300);
        this.url = url;
    }

    public static Payload getFromJSON(JSONObject jsonObject) throws Exception {

        if (jsonObject == null) return null;

        Probe probe;
        try {
            probe = Probe.valueOf(((String) jsonObject.get("probe")).toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Unknown probe: " + jsonObject.get("probe"));
        }
        String name = (String) jsonObject.get("name");
        String evidence = (String) jsonObject.get("evidence");
        String url = (String) jsonObject.get("url");

        return new Payload(name, probe, evidence, url);
    }

    public Probe getProbe() {
        return probe;
    }

    public String getName() {
        return name;
    }

    public String getEvidence() {
        return evidence;
    }

    public String getURL() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toJSON() {
        return "{" + "\"name\":" + "\"" + JSONObject.escape(name) + "\"" + ", \"probe\":" + "\"" + JSONObject.escape(probe.toString()) +
                "\"" + ", \"evidence\":" + "\"" + JSONObject.escape(evidence) + "\"" + ", \"url\":" + "\"" + JSONObject.escape(url) + "}";
    }
}
