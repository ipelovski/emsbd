package emsbj.lesson;

import emsbj.student.Student;
import emsbj.course.CourseStudent;

public class LessonStudent extends CourseStudent {
    private final Lesson lesson;
    private final double absence;

    public LessonStudent(Lesson lesson, Student student) {
        super(lesson.getCourse(), student);
        this.lesson = lesson;
        this.absence = student.getAbsences().stream()
            .filter(anAbsence -> anAbsence.getLesson().equals(lesson))
            .map(a -> a.getType().getValue())
            .findAny()
            .orElse(0.0);
    }

    public Lesson getLesson() {
        return lesson;
    }

    public double getAbsence() {
        return absence;
    }
}
