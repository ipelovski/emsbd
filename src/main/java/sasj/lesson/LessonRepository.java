package sasj.lesson;

import sasj.schoolclass.SchoolClass;
import sasj.teacher.Teacher;
import sasj.term.Term;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    Iterable<Lesson> findAllByCourseTeacherAndBeginIsBetween(
        Teacher teacher, LocalDateTime from, LocalDateTime to);
    @Query("select new sasj.lesson.Lesson(c, ws) from Course c" +
        " left join c.weeklySlots ws" +
        " where c.schoolClass = :schoolClass" +
        " and c.term = :term")
    Iterable<Lesson> findAllBySchoolClass(SchoolClass schoolClass, Term term);
    @Query("select new sasj.lesson.Lesson(c, ws) from Course c" +
        " left join c.weeklySlots ws" +
        " where c.teacher = :teacher" +
        " and c.term = :term")
    Iterable<Lesson> findAllByTeacher(Teacher teacher, Term term);
    @Query("select new sasj.lesson.Lesson(c, ws) from Course c" +
        " left join c.weeklySlots ws" +
        " where c.teacher = :teacher" +
        " and c.term = :term" +
        " and ws.day = :dayOfWeek")
    Iterable<Lesson> findAllByTeacherAndDay(Teacher teacher, DayOfWeek dayOfWeek, Term term);
}
