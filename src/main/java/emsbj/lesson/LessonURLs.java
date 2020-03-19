package emsbj.lesson;

import emsbj.Breadcrumb;
import emsbj.Util;
import emsbj.home.HomeURLs;
import emsbj.web.URLBuilder;
import emsbj.config.WebMvcConfig;
import emsbj.web.ViewInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class LessonURLs {
    @Autowired
    private Util util;
    @Autowired
    private HomeURLs homeURLs;

    public String lessons() {
        return URLBuilder.get(LessonController.class, WebMvcConfig.listName);
    }

    public String lesson(Lesson lesson) {
        return URLBuilder.get(LessonController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, lesson.getId());
    }

    public String startLesson() {
        return URLBuilder.get(LessonController.class, LessonController.start);
    }

    public String lessonsPerWeek(LocalDate date) {
        return new URLBuilder(LessonController.class, WebMvcConfig.listName)
            .gatherNamedURIParams()
            .queryParam(LessonController.date, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .build();
    }

    public String setPresence() {
        return URLBuilder.get(LessonController.class, LessonController.setPresence);
    }

    public Breadcrumb lessonsPerWeekBreadcrumb(LocalDate startDate) {
        String label = util.localize("lessonsPerWeek", startDate.toString(), startDate.plusDays(6).toString());
        return new Breadcrumb(lessonsPerWeek(startDate), label, homeURLs.indexBreadcrumb());
    }

    public Breadcrumb detailsBreadcrumb(Lesson lesson) {
        LocalDate startOfWeek = util.getStartOfWeek(lesson.getBegin());
        return new Breadcrumb(
            lesson(lesson),
            "Lesson in " + lesson.getCourse().getSubject().getName().getValue(),
            lessonsPerWeekBreadcrumb(startOfWeek)
        );
    }
}
