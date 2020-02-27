package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Course implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(mappedBy = "course")
    private List<TeacherAssignment> teachers;
    @ManyToOne
    private SchoolClass schoolClass;
    @ManyToOne
    private Subject subject;
    @ManyToOne
    private Term term;
    @ManyToMany
    @JoinTable(
        name = "course_weekly_slots",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "weekly_slot_id"))
    private List<WeeklySlot> weeklySlots;
    @ManyToOne
    private Room room;

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

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public List<WeeklySlot> getWeeklySlots() {
        return weeklySlots;
    }

    public void setWeeklySlots(List<WeeklySlot> weeklySlots) {
        this.weeklySlots = weeklySlots;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
