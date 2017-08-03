package eve.model.infra;


import eve.wuti.jpa.DAO;

import javax.persistence.*;
import java.util.List;

/**
 * Created by jesus on 11/14/16.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Host.findByIPV4", query = "SELECT h FROM Host h where IPV4 = :ip"),
        @NamedQuery(name = "Host.findByIPV6", query = "SELECT h FROM Host h where IPV6 = :ip"),
        @NamedQuery(name = "Host.findByName", query = "SELECT h FROM Host h where name = :name"),
})
public class Host {

    public static enum Type {
        redTeamLinux, redTeamWindows, serverLinux, serverWindows, workStationWindows, drone, honeyPot
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Basic
    private String name;

    @Basic
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Basic
    private String IPV4;

    @Basic
    private String IPV6;

    @OneToOne
    private Network network;

    @Basic
    private int x, y; // Coordinates of host in XS general map


    public Host() {
    }

    public Host(String name, Type type, String IPV4, String IPV6, Network network, int x, int y) {
        this(name, (network != null ? name + "."+  network.getName().toLowerCase() + ".clf.ex" : name) , type, IPV4, IPV6, network, x, y);
    }

    public Host(String name, String fullName, Type type, String IPV4, String IPV6, Network network, int x, int y) {
        this.name = name;
        this.fullName = fullName;
        this.type = type;
        this.IPV4 = IPV4;
        this.IPV6 = IPV6;
        this.network = network;
        this.x = x;
        this.y = y;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
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

    public Network getNetwork() {
        return network;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Host{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", type=" + type +
                ", IPV4='" + IPV4 + '\'' +
                ", IPV6='" + IPV6 + '\'' +
                ", subnet=" + network +
                '}';
    }

    public boolean isRedTeam() {
        return type != null && (type == Type.redTeamLinux || type == Type.redTeamWindows);
    }

    public static Host find (String ipv4, String ipv6, String name)  {
        Host host = findByIPV4(ipv4);
        if (host != null) return host;
        host = findByIPV6(ipv6) ;
        if (host != null) return host ;
        return findByName(name);
    }

    public static Host findByIPV4(String ip) {
        if (ip == null) return null;
        ip = ip.trim().toLowerCase();
        List<Host> hosts = DAO.get().getList(Host.class, "Host.findByIPV4", "ip", ip);
        if (hosts != null && hosts.size() > 0) return hosts.get(0);
        return null;
    }

    public static Host findByIPV6(String ip) {
        if (ip == null) return null;
        ip = ip.trim().toLowerCase();
        List<Host> hosts = DAO.get().getList(Host.class, "Host.findByIPV6", "ip", ip);
        if (hosts != null && hosts.size() > 0) return hosts.get(0);
        return null;
    }

    public static Host findByName(String name) {
        if (name == null) return null;

        name = name.trim().toLowerCase();
        if (name.endsWith(".ex")) name = name.substring(0, name.lastIndexOf(".ex"));

        List<Host> hosts = DAO.get().getList(Host.class, "Host.findByName", "name", name);
        if (hosts != null && hosts.size() > 0) return hosts.get(0);
        return null;
    }

}
