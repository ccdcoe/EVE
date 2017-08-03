package eve.model.event;

import eve.model.infra.Host;
import eve.var.Utils;
import eve.wuti.jpa.BasicEntity;
import org.json.simple.JSONObject;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;


/**
 * Created by jesus on 11/14/16.
 */
@Entity
public class Source extends BasicEntity {
    @Basic
    @Column(length = 50)
    private String IPV4;

    @Basic
    @Column(length = 50)
    private String IPV6;

    @OneToOne
    private Host host;

    public Source() {
    }

    public Source(String IPV4, String IPV6, Host host) {
        this.IPV4 = Utils.adaptToLength(IPV4, 50);
        this.IPV6 = Utils.adaptToLength(IPV6, 50);
        this.host = host;
    }

    public Source(Source source) {
        this(source.getIPV4(), source.getIPV6(), source.getHost());
    }


    public void autoComplete(Source other) {
        if (Utils.isEmpty(IPV4) && !Utils.isEmpty(other.IPV4)) IPV4 = other.IPV4;
        if (Utils.isEmpty(IPV6) && !Utils.isEmpty(other.IPV6)) IPV6 = other.IPV6;
    }

    public static Source getFromJSON(JSONObject jsonObject) throws Exception {

        if (jsonObject == null) return null;

        String ipv4 = (String) jsonObject.get("IPV4");
        String ipv6 = (String) jsonObject.get("IPV6");

        Host host = Host.find(ipv4, ipv6, "");

        return new Source(ipv4, ipv6, host);
    }


    public boolean isEquivalentTo(Source other) {
        if (other == null) return false;
        if (other.equals(this)) return true;
        if (!Utils.isEquivalent(IPV4, other.IPV4)) return false;
        if (!Utils.isEquivalent(IPV6, other.IPV6)) return false;
        return true;
    }

    public String getIPV4() {
        return IPV4;
    }

    public String getIPV6() {
        return IPV6;
    }

    public void setIPV4(String IPV4) {
        this.IPV4 = IPV4;
    }

    public void setIPV6(String IPV6) {
        this.IPV6 = IPV6;
    }

    public Host getHost() {
        return host;
    }

    public String toJSON() {
        return "{"
                + "\"IPV4\":" + "\"" + JSONObject.escape(IPV4) + "\"" + ",\"IPV6\":" + "\"" + JSONObject.escape(IPV6) + "\""
                + ", \"x\":" + (host != null ? host.getX() : -1000) + ", \"y\":" + (host != null ? host.getY() : -1000) +
                "}";
    }

}
