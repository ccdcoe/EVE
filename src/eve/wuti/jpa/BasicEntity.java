package eve.wuti.jpa;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Creado por Jesus el 23/6/15.
 */
@MappedSuperclass
public class BasicEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BasicEntity basicEntity = (BasicEntity) obj;

        return id == basicEntity.id && id != 0;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
