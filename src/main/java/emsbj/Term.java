package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Term implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SchoolYear schoolYear;
    private String name;
    private LocalDate begin;
    private LocalDate end;

    protected Term() {

    }

    public Term(SchoolYear schoolYear, String name) {
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

    public LocalDate getBegin() {
        return begin;
    }

    public void setBegin(LocalDate begin) {
        this.begin = begin;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public String name() {
        return schoolYear.name() + " " + name;
    }
}
