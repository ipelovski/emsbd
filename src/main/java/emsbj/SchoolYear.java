package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SchoolYear {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int beginYear;
    private int endYear;

    protected SchoolYear() {

    }

    public SchoolYear(int beginYear, int endYear) {
        this.beginYear = beginYear;
        this.endYear = endYear;
    }

    public Long getId() {
        return id;
    }

    public int getBeginYear() {
        return beginYear;
    }

    public int getEndYear() {
        return endYear;
    }
}
