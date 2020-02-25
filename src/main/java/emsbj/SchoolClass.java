package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class SchoolClass implements JournalPersistable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String Name;
    @ManyToOne
    private SchoolYear beginningSchoolYear;
    @ManyToOne
    private Grade beginningGrade;
    @ManyToOne
    private SchoolYear currentSchoolYear;
    @ManyToOne
    private Grade currentGrade;
    @ManyToOne
    private Teacher formMaster;
    @ManyToOne
    private Room classRoom;
    @OneToMany(mappedBy = "schoolClass")
    private List<Student> students;
    private Integer shift;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public SchoolYear getBeginningSchoolYear() {
        return beginningSchoolYear;
    }

    public void setBeginningSchoolYear(SchoolYear beginningSchoolYear) {
        this.beginningSchoolYear = beginningSchoolYear;
    }

    public Grade getBeginningGrade() {
        return beginningGrade;
    }

    public void setBeginningGrade(Grade beginningGrade) {
        this.beginningGrade = beginningGrade;
    }

    public SchoolYear getCurrentSchoolYear() {
        return currentSchoolYear;
    }

    public void setCurrentSchoolYear(SchoolYear currentSchoolYear) {
        this.currentSchoolYear = currentSchoolYear;
    }

    public Grade getCurrentGrade() {
        return currentGrade;
    }

    public void setCurrentGrade(Grade currentGrade) {
        this.currentGrade = currentGrade;
    }

    public Teacher getFormMaster() {
        return formMaster;
    }

    public void setFormMaster(Teacher formMaster) {
        this.formMaster = formMaster;
    }

    public Room getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(Room classRoom) {
        this.classRoom = classRoom;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Integer getShift() {
        return shift;
    }

    public void setShift(Integer shift) {
        this.shift = shift;
    }

    public Grade getGrade() {
        if (currentGrade != null) {
            return currentGrade;
        } else {
            return beginningGrade;
        }
    }
}
