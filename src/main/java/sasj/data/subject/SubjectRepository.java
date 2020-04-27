package sasj.data.subject;

import sasj.data.grade.Grade;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubjectRepository extends CrudRepository<Subject, Long> {
    @Query("select s from Subject as s " +
               "left join SubjectName as sn on s.name = sn.id " +
               "where sn.value = :subjectName " +
               "and s.grade = :grade")
    Optional<Subject> findByNameAndGrade(
        @Param("subjectName") String subjectName,
        @Param("grade") Grade grade);
}
