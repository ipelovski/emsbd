package sasj.controller.teacher.lesson;

import sasj.data.absence.Absence;
import sasj.data.lesson.Lesson;
import sasj.data.student.Student;
import sasj.controller.student.course.CourseStudent;

public class LessonStudent extends CourseStudent {
    private final Lesson lesson;
    private final Absence absence;

    public LessonStudent(Lesson lesson, Student student) {
        super(lesson.getCourse(), student);
        this.lesson = lesson;
        this.absence = student.getAbsences().stream()
            .filter(anAbsence -> anAbsence.getLesson().equals(lesson))
            .findAny()
            .orElse(null);
    }

    public Lesson getLesson() {
        return lesson;
    }

    public Absence getAbsence() {
        return absence;
    }
}
