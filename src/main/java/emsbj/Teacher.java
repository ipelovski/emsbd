package emsbj;

import emsbj.user.User;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Teacher implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private User user;
    @ManyToMany
    private List<Subject> skills = new ArrayList<>();
    @OneToMany(mappedBy = "teacher")
    private List<TeacherAssignment> assignments = new ArrayList<>();
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

    public List<TeacherAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<TeacherAssignment> assignments) {
        this.assignments = assignments;
    }

    public List<SchoolClass> getFormMasterOf() {
        return formMasterOf;
    }

    public void setFormMasterOf(List<SchoolClass> formMasterOf) {
        this.formMasterOf = formMasterOf;
    }
}
