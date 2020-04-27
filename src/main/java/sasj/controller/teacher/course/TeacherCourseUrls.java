package sasj.controller.teacher.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sasj.config.WebMvcConfig;
import sasj.controller.Breadcrumb;
import sasj.controller.home.HomeUrls;
import sasj.data.course.Course;
import sasj.util.Util;
import sasj.web.UrlBuilder;

@Service
public class TeacherCourseUrls {
    @Autowired
    private Util util;
    @Autowired
    private HomeUrls homeURLs;

    public String list() {
        return UrlBuilder.get(TeacherCourseController.class, WebMvcConfig.listName);
    }

    public String course(Course course) {
        return UrlBuilder.get(TeacherCourseController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, course.getId());
    }

    public String schedule() {
        return UrlBuilder.get(TeacherCourseController.class, TeacherCourseController.schedule);
    }

    public Breadcrumb courseBreadcrumb(Course course) {
        return new Breadcrumb(course(course), course.getSubject().getName().getValue(), scheduleBreadcrumb());
    }

    public Breadcrumb scheduleBreadcrumb() {
        return new Breadcrumb(schedule(), util.capitalize("class.schedule"), homeURLs.homeBreadcrumb());
    }
}
