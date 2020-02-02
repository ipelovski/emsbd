package emsbj;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubjectRepository extends CrudRepository<Subject, Long> {
    @Query("select s from Subject as s " +
               "left join SubjectName as sn on s.name = sn.id " +
               "left join Grade as g on s.grade = g.id " +
               "where sn.value = :subjectName " +
               "and g.name = :gradeName")
    Optional<Subject> findByNameAndGrade(
        @Param("subjectName") String subjectName,
        @Param("gradeName") String gradeName);
}
