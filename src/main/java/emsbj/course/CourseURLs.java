package emsbj.course;

import emsbj.Breadcrumb;
import emsbj.util.Util;
import emsbj.home.HomeURLs;
import emsbj.web.URLBuilder;
import emsbj.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseURLs {
    @Autowired
    private Util util;
    @Autowired
    private HomeURLs homeURLs;

    public String list() {
        return URLBuilder.get(CourseController.class, WebMvcConfig.listName);
    }

    public String course(Course course) {
        return URLBuilder.get(CourseController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, course.getId());
    }

    public String schedule() {
        return URLBuilder.get(CourseController.class, CourseController.schedule);
    }

    public Breadcrumb listBreadcrumb() {
        return new Breadcrumb(list(), util.capitalize("courses"), homeURLs.homeBreadcrumb());
    }

    public Breadcrumb courseBreadcrumb(Course course) {
        return new Breadcrumb(course(course), course.getSubject().getName().getValue(), scheduleBreadcrumb());
    }

    public Breadcrumb scheduleBreadcrumb() {
        return new Breadcrumb(schedule(), util.capitalize("schedule"), homeURLs.homeBreadcrumb());
    }
}
