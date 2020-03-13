package emsbj;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;

public interface CourseRepository extends CrudRepository<Course, Long> {
    @Query("select new emsbj.Lesson(c, ws) from Course c" +
        " left join c.weeklySlots ws" +
        " where c.teacher = :teacher")
    Iterable<Lesson> findAllByTeacher(Teacher teacher);
    @Query("select new emsbj.Lesson(c, ws) from Course c" +
        " left join c.weeklySlots ws" +
        " where c.schoolClass = :schoolClass")
    Iterable<Lesson> findAllBySchoolClass(SchoolClass schoolClass);
    @Query("select new emsbj.Lesson(c, ws) from Course c" +
        " left join c.weeklySlots ws" +
        " where c.teacher = :teacher" +
        " and ws.day = :dayOfWeek")
    Iterable<Lesson> findAllByTeacherAndDay(
        @Param("teacher") Teacher teacher,
        @Param("dayOfWeek") DayOfWeek dayOfWeek);
}
