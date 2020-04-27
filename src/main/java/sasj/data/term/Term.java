package sasj.data.term;

import sasj.data.JournalPersistable;
import sasj.data.schoolyear.SchoolYear;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class Term extends JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @NotNull
    @JoinColumn(name = "fk_schoolYear", nullable = false)
    private SchoolYear schoolYear;
    @NotNull
    private String name;
    private LocalDate begin;
    private LocalDate end;

    protected Term() {

    }

    public Term(SchoolYear schoolYear, String name) {
        this.schoolYear = schoolYear;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public SchoolYear getSchoolYear() {
        return schoolYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
