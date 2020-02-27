package emsbj;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;

public interface CourseRepository extends CrudRepository<Course, Long> {
    @Query("select new emsbj.Lesson(c, ws) from Course c" +
        " left join c.weeklySlots ws" +
        " where c.teacher = :teacher" +
        " and ws.day = :dayOfWeek" +
        " and ws.begin > :currentTime")
    Iterable<Lesson> findAllForToday(
        @Param("teacher") Teacher teacher,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("currentTime") LocalTime currentTime);
}
