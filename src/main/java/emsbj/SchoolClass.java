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
    private String Name;
    @ManyToOne
    private Grade grade;
    @ManyToOne
    private TeacherAssignment formMaster;

    @Override
    public Long getId() {
        return id;
    }
}
