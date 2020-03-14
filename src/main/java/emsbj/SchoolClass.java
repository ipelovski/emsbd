package emsbj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

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
    private Teacher formMaster;
    @ManyToOne
    private Room classRoom;
    @OneToMany(mappedBy = "schoolClass")
    private List<Student> students;
    @OneToMany(mappedBy = "schoolClass")
    private List<SchoolClassTermShift> shifts;

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

    public List<SchoolClassTermShift> getShifts() {
        return shifts;
    }

    public void setShifts(List<SchoolClassTermShift> shifts) {
        this.shifts = shifts;
    }

    public int getShift() {
        School school = School.getInstance();
        return getShifts().stream()
            .filter(schoolClassTermShift ->
                Objects.equals(schoolClassTermShift.getTerm().getId(), school.getTerm().getId()))
            .findAny()
            .orElseThrow(() -> new RuntimeException("no shift found"))
            .getShift();
    }

    public Grade getGrade() {
        School school = School.getInstance();
        int offset = school.getSchoolYear().getBeginYear()
            - beginningSchoolYear.getBeginYear();
        return school.getGrades().get(beginningGrade.getOrdinal() + offset);
    }
}
