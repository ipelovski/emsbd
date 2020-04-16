package sasj.absence;

import sasj.lesson.Lesson;
import sasj.student.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * A Spring Data repository for working with entities of type {@link Absence}.
 */
public interface AbsenceRepository extends CrudRepository<Absence, Long> {
    /**
     * Looks for information regarding the presence of a student during specific lesson.
     * @param student The student who is being checked for presence.
     * @param lesson The lesson during which the student is checked for presence.
     * @return information about the presence of a student. If the student is present
     * then an empty Optional object is returned. If the student is late or absent
     * then an Optional object with Absence is returned.
     */
    Optional<Absence> findByStudentAndLesson(Student student, Lesson lesson);
}
