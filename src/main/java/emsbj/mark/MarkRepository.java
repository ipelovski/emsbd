package emsbj.mark;

import emsbj.Grade;
import emsbj.SchoolClass;
import emsbj.Subject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarkRepository extends CrudRepository<Mark, Long> {
    List<Mark> findBySubjectAndStudentSchoolClass(Subject subject, SchoolClass schoolClass);
}
