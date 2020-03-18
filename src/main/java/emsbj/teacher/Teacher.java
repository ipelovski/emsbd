package emsbj.teacher;

import emsbj.JournalPersistable;
import emsbj.course.Course;
import emsbj.schoolclass.SchoolClass;
import emsbj.subject.Subject;
import emsbj.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Teacher extends JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private User user;
    @ManyToMany
    private List<Subject> skills = new ArrayList<>();
    @OneToMany(mappedBy = "teacher")
    private List<Course> courses = new ArrayList<>();
    @OneToMany(mappedBy = "formMaster")
    private List<SchoolClass> formMasterOf = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Subject> getSkills() {
        return skills;
    }

    public void setSkills(List<Subject> skills) {
        this.skills = skills;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<SchoolClass> getFormMasterOf() {
        return formMasterOf;
    }

    public void setFormMasterOf(List<SchoolClass> formMasterOf) {
        this.formMasterOf = formMasterOf;
    }
}
