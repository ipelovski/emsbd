package emsbj;

import emsbj.grade.Grade;
import org.junit.Assert;

public class TestUtils {
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

    public static Grade createGrade(int ordinal) {
        Grade grade = new Grade(ordinal);
        grade.setOrdinal(ordinal);
        return grade;
    }
}
