package emsbj;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Grade implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(
        cascade = { CascadeType.PERSIST })
    private GradeName name;
    @ManyToOne
    private SchoolYear schoolYear;

    public Grade() {

    }

    public Grade(SchoolYear schoolYear, GradeName name) {
        this.schoolYear = schoolYear;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public GradeName getName() {
        return name;
    }

    public SchoolYear getSchoolYear() {
        return schoolYear;
    }
}
