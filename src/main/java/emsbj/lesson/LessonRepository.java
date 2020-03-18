package emsbj.lesson;

import emsbj.teacher.Teacher;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    Iterable<Lesson> findAllByCourseTeacher(Teacher teacher);
    Iterable<Lesson> findAllByCourseTeacherAndBeginIsBetween(
        Teacher teacher, LocalDateTime from, LocalDateTime to);
}
