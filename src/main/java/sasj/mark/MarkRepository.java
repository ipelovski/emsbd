package sasj.mark;

import sasj.schoolclass.SchoolClass;
import sasj.subject.Subject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarkRepository extends CrudRepository<Mark, Long> {
    List<Mark> findBySubjectAndStudentSchoolClass(Subject subject, SchoolClass schoolClass);
}
