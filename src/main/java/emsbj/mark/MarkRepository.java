package emsbj.mark;

import emsbj.Grade;
import emsbj.Subject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarkRepository extends CrudRepository<Mark, Long> {
    List<Mark> findBySubjectAndStudentGrade(Subject subject, Grade grade);
}
