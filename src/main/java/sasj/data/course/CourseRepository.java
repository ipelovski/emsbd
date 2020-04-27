package sasj.data.course;

import sasj.data.schoolclass.SchoolClass;
import sasj.data.term.Term;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {
    Iterable<Course> findAllBySchoolClassAndTerm(SchoolClass schoolClass, Term term);
}
