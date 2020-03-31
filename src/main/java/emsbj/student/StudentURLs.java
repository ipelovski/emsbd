package emsbj.student;

import emsbj.Breadcrumb;
import emsbj.config.WebMvcConfig;
import emsbj.course.Course;
import emsbj.course.CourseURLs;
import emsbj.home.HomeURLs;
import emsbj.web.URLBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentURLs {
    @Autowired
    private HomeURLs homeURLs;
    @Autowired
    private CourseURLs courseURLs;

    public String teacherStudents() {
        return URLBuilder.get(StudentController.class, StudentController.teacherStudents);
    }

    public String teacherCoursesStudents() {
        return URLBuilder.get(StudentController.class, StudentController.teacherCoursesStudents);
    }

    public String searchStudent() {
        return URLBuilder.get(StudentController.class, StudentController.searchStudent);
    }

    public String student(Student student) {
        return URLBuilder.get(StudentController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, student.getId());
    }

    public String student(Student student, Course course) {
        return new URLBuilder(StudentController.class, WebMvcConfig.detailsName)
            .gatherNamedURIParams()
            .namedURIParams(WebMvcConfig.objectIdParamName, student.getId())
            .queryParam(StudentController.course, course.getId())
            .build();
    }

    public Breadcrumb studentBreadcrumb(Student student, Course course) {
        return new Breadcrumb(student(student, course),
            student.getUser().getPersonalInfo().getName(),
            courseURLs.courseBreadcrumb(course));
    }
}
