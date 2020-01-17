package emsbj;

import emsbj.mark.Mark;
import emsbj.user.User;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(
        cascade = { CascadeType.PERSIST })
    private User user;
    @ManyToOne
    private Grade grade;
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
        mappedBy = "student")
    private List<Mark> marks;

    protected Student() {

    }

    public Student(User user, Grade grade) {
        this.user = user;
        this.grade = grade;
        this.marks = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    public Grade getGrade() {
        return grade;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    @Override
    public String toString() {
        return super.toString() + " " + grade;
    }
}
