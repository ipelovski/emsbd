package sasj.controller.student.course;

import sasj.controller.Breadcrumb;
import sasj.controller.student.home.StudentHomeUrls;
import sasj.util.Util;
import sasj.web.UrlBuilder;
import sasj.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentCourseUrls {
    @Autowired
    private Util util;
    @Autowired
    private StudentHomeUrls studentHomeUrls;

    public String list() {
        return UrlBuilder.get(StudentCourseController.class, WebMvcConfig.listName);
    }

    public String schedule() {
        return UrlBuilder.get(StudentCourseController.class, StudentCourseController.schedule);
    }

    public Breadcrumb listBreadcrumb() {
        return new Breadcrumb(list(), util.capitalize("course.courses"), studentHomeUrls.homeBreadcrumb());
    }

    public Breadcrumb scheduleBreadcrumb() {
        return new Breadcrumb(schedule(), util.capitalize("class.schedule"), studentHomeUrls.homeBreadcrumb());
    }
}
