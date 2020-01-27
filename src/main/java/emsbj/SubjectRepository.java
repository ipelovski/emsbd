package emsbj;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubjectRepository extends CrudRepository<Subject, Long> {
    Iterable<Subject> findByTerm(Term term);
    Optional<Subject> findByTermAndName(Term term, String name);
}
