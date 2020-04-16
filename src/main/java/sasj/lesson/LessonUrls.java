package sasj.lesson;

import sasj.Breadcrumb;
import sasj.util.Util;
import sasj.home.HomeUrls;
import sasj.web.UrlBuilder;
import sasj.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class LessonUrls {
    @Autowired
    private Util util;
    @Autowired
    private HomeUrls homeURLs;

    public String lessons() {
        return UrlBuilder.get(LessonController.class, WebMvcConfig.listName);
    }

    public String lesson(Lesson lesson) {
        return UrlBuilder.get(LessonController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, lesson.getId());
    }

    public String startLesson() {
        return UrlBuilder.get(LessonController.class, LessonController.start);
    }

    public String lessonsPerWeek(LocalDate date) {
        return new UrlBuilder(LessonController.class, WebMvcConfig.listName)
            .gatherNamedURIParams()
            .queryParam(LessonController.date, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .build();
    }

    public String setPresence() {
        return UrlBuilder.get(LessonController.class, LessonController.setPresence);
    }

    public Breadcrumb lessonsPerWeekBreadcrumb(LocalDate startDate) {
        String label = util.localize("class.lessonsPerWeek", startDate.toString(), startDate.plusDays(6).toString());
        return new Breadcrumb(lessonsPerWeek(startDate), label, homeURLs.homeBreadcrumb());
    }

    public Breadcrumb detailsBreadcrumb(Lesson lesson) {
        LocalDate startOfWeek = util.getStartOfWeek(lesson.getBegin());
        return new Breadcrumb(
            lesson(lesson),
            util.capitalize("class.lessonIn", lesson.getCourse().getSubject().getName().getValue()),
            lessonsPerWeekBreadcrumb(startOfWeek)
        );
    }
}
