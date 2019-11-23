package emsbd;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TermRepository extends CrudRepository<Term, Long> {
    Optional<Term> findBySchoolYearAndName(SchoolYear schoolYear, String name);
}
