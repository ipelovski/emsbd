package sasj.data.student;

import sasj.data.absence.Absence;
import sasj.data.JournalPersistable;
import sasj.data.mark.Mark;
import sasj.data.note.Note;
import sasj.data.schoolclass.SchoolClass;
import sasj.data.user.HasUser;
import sasj.data.user.User;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends JournalPersistable implements HasUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @ManyToOne
    private User user;
    @ManyToOne
    private SchoolClass schoolClass;
    private Integer number;
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
        mappedBy = "student")
    private List<Mark> marks;
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
        mappedBy = "student")
    private List<Note> notes;
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
        mappedBy = "student")
    private List<Absence> absences;

    public Student() {
        this.marks = new ArrayList<>();
        this.notes = new ArrayList<>();
    }

    public Student(User user) {
        this();
        this.user = user;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<Absence> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Absence> absences) {
        this.absences = absences;
    }
}
