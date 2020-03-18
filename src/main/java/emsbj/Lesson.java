package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Lesson extends JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Course course;
    @ManyToOne
    private WeeklySlot weeklySlot;
    private LocalDateTime begin;

    public Lesson() {}

    public Lesson(Course course, WeeklySlot weeklySlot) {
        this.course = course;
        this.weeklySlot = weeklySlot;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public WeeklySlot getWeeklySlot() {
        return weeklySlot;
    }

    public void setWeeklySlot(WeeklySlot weeklySlot) {
        this.weeklySlot = weeklySlot;
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public void setBegin(LocalDateTime begin) {
        this.begin = begin;
    }
}
