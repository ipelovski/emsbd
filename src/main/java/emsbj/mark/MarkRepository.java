package emsbj.mark;

import emsbj.schoolclass.SchoolClass;
import emsbj.subject.Subject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarkRepository extends CrudRepository<Mark, Long> {
    List<Mark> findBySubjectAndStudentSchoolClass(Subject subject, SchoolClass schoolClass);
}
