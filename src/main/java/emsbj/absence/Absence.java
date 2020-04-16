package emsbj.absence;

import emsbj.JournalPersistable;
import emsbj.lesson.Lesson;
import emsbj.student.Student;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * When a student is late for a lesson or is not present then the teacher may
 * mark him as being late or absent. This is stored as an absence with information
 * about the {@link Student student} and in which {@link Lesson lesson} it happened.
 * The {@link #getType() type} property keeps information whether the student is late
 * or absent.
 */
@Entity
public class Absence extends JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Lesson lesson;
    @ManyToOne
    private Student student;
    private Type type;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        beingLate(0.5), absent(1.0);
        private final double value;
        Type(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }
}
