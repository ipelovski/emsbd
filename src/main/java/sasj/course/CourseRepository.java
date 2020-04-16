package sasj.course;

import sasj.schoolclass.SchoolClass;
import sasj.term.Term;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {
    Iterable<Course> findAllBySchoolClassAndTerm(SchoolClass schoolClass, Term term);
}
