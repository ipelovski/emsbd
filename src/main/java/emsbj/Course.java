package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Course implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany
    private List<TeacherAssignment> teachers;
    @ManyToOne
    private SchoolClass schoolClass;
    @ManyToOne
    private Subject subject;
    @ManyToOne
    private Term term;
    @ManyToMany
    private List<WeeklySlot> weeklySlots;

    public Course() {

    }

    @Override
    public Long getId() {
        return id;
    }

    public List<TeacherAssignment> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherAssignment> teachers) {
        this.teachers = teachers;
    }
}
