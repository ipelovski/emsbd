package emsbd;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SchoolYear schoolYear;
    private String name;
    private Instant begin;
    private Instant end;

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

    public Instant getBegin() {
        return begin;
    }

    public void setBegin(Instant begin) {
        this.begin = begin;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }
}
