package emsbd;

public class Utils {
    public static Student createStudent(String firstName, String middleName, String lastName, Grade grade) {
        Student student = new Student(
            new User(firstName.toLowerCase() + "_" + lastName.toLowerCase()),
            grade);
        student.getUser().getPersonalInfo()
            .setFirstName(firstName)
            .setMiddleName(middleName)
            .setLastName(lastName);
        return student;
    }
}
