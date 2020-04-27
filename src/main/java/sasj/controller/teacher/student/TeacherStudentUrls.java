package sasj.controller.teacher.student;

import sasj.controller.Breadcrumb;
import sasj.config.WebMvcConfig;
import sasj.controller.teacher.course.TeacherCourseUrls;
import sasj.data.course.Course;
import sasj.controller.home.HomeUrls;
import sasj.data.student.Student;
import sasj.web.UrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherStudentUrls {
    @Autowired
    private HomeUrls homeURLs;
    @Autowired
    private TeacherCourseUrls teacherCourseUrls;

    public String teacherStudents() {
        return UrlBuilder.get(TeacherStudentController.class, TeacherStudentController.teacherStudents);
    }

    public String teacherCoursesStudents() {
        return UrlBuilder.get(TeacherStudentController.class, TeacherStudentController.teacherCoursesStudents);
    }

    public String searchStudent() {
        return UrlBuilder.get(TeacherStudentController.class, TeacherStudentController.searchStudent);
    }

    public String student(Student student) {
        return UrlBuilder.get(TeacherStudentController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, student.getId());
    }

    public String student(Student student, Course course) {
        return new UrlBuilder(TeacherStudentController.class, WebMvcConfig.detailsName)
            .gatherNamedURIParams()
            .namedURIParams(WebMvcConfig.objectIdParamName, student.getId())
            .queryParam(TeacherStudentController.course, course.getId())
            .build();
    }

    public Breadcrumb studentBreadcrumb(Student student, Course course) {
        return new Breadcrumb(student(student, course),
            student.getUser().getPersonalInfo().getName(),
            teacherCourseUrls.courseBreadcrumb(course));
    }
}
