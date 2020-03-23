package emsbj.course;

import emsbj.schoolclass.SchoolClass;
import emsbj.term.Term;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {
    Iterable<Course> findAllBySchoolClassAndTerm(SchoolClass schoolClass, Term term);
}
