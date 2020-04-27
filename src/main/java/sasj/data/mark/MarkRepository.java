package sasj.data.mark;

import sasj.data.schoolclass.SchoolClass;
import sasj.data.subject.Subject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarkRepository extends CrudRepository<Mark, Long> {
    List<Mark> findBySubjectAndStudentSchoolClass(Subject subject, SchoolClass schoolClass);
}
