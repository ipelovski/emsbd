package emsbj;

import emsbj.config.WebMvcConfig;
import org.springframework.stereotype.Service;

@Service
public class CourseURLs {
    public String course(Course course) {
        return URLBuilder.get(CourseController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, course.getId());
    }

    public String schedule() {
        return URLBuilder.get(CourseController.class, CourseController.schedule);
    }
}
