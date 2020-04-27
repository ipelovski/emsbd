package sasj.data.student;

import sasj.data.School;
import sasj.data.course.CourseRepository;
import sasj.controller.student.course.CourseStudent;
import sasj.data.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class StudentService {
    @Autowired
    private UserService userService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private School school;

    public Optional<Student> getCurrentStudent() {
        return userService.getCurrentUser()
            .flatMap(user -> studentRepository.findByUserId(user.getId()));
    }

    public List<CourseStudent> getStudentCourses(Student student) {
        return StreamSupport.stream(
            courseRepository.findAllBySchoolClassAndTerm(
                student.getSchoolClass(), school.getTerm()).spliterator(),
            false)
            .sorted(Comparator.comparing(course -> course.getSubject().getName().getValue()))
            .map(course -> new CourseStudent(course, student))
            .collect(Collectors.toList());

    }
}
