package emsbj;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GradeRepository extends CrudRepository<Grade, Long> {
    @Query("select g from Grade as g " +
               "left join GradeName as gn on g.name = gn.id " +
               "where gn.value = :name " +
               "and g.schoolYear = :schoolYear")
    Optional<Grade> findByNameAndSchoolYear(
        @Param("name") String name,
        @Param("schoolYear") SchoolYear schoolYear);
}
