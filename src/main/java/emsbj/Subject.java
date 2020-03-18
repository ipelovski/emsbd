package emsbj;

import emsbj.user.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name_id", "grade_id"})
})
public class Subject extends JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(optional = false)
    private SubjectName name;
    @ManyToOne
    @NotNull
    private Grade grade;
    @CreatedBy
    @ManyToOne
    private User createdBy;
    @CreatedDate
    private Instant createdOn;

    public Subject() {

    }

    public Subject(SubjectName name, Grade grade) {
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

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }
}
