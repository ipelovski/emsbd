package emsbj.student;

import emsbj.teacher.Teacher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Long> {
    @Query("" +
        "select s from Student s " +
        "inner join User u on s.user = u.id " +
        "where u.personalInfo.lastName = ?1")
    List<Student> findByLastName(String lastName);
    @Query("" +
        "select s from Student s " +
        "inner join User u on s.user = u.id " +
        "where u.personalInfo.firstName like %?1% " +
        "or u.personalInfo.middleName like %?1% " +
        "or u.personalInfo.lastName like %?1%")
    List<Student> findByName(String name);
    Iterable<Student> findByIdInOrderByNumberAsc(Iterable<Long> ids);
    Optional<Student> findByUserId(Long userId);
    List<Student> findBySchoolClassCoursesTeacher(Teacher teacher);
}
