package emsbj.teacher;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeacherRepository extends CrudRepository<Teacher, Long> {
    @Query("select new emsbj.FormMaster(t, COUNT(sc.id) as schoolClasses," +
        " u.personalInfo.firstName as firstName," +
        " u.personalInfo.middleName as middleName," +
        " u.personalInfo.lastName as lastName)" +
        " from Teacher t" +
        " left join User u on t.user = u.id" +
        " left join SchoolClass sc on sc.formMaster = t.id" +
        " group by sc.formMaster")
    Iterable<FormMaster> findAllOrderByFormMaster(Sort sort);

    Optional<Teacher> findByUserId(Long userId);
}
