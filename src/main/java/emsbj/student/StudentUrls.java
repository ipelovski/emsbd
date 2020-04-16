package emsbj.student;

import emsbj.Breadcrumb;
import emsbj.config.WebMvcConfig;
import emsbj.course.Course;
import emsbj.course.CourseUrls;
import emsbj.home.HomeUrls;
import emsbj.web.UrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentUrls {
    @Autowired
    private HomeUrls homeURLs;
    @Autowired
    private CourseUrls courseURLs;

    public String teacherStudents() {
        return UrlBuilder.get(StudentController.class, StudentController.teacherStudents);
    }

    public String teacherCoursesStudents() {
        return UrlBuilder.get(StudentController.class, StudentController.teacherCoursesStudents);
    }

    public String searchStudent() {
        return UrlBuilder.get(StudentController.class, StudentController.searchStudent);
    }

    public String student(Student student) {
        return UrlBuilder.get(StudentController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, student.getId());
    }

    public String student(Student student, Course course) {
        return new UrlBuilder(StudentController.class, WebMvcConfig.detailsName)
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
