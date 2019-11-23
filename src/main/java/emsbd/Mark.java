package emsbd;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.Instant;

@Entity
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @CreatedBy
    @ManyToOne
    private User createdBy;
    @CreatedDate
    private Instant createdOn;
    @LastModifiedBy
    @ManyToOne
    private User updatedBy;
    @LastModifiedDate
    private Instant updatedOn;
    @ManyToOne
    private Student student;
    @ManyToOne
    private Subject subject;
    @Min(200)
    @Max(600)
    private short score;

    protected Mark() {

    }

    public Mark(Student student, Subject subject, int score) {
        this.student = student;
        this.subject = subject;
        setScore(score);
    }

    public Long getId() {
        return id;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public Student getStudent() {
        return student;
    }

    public Subject getSubject() {
        return subject;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = (short) score;
    }
}
