package sasj.grade;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GradeRepository extends CrudRepository<Grade, Long> {
    Optional<Grade> findByName(String name);
    Iterable<Grade> findByOrderByOrdinalAsc();
}
