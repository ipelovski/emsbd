package sasj.data.mark;

import sasj.data.JournalPersistable;
import sasj.data.lesson.Lesson;
import sasj.data.student.Student;
import sasj.data.subject.Subject;
import sasj.data.teacher.Teacher;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
public class Mark extends JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Teacher setBy;
    @CreatedDate
    private LocalDateTime createdOn;
    @ManyToOne
    private Student student;
    @ManyToOne
    private Subject subject;
    // can be null. for example the mark is from exam
    @ManyToOne
    private Lesson lesson;
    @Min(200)
    @Max(600)
    private short rawScore;
    // TODO
    private boolean isFinal;

    protected Mark() {

    }

    public Mark(Student student, Subject subject, int rawScore) {
        this.student = student;
        this.subject = subject;
        setRawScore(rawScore);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Teacher getSetBy() {
        return setBy;
    }

    public void setSetBy(Teacher setBy) {
        this.setBy = setBy;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public Student getStudent() {
        return student;
    }

    public Subject getSubject() {
        return subject;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public int getRawScore() {
        return rawScore;
    }

    public void setRawScore(int rawScore) {
        this.rawScore = (short) rawScore;
    }

    public double getScore() {
        return rawScore / 100.0;
    }

    public void setScore(double score) {
        this.rawScore = (short) (score * 100);
    }
}
