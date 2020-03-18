package emsbj.lesson;

import emsbj.absence.Absence;
import emsbj.student.Student;
import emsbj.course.CourseStudent;

public class LessonStudent extends CourseStudent {
    private Lesson lesson;
    private double absence;

    public LessonStudent(Lesson lesson, Student student) {
        super(lesson.getCourse(), student);
        this.absence = student.getAbsences().stream()
            .filter(anAbsence -> anAbsence.getLesson().equals(lesson))
            .map(Absence::getValue)
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
