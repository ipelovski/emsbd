package sasj.data.student;

import sasj.data.teacher.Teacher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Long> {
    @Query("" +
        "select s from Student s " +
        "left join User u on s.user = u.id " +
        "where u.personalInfo.firstName like %:name% " +
        "or u.personalInfo.middleName like %:name% " +
        "or u.personalInfo.lastName like %:name%")
    List<Student> findByName(String name);
    Iterable<Student> findByIdInOrderByNumberAsc(Iterable<Long> ids);
    Optional<Student> findByUserId(Long userId);
    List<Student> findBySchoolClassCoursesTeacher(Teacher teacher);
}
