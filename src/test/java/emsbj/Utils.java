package emsbj;

import emsbj.user.User;
import org.junit.Assert;

public class Utils {
    public static Student createStudent(String firstName, String middleName, String lastName, Grade grade) {
        Student student = new Student(
            new User(firstName.toLowerCase() + "_" + lastName.toLowerCase())
        );
        student.getUser().getPersonalInfo()
            .setFirstName(firstName)
            .setMiddleName(middleName)
            .setLastName(lastName);
        return student;
    }

    public static void assertFails(Class<?> exceptionClass, Runnable task) {
        try {
            task.run();
            Assert.fail();
        } catch(Exception e) {
            Assert.assertTrue(
                "Expected exception of type " + exceptionClass.getCanonicalName()
                + " but caught one of type " + e.getClass().getCanonicalName(),
                exceptionClass.isInstance(e));
        }
    }
}
