package emsbj;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedEntityGraph(name="SchoolYear.all", includeAllAttributes = true)
public class SchoolYear implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int beginYear;
    private int endYear;
    @OneToMany(mappedBy = "schoolYear", fetch = FetchType.LAZY)
    private List<Term> terms = new ArrayList<>(2);

    protected SchoolYear() {

    }

    public SchoolYear(int beginYear, int endYear) {
        this.beginYear = beginYear;
        this.endYear = endYear;
    }

    @Override
    public Long getId() {
        return id;
    }

    public int getBeginYear() {
        return beginYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public String name() {
        return beginYear + " / " + endYear;
    }

    public List<Term> getTerms() {
        return terms;
    }
}
