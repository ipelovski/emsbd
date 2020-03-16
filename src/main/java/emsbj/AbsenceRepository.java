package emsbj;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AbsenceRepository extends CrudRepository<Absence, Long> {
    Optional<Absence> findByStudentAndLesson(Student student, Lesson lesson);
}
