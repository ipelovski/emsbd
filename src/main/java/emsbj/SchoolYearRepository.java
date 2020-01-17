package emsbj;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SchoolYearRepository extends CrudRepository<SchoolYear, Integer> {
    Optional<SchoolYear> findByBeginYear(Integer beginYear);
}
