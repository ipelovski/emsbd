package sasj.controller.teacher.lesson;

import sasj.controller.Breadcrumb;
import sasj.data.lesson.Lesson;
import sasj.util.Util;
import sasj.controller.home.HomeUrls;
import sasj.web.UrlBuilder;
import sasj.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class TeacherLessonUrls {
    @Autowired
    private Util util;
    @Autowired
    private HomeUrls homeURLs;

    public String lessons() {
        return UrlBuilder.get(TeacherLessonController.class, WebMvcConfig.listName);
    }

    public String lesson(Lesson lesson) {
        return UrlBuilder.get(TeacherLessonController.class, WebMvcConfig.detailsName,
            WebMvcConfig.objectIdParamName, lesson.getId());
    }

    public String startLesson() {
        return UrlBuilder.get(TeacherLessonController.class, TeacherLessonController.start);
    }

    public String lessonsPerWeek(LocalDate date) {
        return new UrlBuilder(TeacherLessonController.class, WebMvcConfig.listName)
            .gatherNamedURIParams()
            .queryParam(TeacherLessonController.date, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .build();
    }

    public String setPresence() {
        return UrlBuilder.get(TeacherLessonController.class, TeacherLessonController.setPresence);
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
