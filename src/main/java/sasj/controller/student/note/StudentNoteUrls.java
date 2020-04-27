package sasj.controller.student.note;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sasj.config.WebMvcConfig;
import sasj.controller.Breadcrumb;
import sasj.controller.student.course.StudentCourseUrls;
import sasj.data.course.Course;
import sasj.data.student.Student;
import sasj.util.Util;
import sasj.web.UrlBuilder;

@Service
public class StudentNoteUrls {
    @Autowired
    private StudentCourseUrls studentCourseUrls;
    @Autowired
    private Util util;

    public String notes(Course course) {
        return new UrlBuilder(StudentNoteController.class, WebMvcConfig.listName)
            .gatherNamedURIParams()
            .queryParam(StudentNoteController.courseQueryParam, course.getId())
            .build();
    }

    public Breadcrumb listBreadcrumb(Course course) {
        return new Breadcrumb(notes(course),
            util.capitalize("student.notes"), studentCourseUrls.listBreadcrumb());
    }
}
