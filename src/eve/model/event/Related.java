package eve.model.event;

import eve.model.infra.Host;
import eve.var.Utils;
import eve.wuti.jpa.BasicEntity;
import org.json.simple.JSONObject;

import javax.persistence.*;

/**
 * Created by jesus on 11/14/16.
 */
@Entity
public class Related extends BasicEntity {
    public static enum Type {honeystuff, other}

    @Enumerated(EnumType.STRING)
    private Type type;

    @Basic
    @Column(length = 50)
    private String IPV4;

    @Basic
    @Column(length = 50)
    private String IPV6;

    @Basic
    @Column(length = 500)
    private String description;

    @OneToOne
    private Host host;

    public Related() {
    }

    public Related(Type type, String IPV4, String IPV6, String description, Host host) {
        this.type = type;
        this.IPV4 = Utils.adaptToLength(IPV4, 50);
        this.IPV6 = Utils.adaptToLength(IPV6, 50);
        this.description = Utils.adaptToLength(description, 500);
        this.host = host;
    }

    public Related(Related related) {
        this (related.getType(), related.getIPV4(), related.getIPV6(), related.getDescription(), related.getHost()) ;
    }

    public static Related getFromJSON(JSONObject jsonObject) throws Exception {

        if (jsonObject == null) return null;

        Type type;
        try {
            type = Type.valueOf(((String) jsonObject.get("type")).toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Unknown related type: " + jsonObject.get("type"));
        }

        String ipv4 = (String) jsonObject.get("IPV4");
        String ipv6 = (String) jsonObject.get("IPV6");
        String description = (String) jsonObject.get("description");

        Host host = Host.find(ipv4, ipv6, description);

        return new Related(type, ipv4, ipv6, description, host);
    }

    public boolean isEquivalentTo(Related other) {
        if (other == null) return false;
        if (other.equals(this)) return true;
        if (!Utils.isEquivalent(IPV4, other.IPV4)) return false;
        if (!Utils.isEquivalent(IPV6, other.IPV6)) return false;
        return true;
    }

    public Type getType() {
        return type;
    }

    public String getIPV4() {
        return IPV4;
    }

    public String getIPV6() {
        return IPV6;
    }

    public String getDescription() {
        return description;
    }

    public Host getHost() {
        return host;
    }

    public String toJSON() {
        return "{"
                + "\"type\":" + "\"" + type + "\"" + ",\"description\":" + "\"" + JSONObject.escape(description) + "\""
                + ",\"network\":" + "\"" + (host != null ? JSONObject.escape(host.getNetwork().getName()) : "") + "\""
                + ",\"IPV4\":" + "\"" + JSONObject.escape(IPV4) + "\"" + ",\"IPV6\":" + "\"" + JSONObject.escape(IPV6) + "\""
                + ", \"x\":" + (host != null ? host.getX() : -1000) + ", \"y\":" + (host != null ? host.getY() : -1000) +
                "}";
    }
}
