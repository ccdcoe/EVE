package eve.model.infra;


import eve.wuti.jpa.BasicEntity;

import javax.persistence.*;

/**
 * Created by jesus on 11/14/16.
 */
@Entity
public class Network extends BasicEntity {

    @Basic
    private String name;

    @Basic
    private String IPV4;

    @Basic
    private String IPV6;


    public Network() {
    }

    public Network(String name, String IPV4, String IPV6) {
        this.name = name;
        this.IPV4 = IPV4;
        this.IPV6 = IPV6;
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

    @Override
    public String toString() {
        return "Network{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                ", IPV4='" + IPV4 + '\'' +
                ", IPV6='" + IPV6 + '\'' +
                '}';
    }


}
