package emsbj.course;

import emsbj.lesson.Lesson;
import emsbj.schoolclass.SchoolClass;
import emsbj.teacher.Teacher;
import emsbj.term.Term;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;

public interface CourseRepository extends CrudRepository<Course, Long> {
    @Query("select new emsbj.lesson.Lesson(c, ws) from Course c" +
        " left join c.weeklySlots ws" +
        " where c.teacher = :teacher" +
        " and c.term = :term")
    Iterable<Lesson> findAllByTeacher(Teacher teacher, Term term);
    @Query("select new emsbj.lesson.Lesson(c, ws) from Course c" +
        " left join c.weeklySlots ws" +
        " where c.schoolClass = :schoolClass" +
        " and c.term = :term")
    Iterable<Lesson> findAllBySchoolClass(SchoolClass schoolClass, Term term);
    @Query("select new emsbj.lesson.Lesson(c, ws) from Course c" +
        " left join c.weeklySlots ws" +
        " where c.teacher = :teacher" +
        " and c.term = :term" +
        " and ws.day = :dayOfWeek")
    Iterable<Lesson> findAllByTeacherAndDay(Teacher teacher, DayOfWeek dayOfWeek, Term term);
}
