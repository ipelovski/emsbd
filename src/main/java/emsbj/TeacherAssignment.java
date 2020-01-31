package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class TeacherAssignment implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Teacher teacher;
    @ManyToOne
    private SchoolClass schoolClass;
    @ManyToOne
    private Subject subject;
    @ManyToOne
    private Term term;
    @ManyToMany
    private List<WeeklySlot> weeklySlots;

    public TeacherAssignment() {

    }

    @Override
    public Long getId() {
        return id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
