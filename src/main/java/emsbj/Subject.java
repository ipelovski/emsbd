package emsbj;

import emsbj.user.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class Subject implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(
        cascade = { CascadeType.PERSIST })
    private SubjectName name;
    @ManyToOne
    private GradeName grade;
    @CreatedBy
    @ManyToOne
    private User createdBy;
    @CreatedDate
    private Instant createdOn;

    public Subject() {

    }

    public Subject(SubjectName name) {
        this.name = name;
    }

    public Subject(SubjectName name, GradeName grade) {
        this.name = name;
        this.grade = grade;
    }

    @Override
    public Long getId() {
        return id;
    }

    public SubjectName getName() {
        return name;
    }

    public void setName(SubjectName name) {
        this.name = name;
    }

    public GradeName getGrade() {
        return grade;
    }

    public void setGrade(GradeName grade) {
        this.grade = grade;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }
}
