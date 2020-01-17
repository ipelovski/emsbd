package emsbj;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GradeRepository extends CrudRepository<Grade, Long> {
    Optional<Grade> findBySchoolYearAndName(SchoolYear schoolYear, String name);
}
