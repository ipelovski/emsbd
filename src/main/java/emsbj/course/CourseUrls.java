package emsbj.course;

import emsbj.Breadcrumb;
import emsbj.util.Util;
import emsbj.home.HomeUrls;
import emsbj.web.UrlBuilder;
import emsbj.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseUrls {
    @Autowired
    private Util util;
    @Autowired
    private HomeUrls homeURLs;

    public String list() {
        return UrlBuilder.get(CourseController.class, WebMvcConfig.listName);
    }

    public String course(Course course) {
        return UrlBuilder.get(CourseController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, course.getId());
    }

    public String schedule() {
        return UrlBuilder.get(CourseController.class, CourseController.schedule);
    }

    public Breadcrumb listBreadcrumb() {
        return new Breadcrumb(list(), util.capitalize("course.courses"), homeURLs.homeBreadcrumb());
    }

    public Breadcrumb courseBreadcrumb(Course course) {
        return new Breadcrumb(course(course), course.getSubject().getName().getValue(), scheduleBreadcrumb());
    }

    public Breadcrumb scheduleBreadcrumb() {
        return new Breadcrumb(schedule(), util.capitalize("class.schedule"), homeURLs.homeBreadcrumb());
    }
}
