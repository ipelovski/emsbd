package emsbj.lesson;

import emsbj.web.URLBuilder;
import emsbj.config.WebMvcConfig;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class LessonURLs {
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
}
