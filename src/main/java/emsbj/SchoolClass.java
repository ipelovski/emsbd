package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class SchoolClass implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Term term;
    @ManyToOne
    private Subject subject;
    @ManyToOne
    private Grade grade;
    @ManyToOne
    private Teacher teacher;

    public Long getId() {
        return id;
    }
}
