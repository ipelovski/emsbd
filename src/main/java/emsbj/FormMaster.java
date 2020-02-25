package emsbj;

public class FormMaster {
    private Teacher teacher;

    public FormMaster(Teacher teacher, long count, String firstName, String middleName, String lastName) {
        this.teacher = teacher;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
