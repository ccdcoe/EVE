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
public class Target extends BasicEntity {
    public static enum Type {host, network}

    @Enumerated(EnumType.STRING)
    private Type type;

    @Basic
    @Column(length = 50)
    private String name;

    @Basic
    @Column(length = 50)
    private String IPV4;

    @Basic
    @Column(length = 50)
    private String IPV6;

    @OneToOne
    private Host host;

    public Target() {
    }

    public Target(Type type, String name, String IPV4, String IPV6, Host host) {
        this.type = type;
        this.name = Utils.adaptToLength(name, 50);
        this.IPV4 = Utils.adaptToLength(IPV4, 50);
        this.IPV6 = Utils.adaptToLength(IPV6, 50);
        this.host = host;
    }

    public Target(Target target) {
        this(target.getType(), target.getName(), target.getIPV4(), target.getIPV6(), target.getHost());
    }

    public void autoComplete(Target other) {
        if (Utils.isEmpty(name) && !Utils.isEmpty(other.name)) name = other.name;
        if (Utils.isEmpty(IPV4) && !Utils.isEmpty(other.IPV4)) IPV4 = other.IPV4;
        if (Utils.isEmpty(IPV6) && !Utils.isEmpty(other.IPV6)) IPV6 = other.IPV6;
    }

    public static Target getFromJSON(JSONObject jsonObject) throws Exception {

        if (jsonObject == null) return null;

        Type type;
        try {
            type = Type.valueOf(((String) jsonObject.get("type")).toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Unknown target type: " + jsonObject.get("type"));
        }

        String name = (String) jsonObject.get("name");
        String ipv4 = (String) jsonObject.get("IPV4");
        String ipv6 = (String) jsonObject.get("IPV6");
        Host host = Host.find(ipv4, ipv6, name);

        return new Target(type, name, ipv4, ipv6, host);
    }

    public boolean isEquivalentTo(Target other) {
        if (other == null) return false;
        if (other.equals(this)) return true;
        if (!Utils.isEquivalent(IPV4, other.IPV4)) return false;
        if (!Utils.isEquivalent(IPV6, other.IPV6)) return false;
        return true;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getIPV4() {
        return IPV4;
    }

    public String getIPV6() {
        return IPV6;
    }

    public Host getHost() {
        return host;
    }

    public String toJSON() {
        return "{"
                + "\"type\":" + "\"" + type + "\"" + ",\"name\":" + "\"" + JSONObject.escape(host != null ? host.getFullName() : IPV4) + "\""
                + ",\"network\":" + "\"" + JSONObject.escape(host != null ? host.getNetwork().getName() : "") + "\""
                + ",\"IPV4\":" + "\"" + JSONObject.escape(IPV4) + "\"" + ",\"IPV6\":" + "\"" + JSONObject.escape(IPV6) + "\""
                + ", \"x\":" + (host != null ? host.getX() : -1000) + ", \"y\":" + (host != null ? host.getY() : -1000) +
                "}";
    }
}
