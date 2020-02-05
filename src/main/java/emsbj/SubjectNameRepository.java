package emsbj;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubjectNameRepository extends CrudRepository<SubjectName, Long> {
    Optional<SubjectName> findByValue(String value);
}
