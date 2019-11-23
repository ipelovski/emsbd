package emsbd;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Long> {
    @Query("" +
        "select s from Student s " +
        "inner join User u on s.user = u.id " +
        "inner join PersonalInfo p on p.user = u.id " +
        "where p.lastName = ?1")
    List<Student> findByLastName(String lastName);
    Optional<Student> findById(long id);
}
