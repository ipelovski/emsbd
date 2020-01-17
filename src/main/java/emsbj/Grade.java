package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SchoolYear schoolYear;
    private String name;

    protected Grade() {

    }

    public Grade(SchoolYear schoolYear, String name) {
        this.schoolYear = schoolYear;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public SchoolYear getSchoolYear() {
        return schoolYear;
    }

    public String getName() {
        return name;
    }
}
